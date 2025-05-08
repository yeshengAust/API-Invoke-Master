package com.yes.interfaces.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterfaceSortListDto {
    private Integer pageNum;
    private Integer pageSize;
    private Integer sortId;
}
