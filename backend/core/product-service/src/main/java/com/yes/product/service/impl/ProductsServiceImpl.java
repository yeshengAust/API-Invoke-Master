package com.yes.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yes.common.domain.vo.PageVo;
import com.yes.common.utils.BeanCopyUtils;
import com.yes.common.utils.RedisCache;
import com.yes.common.utils.ResponseResult;
import com.yes.product.domain.dto.ProductPageDto;
import com.yes.common.domain.entity.Products;
import com.yes.product.domain.vo.ProductVo;
import com.yes.product.mapper.ProductsMapper;
import com.yes.product.service.ProductsService;
;
import com.yes.product.utils.ProductCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.yes.product.constants.ProductConstants.CACHE_PRODUCT_KEY;

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



    @Autowired
    private ProductCache productCache;
    /**
     * 获取商品列表
     * @return
     */
    @Override
    public ResponseResult pageProducts(ProductPageDto dto) {
        //进行参数判断
        //先从redis中拿
        List<Products> cacheProduct = productCache.getProducts(dto.getPageNum(),dto.getPageSize());
        cacheProduct =  cacheProduct.stream().filter(product -> product.getIsHot().equals(dto.getIsHot())).collect(Collectors.toList());

        if(Objects.isNull(cacheProduct) || cacheProduct.size()==0){
            //从数据库查
            List<Products> products = productsMapper.selectList(null);
            Integer total =  productsMapper.productsAll(dto).size();
            productCache.setProducts();
            return ResponseResult.okResult(new PageVo(products,total.longValue()));
        }
        Long total = productCache.countProducts();

        return ResponseResult.okResult(new PageVo(cacheProduct,total));
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
        productCache.saveOrUpdateProducts(products);
        return ResponseResult.okResult();
    }

    /**
     * 编辑商品
     * @param products
     * @return
     */
    @Override
    public ResponseResult editProducts(Products products) {
        boolean ok = updateById(products);
        if(ok){
            productCache.saveOrUpdateProducts(products);
        }
        simpMessagingTemplate.convertAndSend("/pd/pushProduct", '1');

        return ResponseResult.okResult();
    }

//    @Override
//    public ResponseResult getProducts(Integer isHot) {
//        //先从redis中拿
//        List<Products> cacheProduct = productCache.getProducts(null,null);
//        if(Objects.isNull(cacheProduct) || cacheProduct.size()==0){
//            //从数据库查
//            List<Products> products = productsMapper.selectList(null);
//            productCache.setProducts();
//        }
//        List<Products> hotProducts = cacheProduct.stream().filter(product -> product.getIsHot().equals(isHot)).collect(Collectors.toList());
//
//        return ResponseResult.okResult(BeanCopyUtils.copyBeanList(hotProducts,ProductVo.class));
//    }

    /**
     * 删除商品
     * @param id
     * @return
     */
    @Override
    @Transactional
    public ResponseResult deleteProducts(Long id) {
        productsMapper.logicDeleteProducts(id);
        productCache.removeProduct(id);
        simpMessagingTemplate.convertAndSend("/pd/pushProduct", '1');
        return ResponseResult.okResult();
    }
}




