package com.yes.user.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import lombok.Data;

/**
 * @TableName credit
 */
@TableName(value ="credit")
@Data
public class Credit {
    private Long id;
    private String accessKey;

    private String secretKey;

    private Date expireTime;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    private String name;

    private Long quota;

    private Long sortId;

    private Long remain;
    private Integer status;
    private Long userId;
}