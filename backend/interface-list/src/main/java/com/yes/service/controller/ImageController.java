package com.yes.service.controller;

import com.yes.service.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apiService")
public class ImageController {
    @Autowired
    ImageService testService;
    @GetMapping("/getName")
    public String getName(String name){
        System.out.println("nihao");
        return name ;
    }
    @PostMapping("/cartoonImage")
    public String cartoonImage( String base64Data){
        return testService.cartoonImage(base64Data).trim();
    }
}
