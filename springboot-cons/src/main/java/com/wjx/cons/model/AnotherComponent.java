package com.wjx.cons.model;

import java.net.InetAddress;

public class AnotherComponent {

    private boolean enable;
    private InetAddress remoteAddress;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public InetAddress getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(InetAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }
}
