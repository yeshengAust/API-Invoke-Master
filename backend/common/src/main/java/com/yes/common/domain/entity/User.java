package com.yes.common.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String account;

    private String avatar;

    private Integer gender;

    private String password;

    private String accessKey;


    private Date createTime;

    private Date updateTime;

    private Integer isDelete;
    private String email;
    private BigDecimal wallet;
    private Long quota;

}