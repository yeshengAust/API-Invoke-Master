package com.yes.buy.service.serviceImpl;

import com.yes.buy.constant.BuyConstants;
import com.yes.buy.domain.dto.BuyProductDto;
import com.yes.buy.service.BuyService;
import com.yes.buy.utils.TokenBucketService;
import com.yes.common.config.security.SecurityUtils;
import com.yes.common.constants.SystemConstants;
import com.yes.common.domain.entity.Log;
import com.yes.common.domain.entity.Products;
import com.yes.common.domain.entity.User;
import com.yes.common.enums.ErrorCode;
import com.yes.common.exception.SystemException;
import com.yes.common.service.inner.InnerProductsService;
import com.yes.common.service.inner.InnerUserService;
import com.yes.common.utils.RedisCache;
import com.yes.common.utils.ResponseResult;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.dubbo.config.Constants;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static com.yes.common.enums.ErrorCode.PRODUCT_NOT_ENOUGH;

@Service
public class BuyServiceImpl implements BuyService {
    @DubboReference(check = false)
    InnerUserService userService;
    @DubboReference(check = false)
    InnerProductsService productsService;
    @Autowired
    RedisCache redisCache;
    @Autowired
    TokenBucketService tokenBucketService;


    /**
     * 购买商品
     *
     * @param dto
     * @return
     */
    @Override
    @GlobalTransactional(name = "buy")
    public ResponseResult buy(BuyProductDto dto) {
        String key = BuyConstants.LOCK_BUY_KEY + dto.getUserId();
        boolean tryLock = false;
        try {
            // 通过dubbo获取用户的信息
            User user = userService.getUserById(dto.getUserId());
            // 通过dubbo获取商品的信息
            Products products = productsService.getProductsById(dto.getProductId());

            // 对热门商品进行限流，10s只能买1次
            if (SystemConstants.IS_HOT.equals(products.getIsHot())) {

                String hotKey = "buy:" + dto.getUserId() + ":" + dto.getProductId();
                boolean tryBucket = tokenBucketService.tryAcquire(hotKey, 1, 1.0/(24*3600), 1);
                if (!tryBucket) {
                    throw new SystemException("该商品一天仅限一份哦~");
                }
            }

            // 同一时间一个用户只能操作一次
            tryLock = redisCache.tryLock(key, 10, TimeUnit.SECONDS);
            if (!tryLock) {
                throw new SystemException(ErrorCode.OPTIONS_TOO_QUICK);
            }

            // 检查用户和商品是否存在
            checkUserAndProductExists(user, products);
            // 检查余额是否充足
            checkWalletBalance(user, products);
            // 检查商品数量是否足够
            checkProductStock(products);

            // 更新数据
            user.setWallet(user.getWallet().subtract(products.getPrice()));
            user.setQuota(user.getQuota() + products.getQuota());
            products.setCount(products.getCount() - 1);

            // 数据有效性验证
            if (user.getWallet().compareTo(BigDecimal.ZERO) < 0) {
                throw new SystemException(ErrorCode.WALLET_NOT_ENOUGH);
            }
            if (products.getCount() < 0) {
                throw new SystemException(ErrorCode.PRODUCT_NOT_ENOUGH);
            }

            // 调用更新的方法
            userService.updateUser(user);
            if (SystemConstants.IS_HOT.equals(products.getIsHot())) {
                productsService.updateProducts(products);
            }

            return ResponseResult.okResult();
        } catch (Exception e) {
            // 统一异常处理，可根据具体业务需求进行日志记录和错误返回
            throw e;
        } finally {
            if (tryLock) {
                redisCache.unlock(key);
            }
        }
    }

    private void checkUserAndProductExists(User user, Products products) {
        if (user == null) {
            throw new SystemException(ErrorCode.ACCOUNT_NOT_EXIST);
        }
        if (products == null) {
            throw new SystemException(ErrorCode.PRODUCT_NOT_EXIST);
        }
    }

    private void checkWalletBalance(User user, Products products) {
        if (user.getWallet().compareTo(products.getPrice()) < 0) {
            // 余额不足
            throw new SystemException(ErrorCode.WALLET_NOT_ENOUGH);
        }
    }

    private void checkProductStock(Products products) {
        if (products.getCount() <= 0 && SystemConstants.IS_HOT.equals(products.getIsHot())) {
            throw new SystemException(ErrorCode.PRODUCT_NOT_ENOUGH);
        }
    }
}
