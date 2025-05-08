package com.yes.common.service.inner;

import com.yes.common.domain.entity.Products;

public interface InnerProductsService {
    Products getProductsById(Long id);
    void updateProducts(Products products);
}
