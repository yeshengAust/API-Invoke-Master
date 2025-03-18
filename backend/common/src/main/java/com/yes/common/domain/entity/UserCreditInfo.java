package com.yes.common.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreditInfo implements Serializable {
    private String accessKey;
    private String secretKey;
    private Long creditId;
    private Long userId;
    private Long remain;
    private Long sortId;
}
