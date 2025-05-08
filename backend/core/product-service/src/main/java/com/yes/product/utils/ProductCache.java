package com.yes.product.utils;

import com.yes.common.domain.entity.Products;
import com.yes.product.mapper.ProductsMapper;
import com.yes.product.service.ProductsService;
import com.yes.product.service.impl.ProductsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.yes.product.constants.ProductConstants.CACHE_PRODUCT_KEY;

@Component
@RequiredArgsConstructor
public class ProductCache {
    private final RedisTemplate redisTemplate;
    private final ProductsMapper productsMapper;

    public void setProducts() {
        List<Products> products = productsMapper.selectList(null);
        products.stream().forEach(product -> {
            redisTemplate.opsForZSet().add(CACHE_PRODUCT_KEY, product, product.getId());
        });

    }

    public List<Products> getProducts(Integer page, Integer pageSize) {
        if (Objects.isNull(page) || Objects.isNull(pageSize)) {
            return new ArrayList<>(redisTemplate.opsForZSet().range(CACHE_PRODUCT_KEY, 0, -1));
        }
        int start = (page - 1) * pageSize;
        int end = start + pageSize - 1;
        return new ArrayList<>(redisTemplate.opsForZSet().range(CACHE_PRODUCT_KEY, start, end));

    }

    public Long countProducts() {
        Long count = redisTemplate.opsForZSet().size(CACHE_PRODUCT_KEY);
        return count;
    }

    public void saveOrUpdateProducts(Products products) {
        redisTemplate.opsForZSet().removeRangeByScore(CACHE_PRODUCT_KEY, products.getId(), products.getId());
        redisTemplate.opsForZSet().add(CACHE_PRODUCT_KEY, products, products.getId());
    }
    public void removeProduct(Long id) {
        redisTemplate.opsForZSet().removeRangeByScore(CACHE_PRODUCT_KEY, id, id);
    }
}
