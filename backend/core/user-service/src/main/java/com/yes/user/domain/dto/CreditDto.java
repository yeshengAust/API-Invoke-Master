package com.yes.user.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditDto {

    private String name;
    private Long userId;
    private String secretKey;
    private Date expireTime;
    private Long quota;
    private Long sortId;
    private Integer pageNum;
    private Integer pageSize;

}
