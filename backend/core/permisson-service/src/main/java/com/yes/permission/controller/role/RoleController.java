package com.yes.permission.controller.role;

import com.yes.common.domain.dto.PageDto;
import com.yes.common.utils.ResponseResult;
import com.yes.permission.domain.dto.AddRoleDto;
import com.yes.permission.domain.dto.UpdateRoleDto;
import com.yes.permission.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/permission/role")
public class RoleController {
    @Autowired
    RoleService roleService;
    @GetMapping("/list")
    public ResponseResult  getRoleList(PageDto pageDto){
        return roleService.getRoleList(pageDto);
    }
    @PostMapping("/add")
    public ResponseResult addRole(@RequestBody AddRoleDto role){
        return roleService.addRole(role);
    }
    @PutMapping("/update")
    public  ResponseResult edit(@RequestBody UpdateRoleDto updateRoleDto){
        return roleService.edit(updateRoleDto);
    }
    @DeleteMapping("/delete")
    public  ResponseResult delete(Integer id){
        return roleService.delete(id.longValue());
    }
}
