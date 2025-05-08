package com.yes.permission.service.impl.inner;


import com.yes.common.service.inner.InnerRoleService;
import com.yes.permission.mapper.RoleMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService(group = "api",interfaceClass = InnerRoleService.class)
public class InnerRoleServiceImpl implements InnerRoleService {
    /**
     * 查询用户角色
     * @param userId
     * @return
     */
    @Resource
    RoleMapper  mapper;
    @Override
    public String getUserRole(Long userId) {
        String userRole = mapper.getUserRole(userId);
        return userRole;
    }
}
