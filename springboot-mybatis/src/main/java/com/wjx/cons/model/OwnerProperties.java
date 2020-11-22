package com.wjx.cons.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
//在属性类中前缀不可以驼峰模式，只能用羊肉串模式
@ConfigurationProperties("acme.my-person.person")
public class OwnerProperties {

    private String fristName;

    public String getFristName() {
        return fristName;
    }

    public void setFristName(String fristName) {
        this.fristName = fristName;
    }
}
