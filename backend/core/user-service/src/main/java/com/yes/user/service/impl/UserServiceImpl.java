package com.yes.user.service.impl;

import cn.hutool.core.util.RandomUtil;

import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yes.common.constants.SystemConstants;
import com.yes.user.config.rabbitmq.RabbitMqExAndQu;
import com.yes.common.config.security.SecurityUtils;
import com.yes.common.config.security.UserLogin;
import com.yes.common.domain.dto.CacheDto;
import com.yes.common.domain.dto.PageDto;
import com.yes.common.domain.entity.User;
import com.yes.common.domain.vo.PageVo;
import com.yes.common.enums.ErrorCode;
import com.yes.common.exception.SystemException;
import com.yes.common.service.inner.InnerRoleService;
import com.yes.user.domain.vo.UserLoginVo;
import com.yes.user.mapper.UserMapperN;
import com.yes.common.utils.*;
import com.yes.user.constants.UserConstants;
import com.yes.user.domain.dto.*;
import com.yes.user.domain.vo.UserInfoVo;
import com.yes.user.domain.vo.VerifyCodeDto;
import com.yes.user.service.UserService;
import com.yes.user.utils.OssUploadUtil;
import com.yes.user.utils.UserUtils;
import com.yes.user.utils.VerificationCodeUtil;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


/**
 * @author 叶苗
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2025-02-25 14:24:38
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapperN, User>
        implements UserService {
    @DubboReference(check = false)
    InnerRoleService roleService;

    private final RedisCache redisCache;

    private final AuthenticationManager authenticationManager;

    private final RabbitTemplate rabbitTemplate;

    private final UserMapperN userMapperN;


    private final OssUploadUtil ossUploadUtil;

    private final RedissonClient redissonClient;

    /**
     * 获取验证码
     */
    @Override
    public ResponseResult getCaptcha() {
        VerificationCodeUtil codeUtil = new VerificationCodeUtil();
        BufferedImage image = codeUtil.getImage();


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpeg", baos);
        } catch (Exception e) {
            throw new SystemException(ErrorCode.SYSTEM_ERROR);
        }
        byte[] imageBytes = baos.toByteArray();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        String text = codeUtil.getText().toLowerCase();

        //发送mq
        String uuid = UUID.randomUUID().toString();
        VerifyCodeDto verifyCodeDto;
        try {
            //开启mq将text存储到redis中
            String key = UserConstants.LOGIN_USER_CODE_CACHE + uuid;
            CacheDto cacheDto = new CacheDto(key, text);
            //发送rabbitmq
            rabbitTemplate.convertAndSend(RabbitMqExAndQu.REDIS_EXCHANGE, RabbitMqExAndQu.VERIFY_CODE_RK, cacheDto);
            verifyCodeDto = new VerifyCodeDto(base64Image, key);
        } catch (Exception e) {
            throw new SystemException(ErrorCode.SYSTEM_ERROR);
        }
        // 封装到统一响应返回体中

        return ResponseResult.okResult(verifyCodeDto);
    }

    /**
     * 用户登录接口
     *
     * @param userLoginDto
     * @return
     */
    @Override
    public ResponseResult login(UserLoginDto userLoginDto) {
        //判断参数是否合法
        if (Objects.isNull(userLoginDto.getAccount())) {
            throw new SystemException(ErrorCode.REQUIRE_ACCOUNT);
        }
        if (StringUtils.isEmpty(userLoginDto.getAccount())) {
            throw new SystemException(ErrorCode.ACCOUNT_NOT_NULL);
        }
        if (StringUtils.isEmpty(userLoginDto.getPassword())) {
            throw new SystemException(ErrorCode.PASSWORD_NOT_NULL);
        }
        //从redis中拿用户数据
        User user = redisCache.getCacheObject(UserConstants.USER_CACHE+userLoginDto.getAccount());
        //redis中没数据
        if (Objects.isNull(user)) {
           //获取分布式锁
            RLock lock = redissonClient.getLock(UserConstants.USER_LOGIN_LOCK+userLoginDto.getAccount());
            lock.lock();
            try {
                //爽查询
                 user = redisCache.getCacheObject(UserConstants.USER_CACHE + userLoginDto.getAccount());
                if (Objects.isNull(user)) {
                    //从数据库中查询
                    user = validate(userLoginDto);
                    if(Objects.isNull(user)){
                        throw  new SystemException(ErrorCode.ACCOUNT_NOT_EXIST);
                    }
                    //将用户信息缓存到redis中
                    redisCache.setCacheObject(UserConstants.USER_CACHE+user.getId(), new UserLogin(user), UserConstants.USER_INFO_EXPIRE_TIME, UserConstants.USER_INFO_EXPIRE_UNIT);
                }
            }
            catch (SystemException e){
                throw e;
            }
            finally {
                lock.unlock();
            }


        }

        //获取验证码
        String code =  redisCache.getCacheObject(userLoginDto.getPrefix());
        if (StringUtils.isEmpty(code) || !StringUtils.equals(code, userLoginDto.getVerify().toLowerCase())) {
            throw new SystemException(ErrorCode.VERIFY_CODE_ERROR);
        }
        // 进行用户的校验，通过security框架提供的ProviderManager
        //将用户信息封装到Authentication中进行运输
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userLoginDto.getAccount(), userLoginDto.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (Objects.isNull(authenticate)) {
            throw new SystemException(ErrorCode.ACCOUNT_OR_PASSWORD_ERROR);
        }
        //获取认证后的用户信息
        UserLogin principal = (UserLogin) authenticate.getPrincipal();
        //将数据缓存到redis中（异步）
//        try {
//            redisCache.setCacheObject(UserConstants.USER_CACHE+principal.getUser().getId(), principal, SystemConstants.USER_INFO_EXPIRE_TIME,SystemConstants.EXPIRE_UNIT);
//        }
//        catch (Exception e) {
//            throw new SystemException(ErrorCode.SYSTEM_ERROR);
//        }
        try {
            //开启mq将text存储到redis中
            String key = UserConstants.USER_LOGIN_CACHE + principal.getUser().getId();
            CacheDto cacheDto = new CacheDto(key, principal);
            //发送rabbitmq
//            rabbitTemplate.convertAndSend("user_info_exg", "user_info", cacheDto);
        } catch (Exception e) {
            throw new SystemException(ErrorCode.SYSTEM_ERROR);
        }

        //生成token
        String token = JwtUtil.createJWT(principal.getUser().getId().toString());
        //通过用户id获取用户
        String roleKey = roleService.getUserRole(principal.getUser().getId());
        return ResponseResult.okResult(new UserLoginVo(token, roleKey));
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    @Override
    public ResponseResult getUserInfo() {
        //从springSecurity上下文中获取用户信息
        UserLogin loginUser = SecurityUtils.getLoginUser();
        //将用户信息封装到vo类中
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(loginUser.getUser(), UserInfoVo.class);
        return ResponseResult.okResult(userInfoVo);
    }

    /**
     * 修改用户密码
     *
     * @param updatePasswordDto
     * @return
     */
    @Override
    public ResponseResult updatePassword(UserUpdatePasswordDto updatePasswordDto) {
        UserLogin loginUser = SecurityUtils.getLoginUser();
        Long id = loginUser.getUser().getId();
        //查询用户数据
        User user = getById(id);
        //校验旧密码是否正确
        validateNewOldPass(updatePasswordDto, user);
        //修改密码
        user.setPassword(UserUtils.encode(updatePasswordDto.getNewPassword()));
        boolean success = true;
        try {
            updateById(user);
        } catch (Exception e) {
            throw new SystemException(ErrorCode.SYSTEM_ERROR);
        }
        if (!success) {
            throw new SystemException(ErrorCode.SYSTEM_ERROR);
        }
        return ResponseResult.okResult();
    }

    /**
     * 保存用户
     *
     * @param saveUserDto
     * @return
     */
    @Override
    public ResponseResult saveUser(SaveUserDto saveUserDto) {
        //将用户信息封装到实体类
        User user = BeanCopyUtils.copyBean(saveUserDto, User.class);
        //查看用户是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getAccount, user.getAccount());
        User one = getOne(queryWrapper);
        if (one != null) {
            throw new SystemException(ErrorCode.ACCOUNT_ALREADY_EXISTS);
        }
        //生成ak，sk并且加密密码
        String encodePass = UserUtils.encode(saveUserDto.getPassword());
        String accessKey = UserUtils.encode(saveUserDto.getAccount() + RandomUtil.randomNumbers(5));
        user.setAccessKey(accessKey);
        user.setPassword(encodePass);
        //插入
        Integer userId = 0;
        try {
            save(user);
        } catch (Exception e) {
            log.error(e.toString());
            throw new SystemException(ErrorCode.SYSTEM_ERROR);
        }
        //插入用户角色信息
        userMapperN.saveUserRoleInfo(user.getId(), UserConstants.USER_ROLE_ID);
        return ResponseResult.okResult();
    }

    /**
     * 分页查询用户信息
     *
     * @param pageDto
     * @return
     */
    @Override
    public ResponseResult pageList(PageDto pageDto) {
        //配置分页信息
        Page<User> page = new Page<>(pageDto.getPageNum(), pageDto.getPageSize());
        page(page);
        List<UserInfoVo> userInfoVos = BeanCopyUtils.copyBeanList(page.getRecords(), UserInfoVo.class);
        return ResponseResult.okResult(new PageVo(userInfoVos, page.getTotal()));
    }

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    @Override
    @GlobalTransactional
    public ResponseResult delete(Long id) {
        try {
            removeById(id);
        } catch (Exception e) {
            throw new SystemException(ErrorCode.SYSTEM_ERROR);
        }
        System.out.println(1/0);
        return ResponseResult.okResult();
    }

    /**
     * 更新用户
     *
     * @param userDto
     * @return
     */
    @Override
    public ResponseResult updateUser(UpdateUserDto userDto) {
        try {
            updateById(BeanCopyUtils.copyBean(userDto, User.class));
        } catch (Exception e) {
            throw new SystemException(ErrorCode.SYSTEM_ERROR);
        }
        return ResponseResult.okResult();

    }

    /**
     * 用户注册接口
     *
     * @param user
     * @return
     */
    @Override
    public ResponseResult registry(UserLoginDto user) {
        //获取验证码
        String verify = user.getVerify();
        //获取key
        String key = UserConstants.REGISTRY_EMAIL_CODE_CACHE + user.getEmail();
        String value = (String) redisCache.getCacheObject(key);
        if (!StringUtils.equals(user.getVerify(), value)) {
            throw new SystemException(ErrorCode.VERIFY_CODE_ERROR);
        }
        //将用户信息写入数据库
        saveUser(BeanCopyUtils.copyBean(user, SaveUserDto.class));
        return ResponseResult.okResult();
    }

    /**
     * 邮箱注册
     *
     * @param email
     * @return
     */
    @Override
    public ResponseResult sendEmail(String email) {
        //调用mq
        String key = UserConstants.REGISTRY_EMAIL_CODE_CACHE + email;
        VerificationCodeUtil verificationCodeUtil = new VerificationCodeUtil();
        verificationCodeUtil.getImage();
        CacheDto cacheDto = new CacheDto(key, verificationCodeUtil.getText());
        try {
            rabbitTemplate.convertAndSend(RabbitMqExAndQu.EMAIL_EXCHANGE, RabbitMqExAndQu.EMAIL_RK, cacheDto);
        } catch (Exception e) {
            throw new SystemException(ErrorCode.SYSTEM_ERROR);
        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult uploadFile(MultipartFile img) {
        String url = ossUploadUtil.uploadImg(img);
        //将头像地址存入数据库
        try {
            userMapperN.updateUserAvatar(url, SecurityUtils.getUserId());
        } catch (Exception e) {
            log.error(e.toString());
            throw new SystemException(ErrorCode.SYSTEM_ERROR);
        }
        //清掉redis中的数据
        User user = getById(SecurityUtils.getUserId());
        UserLogin userLogin = new UserLogin(user);
        redisCache.setCacheObject(UserConstants.USER_CACHE + user.getId(), userLogin, UserConstants.USER_INFO_EXPIRE_TIME, UserConstants.USER_INFO_EXPIRE_UNIT);
        return ResponseResult.okResult(url);
    }

    /**
     * 检查qq是否已经存在
     *
     * @param qq
     * @return
     */
    @Override
    public ResponseResult checkQQ(String qq) {
        //判断是不是有qq存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, qq);
        int count = count(queryWrapper);
        return ResponseResult.okResult(count > 0 ? false : true);
    }



    private void validateNewOldPass(UserUpdatePasswordDto updatePasswordDto, User user) {
        if (!UserUtils.match(updatePasswordDto.getOldPassword(), user.getPassword())) {
            throw new SystemException(ErrorCode.OLD_PASSWORD_ERROR);
        }
    }

    private User validate(UserLoginDto user) {


        //通过账号查询数据库
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        //通过用户名查询
        queryWrapper.eq(User::getUsername, user.getAccount());
        User one = getOne(queryWrapper);
        //判断用户是否存在
        if (Objects.isNull(one)) {
            throw new SystemException(ErrorCode.ACCOUNT_NOT_EXIST);
        }
        //判断密码是否正确
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean pwdMatch = encoder.matches(user.getPassword(), one.getPassword());
        if (!pwdMatch) {
            throw new SystemException(ErrorCode.PASSWORD_ERROR);
        }
        return one;
    }
}




