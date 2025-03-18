package com.yes.user.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDto {
    String account;
    String username;
    String password;
    String prefix;
    String verify;
    String email;
}
