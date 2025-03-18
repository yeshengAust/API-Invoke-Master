package com.yes.interfaces.controller;

import com.yes.common.domain.dto.PageDto;
import com.yes.common.domain.entity.InterfaceInfo;
import com.yes.common.utils.ResponseResult;
import com.yes.interfaces.domain.dto.CallInterfaceDto;
import com.yes.interfaces.domain.dto.InterfaceLikeListDto;
import com.yes.interfaces.domain.dto.InterfaceSortListDto;
import com.yes.interfaces.service.InterfaceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/interface")
public class InterfaceController {
    @Autowired
    InterfaceInfoService interfaceService;
    @PostMapping("/save")
    public ResponseResult saveInterface( @RequestBody InterfaceInfo interfaceInfo){
        return interfaceService.saveInterfaces(interfaceInfo);
    }

    @GetMapping("/getInterfaceInfo")
    public ResponseResult getInterfaceInfo(Long id) {
        return interfaceService.getInterfaceInfo(id);
    }

    @GetMapping("/list")
    public ResponseResult list(PageDto pageDto) {
        return interfaceService.pageList(pageDto);
    }

    @GetMapping("/listBySort")
    public ResponseResult listBySort(InterfaceSortListDto dto) {
        return interfaceService.pageListBySort(dto);
    }

    @GetMapping("/like")
    public ResponseResult like(@RequestBody InterfaceLikeListDto dto) {
        return interfaceService.like(dto);
    }

    @PostMapping("/call")
    public ResponseResult call(@RequestBody CallInterfaceDto dto) {
        return interfaceService.call(dto);
    }
    @GetMapping("/interfaceExample")
    public ResponseResult getInterfaceExample(Long interfaceId){
        return interfaceService.getInterfaceExample(interfaceId);
    }
    @PostMapping("/editInterfaceExample")
    public ResponseResult editInterfaceExample(@RequestBody InterfaceInfo interfaceInfo){
        return interfaceService.setInterfaceExample(interfaceInfo);
    }

}
