package com.myfreax.www.arp_scanner;


import com.myfreax.www.arp_scanner.ping.PingOptions;
import com.myfreax.www.arp_scanner.ping.PingResult;
import com.myfreax.www.arp_scanner.ping.PingTools;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Ping {

    // This class is not to be instantiated
    private Ping() {
    }

    private InetAddress address;
    private final PingOptions pingOptions = new PingOptions();

    /**
     * Set the address to ping
     *
     * @param ia - Address to be pinged
     * @return this object to allow chaining
     */
    public static Ping onAddress(InetAddress ia) {
        Ping ping = new Ping();
        ping.setAddress(ia);
        return ping;
    }

    /**
     * Set the timeout
     *
     * @param timeOutMillis - the timeout for each ping in milliseconds
     * @return this object to allow chaining
     */
    public Ping setTimeOutMillis(int timeOutMillis) {
        if (timeOutMillis < 0) throw new IllegalArgumentException("Times cannot be less than 0");
        pingOptions.setTimeoutMillis(timeOutMillis);
        return this;
    }

    private void setAddress(InetAddress address) {
        this.address = address;
    }

    /**
     * Perform a synchronous ping and return a result, will ignore number of times.
     *
     * Note that this should be performed on a background thread as it will perform a network
     * request
     *
     * @return - ping result
     * @throws UnknownHostException - if the host cannot be resolved
     */
    public PingResult doPing() throws UnknownHostException {
        return PingTools.doPing(address, pingOptions);
    }

}