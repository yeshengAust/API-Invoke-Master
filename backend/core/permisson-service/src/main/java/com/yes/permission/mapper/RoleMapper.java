package com.yes.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yes.permission.domain.entity.Role;

/**
* @author 叶苗
* @description 针对表【role(角色信息表)】的数据库操作Mapper
* @createDate 2025-02-26 13:43:52
* @Entity generator.domain.Role
*/
public interface RoleMapper extends BaseMapper<Role> {
    String getUserRole(Long userId);
}




