package com.yes.user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yes.common.domain.dto.PageDto;
import com.yes.common.domain.entity.User;
import com.yes.common.utils.ResponseResult;
import com.yes.user.domain.dto.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author 叶苗
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2025-02-25 14:24:38
 */
public interface UserService extends IService<User> {
    ResponseResult getCaptcha();

    ResponseResult login(UserLoginDto user);

    ResponseResult getUserInfo();

    ResponseResult updatePassword(UserUpdatePasswordDto updatePasswordDto);

    ResponseResult saveUser(SaveUserDto saveUserDto);

    ResponseResult pageList(PageDto pageDto);

    ResponseResult delete(Long id);

    ResponseResult updateUser(UpdateUserDto userDto);

    ResponseResult registry(UserLoginDto user);

    ResponseResult sendEmail(String email);


    ResponseResult uploadFile(MultipartFile file);

    ResponseResult checkQQ(String qq);
}
