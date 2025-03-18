package com.yes.product.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yes.product.domain.dto.ProductPageDto;
import com.yes.common.domain.entity.Products;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author 叶苗
* @description 针对表【products】的数据库操作Mapper
* @createDate 2025-03-11 12:42:13
* @Entity generator.domain.Products
*/
@Mapper
public interface ProductsMapper extends BaseMapper<Products> {
   List<Products> pageProducts(ProductPageDto dto);
   List<Products> productsAll(ProductPageDto dto);

    void logicDeleteProducts(Long id);
}




