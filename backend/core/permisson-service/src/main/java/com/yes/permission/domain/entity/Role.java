package com.yes.permission.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;

import lombok.Data;

/**
 * @TableName role
 */
@TableName(value = "role")
@Data
public class Role {
    private Long id;

    private String roleName;

    private String roleKey;

    private Integer roleSort;

    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private Integer isDelete;
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    private String remark;
}