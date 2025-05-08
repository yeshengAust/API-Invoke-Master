package com.yes.permission.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoleDto {
    private Long id;
    private String roleName;
    private Integer status;
    private String remark;
}
