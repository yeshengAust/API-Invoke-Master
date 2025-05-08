package com.yes.buy.controller;

import com.yes.buy.domain.dto.BuyProductDto;
import com.yes.buy.service.BuyService;
import com.yes.common.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/buy")
public class BuyController {
    @Autowired
    BuyService service;
    @PostMapping
    ResponseResult buy(@RequestBody BuyProductDto dto) throws Exception {
        return service.buy(dto);
    }

}
