package com.yes.product.service.impl.inner;

import com.yes.common.domain.entity.Products;
import com.yes.common.service.inner.InnerProductsService;
import com.yes.product.mapper.ProductsMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import javax.annotation.Resource;

@DubboService
public class InnerProductsServiceImpl implements InnerProductsService {
    @Resource
    ProductsMapper productsMapper;
    @Resource
    private SimpMessagingTemplate simpMessagingTemplate; // 注入 SimpMessagingTemplate

    @Override
    public Products getProductsById(Long id) {
        Products products = productsMapper.selectById(id);
        return products;
    }

    @Override
    public void updateProducts(Products products) {
        productsMapper.updateById(products);
        //通知前端进行更新
        simpMessagingTemplate.convertAndSend("/pd/pushProduct",  '1');

    }
}
