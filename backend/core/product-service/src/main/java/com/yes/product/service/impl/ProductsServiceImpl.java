package com.yes.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yes.common.domain.vo.PageVo;
import com.yes.common.utils.BeanCopyUtils;
import com.yes.common.utils.ResponseResult;
import com.yes.product.domain.dto.ProductPageDto;
import com.yes.common.domain.entity.Products;
import com.yes.product.domain.vo.ProductVo;
import com.yes.product.mapper.ProductsMapper;
import com.yes.product.service.ProductsService;
;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 叶苗
* @description 针对表【products】的数据库操作Service实现
* @createDate 2025-03-11 12:42:13
*/
@Service
public class ProductsServiceImpl extends ServiceImpl<ProductsMapper, Products>
    implements ProductsService{
    @Autowired
    ProductsMapper productsMapper;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate; // 注入 SimpMessagingTemplate
    /**
     * 获取商品列表
     * @return
     */
    @Override
    public ResponseResult pageProducts(ProductPageDto dto) {
        dto.setPageNum(dto.getPageNum()-1);
        List<Products> products = productsMapper.pageProducts(dto);
        Integer total =  productsMapper.productsAll(dto).size();
        return ResponseResult.okResult(new PageVo(products,total.longValue()));
    }

    /**w
     * 新增商品
     * @param products
     * @return
     */
    @Override
    public ResponseResult saveProducts(Products products) {
        save(products);
        simpMessagingTemplate.convertAndSend("/pd/pushProduct",  '1');

        return ResponseResult.okResult();
    }

    /**
     * 编辑商品
     * @param products
     * @return
     */
    @Override
    public ResponseResult editProducts(Products products) {
        updateById(products);
        simpMessagingTemplate.convertAndSend("/pd/pushProduct", '1');

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getProducts(Integer isHot) {
        LambdaQueryWrapper<Products> queryWrapper =  new LambdaQueryWrapper<>();
        queryWrapper.eq(isHot!=null,Products::getIsHot,isHot);
        List<Products> list = list(queryWrapper);
        return ResponseResult.okResult(BeanCopyUtils.copyBeanList(list,ProductVo.class));
    }

    /**
     * 删除商品
     * @param id
     * @return
     */
    @Override
    public ResponseResult deleteProducts(Long id) {
        productsMapper.logicDeleteProducts(id);
        simpMessagingTemplate.convertAndSend("/pd/pushProduct", '1');

        return ResponseResult.okResult();
    }
}




