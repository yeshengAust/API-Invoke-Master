package com.yes.buy.service.serviceImpl;

import com.yes.buy.domain.dto.BuyProductDto;
import com.yes.buy.service.BuyService;
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
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.yes.common.enums.ErrorCode.PRODUCT_NOT_ENOUGH;

@Service
public class BuyServiceImpl implements BuyService {
    @DubboReference(check = false)
    InnerUserService userService;
    @DubboReference(check = false)
    InnerProductsService productsService;
    @Autowired
    RedisCache redisCache;


    /**
     * 购买商品
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult buy(BuyProductDto dto) {
        //通过dubbo获取用户的信息
        User user = userService.getUserById(dto.getUserId());
        //通过dubbo获取商品的信
        Products products = productsService.getProductsById(dto.getProductId());
        //判断用户是否有钱
        if (user == null) {
            throw new SystemException(ErrorCode.ACCOUNT_NOT_EXIST);
        }
        if (products == null) {
            throw new SystemException(ErrorCode.PRODUCT_NOT_EXIST);
        }
        //判断余额是否充裕
        if (user.getWallet().compareTo(products.getPrice()) < 0) {
            // 余额不足
            throw new SystemException(ErrorCode.WALLET_NOT_ENOUGH);
        }
        //判断商品数量是否足够
        if (products.getCount() <= 0 && SystemConstants.IS_HOT.equals(products.getIsHot())) {
            throw new SystemException(ErrorCode.PRODUCT_NOT_ENOUGH);
        }
        //更新数据
        user.setWallet(user.getWallet().subtract(products.getPrice()));
        user.setQuota(user.getQuota() + products.getQuota());

        products.setCount(products.getCount() - 1);

        //调用更新的方法
        userService.updateUser(user);
        if (SystemConstants.IS_HOT.equals(products.getIsHot())) {
            productsService.updateProducts(products);
        }
        return ResponseResult.okResult();
    }
}
