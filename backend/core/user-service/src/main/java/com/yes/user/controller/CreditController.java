package com.yes.user.controller;

import com.yes.common.domain.dto.PageDto;
import com.yes.common.utils.ResponseResult;
import com.yes.user.domain.dto.CreditDto;
import com.yes.user.domain.dto.QuotaChargeDto;
import com.yes.user.service.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/credit")
public class CreditController {
    @Autowired
    CreditService creditService;
    @PostMapping("/save")
    public ResponseResult saveCredit(@RequestBody CreditDto dto){
        return creditService.saveCredit(dto);
    }
    @GetMapping("/page")
    public ResponseResult pageCredit(CreditDto dto){
        return creditService.pageCredit(dto);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseResult delete(@PathVariable Long id){
        return creditService.delete(id);
    }
    @PutMapping ("/forbid")
    public ResponseResult updateStatus( Long id,Integer status){
        return creditService.updateStatus(id,status);
    }
    @PostMapping("/charge")
    public ResponseResult handleRecharge(@RequestBody QuotaChargeDto dto){
        return creditService.handleRecharge(dto);
    }
}
