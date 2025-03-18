package com.yes.product.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductVo {
    private Long id;

    private String name;

    private BigDecimal price;
    private Date startTime;
    private Date endTime;
    private Long quota;

}
