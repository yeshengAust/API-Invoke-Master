package com.yes.common.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @TableName products
 */
@TableName(value ="products")
@Data
public class Products implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private BigDecimal price;

    private Long quota;

    private Date startTime;

    private Date endTime;

    private Integer isHot;
    private Integer status;
    private Long count;
}