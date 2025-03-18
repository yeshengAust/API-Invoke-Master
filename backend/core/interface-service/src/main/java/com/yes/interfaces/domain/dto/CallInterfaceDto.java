package com.yes.interfaces.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallInterfaceDto {
    private String accessKey;
    private String secretKey;
    private Long id;
    private String name;
    private String requestParamsEdit;
    private Integer sort;
}
