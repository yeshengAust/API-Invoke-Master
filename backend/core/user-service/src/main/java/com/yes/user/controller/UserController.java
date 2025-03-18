package com.yes.user.controller;

import com.yes.common.annotation.SystemLog;
import com.yes.common.domain.dto.PageDto;
import com.yes.common.utils.ResponseResult;
import com.yes.user.domain.dto.*;
import com.yes.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.print.DocFlavor;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;



    @GetMapping("/captcha")
    public ResponseResult getCaptcha() throws IOException {
      return userService.getCaptcha();
    }
    @GetMapping("/sendEmail")
    public ResponseResult sendEmail(String email){
        return userService.sendEmail(email);
    }

    @SystemLog(message = "用户登录接口")
    @PostMapping("/login")
    public ResponseResult login(@RequestBody UserLoginDto user){
        return userService.login(user);
    }
    @SystemLog(message = "用户注册接口")
    @PostMapping("/registry")
    public ResponseResult registry(@RequestBody UserLoginDto user){
        return userService.registry(user);
    }

    @GetMapping("/getUserInfo")
    public ResponseResult getUserInfo(){
        return userService.getUserInfo();
    }
    @PutMapping("/updatePassword")
    public ResponseResult updatePassword(@RequestBody UserUpdatePasswordDto updatePasswordDto){
        return userService.updatePassword(updatePasswordDto);
    }
    @PostMapping("/save")
    public ResponseResult saveUser(@RequestBody SaveUserDto saveUserDto){
        return userService.saveUser(saveUserDto);
    }
    @GetMapping("/list")
    public ResponseResult pageList(PageDto pageDto){
        return userService.pageList(pageDto);
    }
    @DeleteMapping("/delete")
    public  ResponseResult delete(Integer id){
        return userService.delete(id.longValue());
    }
    @PutMapping("/update")
    public ResponseResult update(@RequestBody UpdateUserDto userDto){
        return userService.updateUser(userDto);
    }
    @PutMapping("/uploadHead")
    public ResponseResult uploadFile(@RequestParam(value = "image") MultipartFile file) {
        return userService.uploadFile(file);
    }
    @GetMapping("/checkQQ")
    public ResponseResult checkQQ(String qq){
        return userService.checkQQ(qq);
    }
}
