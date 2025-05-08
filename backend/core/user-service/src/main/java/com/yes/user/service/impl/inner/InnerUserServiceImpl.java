package com.yes.user.service.impl.inner;

import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yes.common.constants.SystemConstants;
import com.yes.common.domain.entity.InterfaceInfo;
import com.yes.common.domain.entity.LogTransport;
import com.yes.common.domain.entity.User;
import com.yes.common.domain.entity.UserCreditInfo;
import com.yes.common.enums.ErrorCode;
import com.yes.common.exception.SystemException;
import com.yes.common.mapper.UserMapper;
import com.yes.common.service.inner.InnerCreditInterfaceInfoService;
import com.yes.common.service.inner.InnerInterfaceInfoService;
import com.yes.common.service.inner.InnerUserService;
import com.yes.common.utils.RedisCache;
import com.yes.user.config.rabbitmq.RabbitMqExAndQu;
import com.yes.user.mapper.UserMapperN;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@DubboService(group = "api", interfaceClass = InnerUserService.class)
public class InnerUserServiceImpl implements InnerUserService {
    @Resource
    UserMapper userMapper;
    @Resource
    UserMapperN userMapperN;
    @Resource
    RabbitTemplate rabbitTemplate;
    @Resource
    RedisCache redisCache;


    @DubboReference
    InnerCreditInterfaceInfoService innerCreditInterfaceInfoService;

    @Override
    public User getInvokeUser(String accessKey) {
        if (StringUtils.isAnyBlank(accessKey)) {
            throw new SystemException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getAccessKey, accessKey);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public User getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        return user;
    }

    @Override
    public void updateUser(User user) {
        userMapper.updateById(user);
    }

    @Override
    public void updateUserAllQuota(Long userId, Long cost) {
        userMapperN.updateUserAllQuota(userId, cost);
    }

    @Override
    @GlobalTransactional(name = "invokeOk")
    public void invokeInterfaceOk(UserCreditInfo userCreditInfo, InterfaceInfo interfaceInfo, LogTransport log) {
//        String lockKey = "invoke_lock:" + userCreditInfo.getUserId() + ":" + interfaceInfo.getId();

        try {
            //扣减响应令牌的剩余额度
            userMapperN.updateCreditRemain(userCreditInfo.getCreditId(), interfaceInfo.getCost());
            //添加用户接口调用次数
            innerCreditInterfaceInfoService.updateUserInterfaceInvokeCount(userCreditInfo.getUserId(), interfaceInfo.getId());
            //添加总的调用次数
            redisCache.incrementCacheMapValue(SystemConstants.INVOKE_COUNT_PREFIX, String.valueOf(interfaceInfo.getId()), 1);
            //添加用户的总调用额度
            userMapperN.updateUserAllQuota(userCreditInfo.getUserId(), interfaceInfo.getCost());

            rabbitTemplate.convertAndSend(RabbitMqExAndQu.LOG_EXCHANGE, RabbitMqExAndQu.LOG_RK, log);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
