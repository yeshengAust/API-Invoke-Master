package com.yes.permission.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserUtils {
   private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
//加密数据
    public static String encode(String pass){
        return bCryptPasswordEncoder.encode(pass);
    }
    public static boolean match(String pass,String encodedPass){
      return bCryptPasswordEncoder.matches(pass,encodedPass);
    }

}
