package com.myfreax.www.arp_scanner.subnet;

import java.net.InetAddress;

public class Device {
    public String ip;
    public String hostname;
    public String mac;
    public String vendor;
    public float time = 0;

    public Device(InetAddress ip) {
        this.ip = ip.getHostAddress();
        this.hostname = ip.getCanonicalHostName();
    }

    @Override
    public String toString() {
        return "Device{" +
                "ip='" + ip + '\'' +
                "vendor='" + vendor + '\'' +
                ", hostname='" + hostname + '\'' +
                ", mac='" + mac + '\'' +
                ", time=" + time +
                '}';
    }
}

