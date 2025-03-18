package com.yes.common.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName interface_info
 */
@TableName(value ="interface_info")
@Data
public class InterfaceInfo implements Serializable {
    private Long id;

    private String name;

    private String description;

    private String url;

    private String params;
    private String data;


    private Integer status;

    private String method;
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    private Integer isDelete;
    private Long sortId;
    private Long cost;
    @TableField(exist = false)
    private  Long invokeCount;
    private String pom;
    private String example;
}