package com.wjx.config.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigController {

    @RequestMapping("/config")
    public String frist(){
        return "frist spring boot";
    }

}
