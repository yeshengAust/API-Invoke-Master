package com.yes.user.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDto {
    private String username;

    private Integer status;

    private Integer gender;

    private String email;
    private Long id;
}
