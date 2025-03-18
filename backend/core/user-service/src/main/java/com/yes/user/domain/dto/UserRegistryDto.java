package com.yes.user.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistryDto
{
    private String username;

    private String account;

    private String password;

    private String email;
}
