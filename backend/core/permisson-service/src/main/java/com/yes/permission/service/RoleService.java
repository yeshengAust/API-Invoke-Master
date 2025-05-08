package com.yes.permission.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yes.common.domain.dto.PageDto;
import com.yes.common.utils.ResponseResult;
import com.yes.permission.domain.dto.AddRoleDto;
import com.yes.permission.domain.dto.UpdateRoleDto;
import com.yes.permission.domain.entity.Role;

/**
* @author 叶苗
* @description 针对表【role(角色信息表)】的数据库操作Service
* @createDate 2025-02-26 13:43:52
*/
public interface RoleService extends IService<Role> {

    ResponseResult getRoleList(PageDto pageDto);

    ResponseResult addRole(AddRoleDto role);

    ResponseResult edit(UpdateRoleDto updateRoleDto);

    ResponseResult delete(Long l);
}
