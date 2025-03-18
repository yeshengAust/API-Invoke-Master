package com.yes.interfaces.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yes.common.domain.dto.PageDto;
import com.yes.common.utils.ResponseResult;
import com.yes.interfaces.domain.dto.CallInterfaceDto;
import com.yes.interfaces.domain.dto.InterfaceLikeListDto;
import com.yes.interfaces.domain.dto.InterfaceSortListDto;
import com.yes.common.domain.entity.InterfaceInfo;

/**
* @author 叶苗
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2025-02-27 11:17:57
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    ResponseResult getInterfaceInfo(Long id);

    ResponseResult pageList(PageDto pageDto);
    ResponseResult pageListBySort(InterfaceSortListDto dto);


    ResponseResult like(InterfaceLikeListDto dto);

    ResponseResult call(CallInterfaceDto dto);


    ResponseResult saveInterfaces(InterfaceInfo interfaceInfo);

    ResponseResult getInterfaceExample(Long interfaceId);

    ResponseResult setInterfaceExample(InterfaceInfo interfaceInfo);
}
