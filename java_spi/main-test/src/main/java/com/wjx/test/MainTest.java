package com.wjx.test;

import com.wjx.service.PayService;

import java.util.ServiceLoader;

public class MainTest {

    public static void main(String[] args) {
        ServiceLoader<PayService> load = ServiceLoader.load(PayService.class);
        for (PayService service: load) {
            System.out.println(service);
            service.pay();
        }
    }

}
