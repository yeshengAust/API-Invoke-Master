package com.yes.interfaces.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterfaceLikeListDto {
    private Integer pageNum;
    private Integer pageSize;
    private String name;
    private String description;

}
