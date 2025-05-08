package com.yes.common.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName log
 */
@TableName(value ="log")
@Data
public class Log  implements Serializable {
    private Long id;

    private Date createTime;


    private Long groupId;

    private Long creditId;

    private Date startTime;

    private Date endTime;

    private Long useTime;
    private Long invokeTime;

    private Long cost;
    private Long userId;
    private Long interfaceId;




}