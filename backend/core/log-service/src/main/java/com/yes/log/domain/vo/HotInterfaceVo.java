package com.yes.log.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotInterfaceVo {
    private Long id;

    private String name;

    private Long invokeCount;
    private Double upRate;
    private Long sort;








}
