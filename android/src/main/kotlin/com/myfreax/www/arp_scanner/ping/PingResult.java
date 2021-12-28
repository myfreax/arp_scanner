package com.myfreax.www.arp_scanner.ping;

import java.net.InetAddress;

public class PingResult {
    public final InetAddress ia;
    public boolean isReachable;
    public String error = null;
    public float timeTaken;
    public String fullString;
    public String result;

    public PingResult(InetAddress ia) {
        this.ia = ia;
    }

    @Override
    public String toString() {
        return "PingResult{" +
                "ia=" + ia +
                ", isReachable=" + isReachable +
                ", error='" + error + '\'' +
                ", timeTaken=" + timeTaken +
                ", fullString='" + fullString + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
