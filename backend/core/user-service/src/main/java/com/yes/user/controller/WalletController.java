package com.yes.user.controller;

import com.yes.common.utils.ResponseResult;
import com.yes.user.domain.dto.ChargeDto;
import com.yes.user.service.UserService;
import com.yes.user.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/wallet")
public class WalletController {
    @Autowired
    WalletService walletService;
    @GetMapping("/getWallet")
    public ResponseResult getWallet(Long userId){
        return walletService.getWallet(userId);
    }
    @GetMapping("/getQuota")
    public ResponseResult getQuota(){
        return walletService.getQuota();
    }
    @PostMapping("/charge")
    public ResponseResult charge(@RequestBody ChargeDto dto){
        return walletService.charge(dto);
    }
}
