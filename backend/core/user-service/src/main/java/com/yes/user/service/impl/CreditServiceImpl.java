package com.yes.user.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yes.common.config.security.SecurityUtils;
import com.yes.common.config.security.UserLogin;
import com.yes.common.domain.entity.User;
import com.yes.common.domain.vo.PageVo;
import com.yes.common.enums.ErrorCode;
import com.yes.common.exception.SystemException;
import com.yes.common.utils.BeanCopyUtils;
import com.yes.common.utils.ResponseResult;
import com.yes.user.domain.dto.CreditDto;
import com.yes.user.domain.dto.QuotaChargeDto;
import com.yes.user.domain.entity.Credit;
import com.yes.user.domain.vo.CreditVo;
import com.yes.user.mapper.CreditMapper;
import com.yes.user.mapper.UserMapperN;
import com.yes.user.service.CreditService;
import com.yes.user.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 叶苗
* @description 针对表【credit】的数据库操作Service实现
* @createDate 2025-03-07 20:39:35
*/
@Service
public class CreditServiceImpl extends ServiceImpl<CreditMapper, Credit>
    implements CreditService {
    @Autowired
    UserMapperN userMapperN;
    /**
     * 新增令牌信息
     * @param dto
     * @return
     */
    @Override
    public ResponseResult saveCredit(CreditDto dto) {
        UserLogin loginUser = SecurityUtils.getLoginUser();
        //生成ak，sk
        String accessKey = UserUtils.encode(loginUser.getUsername() + RandomUtil.randomNumbers(5));
        String secretKey = DigestUtil.md5Hex(loginUser.getUsername() + RandomUtil.randomNumbers(8));
        Long userId = loginUser.getUser().getId();
        Credit credit = BeanCopyUtils.copyBean(dto, Credit.class);
        credit.setSecretKey(secretKey);
        credit.setUserId(loginUser.getUser().getId());
        //将令牌信息存储在credit表中
        userMapperN.saveCredit(credit);
        //将用户令牌信息表存储到user_credit中


        return ResponseResult.okResult();
    }

    /**
     * 分页查询credit
     * @param
     * @return
     */
    @Override
    public ResponseResult pageCredit(CreditDto dto) {
        dto.setUserId(SecurityUtils.getUserId());
        dto.setPageNum(dto.getPageNum()-1);
        List<CreditVo> creditVos = userMapperN.pageListCredit(dto);
        Long total = userMapperN.getTotalUserCount(dto.getUserId());
        return ResponseResult.okResult(new PageVo(creditVos, total));
    }

    @Override
    public ResponseResult delete(Long id) {
        try {
            removeById(id);
        }
        catch (Exception e){
            log.error(e.toString());
            throw new SystemException(ErrorCode.SYSTEM_ERROR);
        }
        return ResponseResult.okResult();
    }

    /**
     * 修改状态
     * @param id
     * @return
     */

    @Override
    public ResponseResult updateStatus(Long id, Integer status) {
        userMapperN.forbid(id,status);
        return ResponseResult.okResult();
    }

    /**
     * 给令牌充值
     * @param
     * @return
     */
    @Override
    public ResponseResult handleRecharge(QuotaChargeDto dto) {
        //获取令牌信息
        Credit credit = getById(dto.getCreditId());
        //获取用户信息
        User user = userMapperN.selectById(SecurityUtils.getUserId());
        //进行判断用户余额
        if (user.getQuota() < dto.getQuota()){
            throw new SystemException(ErrorCode.QUOTA_NOT_ENOUGH);
        }
        //进行额度的扣减
        user.setQuota(user.getQuota()-dto.getQuota());
        userMapperN.updateById(user);
        userMapperN.charRemainQuota(dto.getCreditId(),dto.getQuota());
        return ResponseResult.okResult();
    }


}




