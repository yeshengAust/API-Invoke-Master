package com.yes.product.controller;

import com.yes.common.utils.ResponseResult;
import com.yes.product.domain.dto.ProductPageDto;
import com.yes.common.domain.entity.Products;
import com.yes.product.service.ProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    ProductsService productsService;
    @GetMapping("/page")
    ResponseResult getProducts(ProductPageDto dto){
        return productsService.pageProducts(dto );
    }
//    @GetMapping("")
//    ResponseResult getProducts(Integer isHot){
//        return productsService.getProducts(isHot );
//    }
    @DeleteMapping
    ResponseResult deleteProducts(Long id){
        return productsService.deleteProducts(id);
    }

    @PostMapping
    ResponseResult saveProducts(@RequestBody Products products){
        return productsService.saveProducts(products);
    }
    @PutMapping
    ResponseResult editProducts(@RequestBody Products products){
        return productsService.editProducts(products);
    }
}
