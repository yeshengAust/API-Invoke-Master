package com.yes.user.service.impl;

import com.yes.common.config.security.SecurityUtils;
import com.yes.common.domain.entity.User;

import com.yes.common.utils.ResponseResult;
import com.yes.user.domain.dto.ChargeDto;
import com.yes.user.mapper.UserMapperN;
import com.yes.user.service.WalletService;
import org.apache.catalina.security.SecurityUtil;
import org.apache.dubbo.config.annotation.DubboReference;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletServiceImpl implements WalletService {
    /**
     * 获取用户钱包信息
     * @return
     */
    @Autowired
    UserMapperN userMapperN;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public ResponseResult getWallet(Long userId) {
        //获取用户余额
        User user = userMapperN.selectById(SecurityUtils.getUserId());
        BigDecimal wallet = user.getWallet();
        return ResponseResult.okResult(wallet);
    }

    /**
     * 充钱
     * @param dto
     * @return
     */
    @Override
    public ResponseResult charge(ChargeDto dto) {
        // todo进行支付,调用微信支付

        //支付成功后进行修改用户信息
        userMapperN.charge(dto);
        //添加用户总充值
        userMapperN.updateUserAllWallet(dto.getUserId(),dto.getAmount());
        return ResponseResult.okResult();
    }

    /**
     * 获取额度
     * @return
     */
    @Override
    public ResponseResult getQuota() {
        User user = userMapperN.selectById(SecurityUtils.getUserId());
        return ResponseResult.okResult(user.getQuota());
    }

}
