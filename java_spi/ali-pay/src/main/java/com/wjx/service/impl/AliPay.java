package com.wjx.service.impl;

import com.wjx.service.PayService;

public class AliPay implements PayService {
    @Override
    public void pay() {
        System.out.println("AliPay");
    }
}
