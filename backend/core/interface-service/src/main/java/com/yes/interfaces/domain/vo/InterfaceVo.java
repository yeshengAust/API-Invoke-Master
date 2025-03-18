package com.yes.interfaces.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterfaceVo {

    private Long id;

    private String name;

    private String description;

    private String url;

    private String params;


    private String data;

    private Integer status;

    private String method;
    private Long sortId;



}