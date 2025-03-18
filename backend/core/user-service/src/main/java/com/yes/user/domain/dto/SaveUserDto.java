package com.yes.user.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveUserDto
{
    private String username;

    private String account;

    private String password;

    private Integer gender;

    private String email;
}
