package com.yes.log.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogCountVo {
    private Long allQuota;
    private Long rpm;
    private Long tpm;

}
