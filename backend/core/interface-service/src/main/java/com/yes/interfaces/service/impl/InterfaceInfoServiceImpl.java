package com.yes.interfaces.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yes.common.domain.dto.PageDto;
import com.yes.common.domain.entity.InterfaceInfo;
import com.yes.common.domain.vo.PageVo;
import com.yes.common.enums.ErrorCode;
import com.yes.common.exception.SystemException;
import com.yes.common.utils.BeanCopyUtils;
import com.yes.common.utils.ResponseResult;
import com.yes.interfaces.domain.dto.CallInterfaceDto;
import com.yes.interfaces.domain.dto.InterfaceLikeListDto;
import com.yes.interfaces.domain.dto.InterfaceSortListDto;
import com.yes.interfaces.domain.vo.InterfaceExampleVo;
import com.yes.interfaces.domain.vo.InterfaceVo;
import com.yes.interfaces.mapper.InterfaceInfoMapper;
import com.yes.interfaces.service.InterfaceInfoService;
import com.yes.sdk.client.ApiClient;
import com.yes.sdk.exception.ApiInvokeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author 叶苗
 * @description 针对表【interface_info(接口信息)】的数据库操作Service实现
 * @createDate 2025-02-27 11:19:55
 */
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
        implements InterfaceInfoService {


    @Autowired
    InterfaceInfoMapper mapper;


    /**
     * 获取接口信息
     *
     * @return
     */
    @Override
    public ResponseResult getInterfaceInfo(Long id) {
        InterfaceInfo interfaceInfo = getById(id);
        InterfaceVo interfaceVo = BeanCopyUtils.copyBean(interfaceInfo, InterfaceVo.class);
        return ResponseResult.okResult(interfaceVo);
    }

    @Override
    public ResponseResult pageList(PageDto pageDto) {
        //开启分页
        Page<InterfaceInfo> page = new Page<>(pageDto.getPageNum(), pageDto.getPageSize());
        //查询
        page(page);
        List<InterfaceVo> interfaceVos = BeanCopyUtils.copyBeanList(page.getRecords(), InterfaceVo.class);
        PageVo pageVo = new PageVo(interfaceVos, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 根据接口分类查询接口信息
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult pageListBySort(InterfaceSortListDto dto) {
        //条件
        LambdaQueryWrapper<InterfaceInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InterfaceInfo::getSortId, dto.getSortId());
        //开启分页
        Page<InterfaceInfo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        //查询
        page(page, queryWrapper);
        List<InterfaceVo> interfaceVos = BeanCopyUtils.copyBeanList(page.getRecords(), InterfaceVo.class);
        PageVo pageVo = new PageVo(interfaceVos, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 根据 name 和 description做模糊查询
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult like(InterfaceLikeListDto dto) {
        LambdaQueryWrapper<InterfaceInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(dto.getName()), InterfaceInfo::getName, dto.getName());
        queryWrapper.like(StringUtils.isNotBlank(dto.getDescription()), InterfaceInfo::getDescription, dto.getDescription());
        Page<InterfaceInfo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        page(page, queryWrapper);
        List<InterfaceVo> interfaceVos = BeanCopyUtils.copyBeanList(page.getRecords(), InterfaceVo.class);
        PageVo pageVo = new PageVo(interfaceVos, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 调用接口
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult call(CallInterfaceDto dto) {
        Long start = System.currentTimeMillis();

        //调用接口
        ApiClient apiClient = new ApiClient(dto.getAccessKey(), dto.getSecretKey());

        Class<? extends ApiClient> apiClientClass = apiClient.getClass();
        //根据接口名称获取方法

        Method method;

        try {
            //判断用户是否传递了参数
            if (StringUtils.isNotBlank(dto.getRequestParamsEdit())) {
                method = apiClientClass.getMethod(dto.getName(), String.class);
                //调用方法
                //判断是否传递文件
                Object invoke = null;

                invoke = method.invoke(apiClient, dto.getRequestParamsEdit());


                return new ResponseResult(201, "操作成功", invoke);
            }

            //调用mq写日志信息
            method = apiClientClass.getMethod(dto.getName());
            Object invoke = null;
            try {
                invoke = method.invoke(apiClient);
            } catch (Exception e) {
                log.error(e.toString());
            }

            //调用方法
            return new ResponseResult(201, "操作成功", invoke);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause instanceof ApiInvokeException) {
                throw (ApiInvokeException) cause;
            }
            log.error(e.toString());
            throw new SystemException(ErrorCode.SYSTEM_ERROR);
        }

    }

    /**
     * 保存
     * @param interfaceInfo
     * @return
     */
    @Override
    public ResponseResult saveInterfaces(InterfaceInfo interfaceInfo) {
        save(interfaceInfo);
        return ResponseResult.okResult();
    }

    /**
     * 获取接口的实例
     * @param interfaceId
     * @return
     */
    @Override
    public ResponseResult getInterfaceExample(Long interfaceId) {
        InterfaceExampleVo interfaceExample = mapper.getInterfaceExample(interfaceId);
        return ResponseResult.okResult(interfaceExample);
    }

    /**
     * 编辑接口的例子
     * @param interfaceInfo
     * @return
     */
    @Override
    public ResponseResult setInterfaceExample(InterfaceInfo interfaceInfo) {
        updateById(interfaceInfo);
        return ResponseResult.okResult();
    }


}




