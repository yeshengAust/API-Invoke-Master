package com.yes.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.yes.common.domain.dto.PageDto;
import com.yes.common.domain.vo.PageVo;
import com.yes.common.enums.ErrorCode;
import com.yes.common.exception.SystemException;
import com.yes.common.utils.BeanCopyUtils;
import com.yes.common.utils.ResponseResult;
import com.yes.permission.domain.dto.AddRoleDto;
import com.yes.permission.domain.dto.UpdateRoleDto;
import com.yes.permission.domain.entity.Role;
import com.yes.permission.domain.vo.RoleVo;
import com.yes.permission.mapper.RoleMapper;
import com.yes.permission.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 叶苗
* @description 针对表【role(角色信息表)】的数据库操作Service实现
* @createDate 2025-02-26 13:43:52
*/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
    implements RoleService {
    /**
     * 获取角色信息
     * @return
     */
    @Override
    public ResponseResult getRoleList(PageDto pageDto) {
        //配置分页
        Page<Role> page = new Page<>(pageDto.getPageNum(),pageDto.getPageSize());
        page(page);
        PageVo pageVo = new PageVo(BeanCopyUtils.copyBeanList(page.getRecords(), RoleVo.class),page.getTotal());

        return ResponseResult.okResult(pageVo);
    }

    /**
     * 添加角色
     * @param
     * @return
     */
    @Override
    public ResponseResult addRole(AddRoleDto roleDto) {
        //将dto转为实体
        Role role = BeanCopyUtils.copyBean(roleDto, Role.class);
        role.setRoleSort(count()+1);
        boolean flag = true;
        try {
          flag =  save(role);
        }
        catch (Exception e){
            log.error(e.getMessage());
        }
        if(!flag){
            throw new SystemException(ErrorCode.SYSTEM_ERROR);
        }
        return ResponseResult.okResult();
    }

    /**
     * 更新角色信息
     * @param updateRoleDto
     * @return
     */
    @Override
    public ResponseResult edit(UpdateRoleDto updateRoleDto) {
        //bean拷贝
        Role role = BeanCopyUtils.copyBean(updateRoleDto, Role.class);
        try {
            updateById(role);
        }
        catch (Exception e){
            throw new SystemException(ErrorCode.SYSTEM_ERROR);
        }
        return ResponseResult.okResult();
    }

    /**
     * 删除角色信息
     * @param id
     * @return
     */
    @Override
    public ResponseResult delete(Long id) {
        Role role = new Role();
        role.setId(id);
        try {
            removeById(id);
        }
        catch (Exception e){
            throw new SystemException(ErrorCode.SYSTEM_ERROR);
        }
        return ResponseResult.okResult();
    }
}




