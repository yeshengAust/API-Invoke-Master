package com.yes.product.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yes.common.utils.ResponseResult;
import com.yes.product.domain.dto.ProductPageDto;
import com.yes.common.domain.entity.Products;

/**
* @author 叶苗
* @description 针对表【products】的数据库操作Service
* @createDate 2025-03-11 12:42:13
*/
public interface ProductsService extends IService<Products> {

    ResponseResult pageProducts(ProductPageDto dto);

    ResponseResult saveProducts(Products products);


    ResponseResult editProducts(Products products);

//    ResponseResult getProducts(Integer isHot);

    ResponseResult deleteProducts(Long id);
}
