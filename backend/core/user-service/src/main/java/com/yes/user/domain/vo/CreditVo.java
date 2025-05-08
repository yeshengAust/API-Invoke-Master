package com.yes.user.domain.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @TableName credit
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditVo {
    private Long id;

    private String accessKey;

    private String secretKey;

    private Date expireTime;
    private Date createTime;

    private String description;
    private String name;

    private Integer quota;

    private Long sortId;
    private String sort;
    private Integer status;

    private Integer remain;
}