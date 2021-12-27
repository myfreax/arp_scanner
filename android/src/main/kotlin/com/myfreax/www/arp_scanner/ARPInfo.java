package com.myfreax.www.arp_scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Looks at the file at /proc/net/arp to fromIPAddress ip/mac addresses from the cache
 * We assume that the file has this structure:
 *
 * IP address       HW type     Flags       HW address            Mask     Device
 * 192.168.18.11    0x1         0x2         00:04:20:06:55:1a     *        eth0
 * 192.168.18.36    0x1         0x2         00:22:43:ab:2a:5b     *        eth0
 *
 * Also looks at the output from `ip sleigh show` command
 *
 */
public class ARPInfo {

    // This class is not to be instantiated
    private ARPInfo() {
    }

    /**
     * Returns all the ip addresses currently in the ARP cache (/proc/net/arp).
     *
     * @return list of IP addresses found
     */
    public static ArrayList<String> getAllIPAddressesInARPCache() {
        return new ArrayList<>(getAllIPAndMACAddressesInARPCache().keySet());
    }

    /**
     * Returns all the IP/MAC address pairs currently in the following places
     *
     * 1. ARP cache (/proc/net/arp).
     * 2. `ip neigh show` command
     *
     * @return list of IP/MAC address pairs found
     */
    public static HashMap<String, String> getAllIPAndMACAddressesInARPCache() {
        HashMap<String, String> macList = getAllIPandMACAddressesFromIPSleigh();
        return macList;
    }

    /**
     * Get the IP / MAC address pairs from `ip sleigh show` command
     *
     * @return hashmap of ips and mac addresses
     */
    public static HashMap<String, String> getAllIPandMACAddressesFromIPSleigh() {
        HashMap<String, String> macList = new HashMap<>();

        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("ip neigh show");
            proc.waitFor();

            InputStreamReader reader = new InputStreamReader(proc.getInputStream());
            BufferedReader buffer = new BufferedReader(reader);
            String line;
            while ((line = buffer.readLine()) != null) {
                String[] splits = line.split(" ");
                if (splits.length < 4) {
                    continue;
                }
                macList.put(splits[0], splits[4]);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return macList;
    }

}
