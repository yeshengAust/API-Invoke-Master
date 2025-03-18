package com.yes.product.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPageDto {
    private Integer pageNum;
    private Integer pageSize;
    private Integer isHot;
}
