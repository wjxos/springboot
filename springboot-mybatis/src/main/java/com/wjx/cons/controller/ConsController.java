package com.wjx.cons.controller;

import com.wjx.cons.model.AnotherComponent;
import com.wjx.cons.model.OwnerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsController {

    @Autowired
    private AnotherComponent anotherComponent;

    @RequestMapping("anthoer")
    public String frist(){
        System.out.println(anotherComponent);
        return "frist spring boot";
    }

    @Autowired
    private OwnerProperties ownerProperties;

    @RequestMapping("owner")
    public String owner(){
        System.out.println(ownerProperties.getFristName());
        return "frist spring boot";
    }

}
