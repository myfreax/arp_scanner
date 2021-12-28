package com.myfreax.www.arp_scanner;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import com.myfreax.www.arp_scanner.db.AppDatabase;
import com.myfreax.www.arp_scanner.ping.PingResult;
import com.myfreax.www.arp_scanner.subnet.Device;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SubnetDevices {
    final String TAG = "SubnetDevices";
    private AppDatabase db;

    private synchronized AppDatabase getDb(@NonNull Context context) {
        if (db == null) {
            db = AppDatabase.createInstance(context);
        }
        return db;
    }

    private final int noThreads = 100;

    private ArrayList<String> addresses;
    private ArrayList<Device> devicesFound;
    private OnSubnetDeviceFound listener;
    private Context appContext;
    private boolean cancelled = false;

    private final boolean disableProcNetMethod = false;
    private HashMap<String, String> ipMacHashMap = null;

    // This class is not to be instantiated
    private SubnetDevices() {
    }

    /**
     * Cancel a running scan
     */
    public void cancel() {
        this.cancelled = true;
    }

    private String getVendor(String mac) {
        if (mac != null) {
            String[] macChars = mac.split(":");
            if (macChars.length == 6) {
                try {
                    String org = (macChars[0] + macChars[1] + macChars[2]).toUpperCase(Locale.ROOT);
                    Log.d(TAG, org);
                    return getDb(appContext).macVendorsDao().findByMac(org).getName();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "Invalid Mac Address");
                return null;
            }
        }
        return null;
    }

    public interface OnSubnetDeviceFound {
        void onDeviceFound(Device device);

        void onFinished(ArrayList<Device> devicesFound);
    }

    /**
     * Find devices on the subnet working from the local device ip address
     *
     * @return - this for chaining
     */
    public static SubnetDevices fromLocalAddress() {
        InetAddress ipv4 = IPTools.getLocalIPv4Address();

        if (ipv4 == null) {
            throw new IllegalAccessError("Could not access local ip address");
        }

        return fromIPAddress(ipv4.getHostAddress());
    }

    /**
     * @param ipAddress - the ipAddress string of any device in the subnet i.e. "192.168.0.1"
     *                  the final part will be ignored
     * @return - this for chaining
     */
    public static SubnetDevices fromIPAddress(final String ipAddress) {

        if (!IPTools.isIPv4Address(ipAddress)) {
            throw new IllegalArgumentException("Invalid IP Address");
        }

        String segment = ipAddress.substring(0, ipAddress.lastIndexOf(".") + 1);

        SubnetDevices subnetDevice = new SubnetDevices();

        subnetDevice.addresses = new ArrayList<>();

        // Get addresses from ARP Info first as they are likely to be reachable
        for (String ip : ARPInfo.getAllIPAddressesInARPCache()) {
            if (ip.startsWith(segment)) {
                subnetDevice.addresses.add(ip);
            }
        }

        // Add all missing addresses in subnet
        for (int j = 0; j < 255; j++) {
            if (!subnetDevice.addresses.contains(segment + j)) {
                subnetDevice.addresses.add(segment + j);
            }
        }

        return subnetDevice;
    }


    /**
     * Starts the scan to find other devices on the subnet
     *
     * @param listener - to pass on the results
     * @return this object so we can call cancel on it if needed
     */
    public SubnetDevices findDevices(final Context context, final OnSubnetDeviceFound listener) {
        this.appContext = context;
        this.listener = listener;

        cancelled = false;
        devicesFound = new ArrayList<>();

        new Thread(() -> {
            // Load mac addresses into cache var (to avoid hammering the /proc/net/arp file when
            // lots of devices are found on the network.
            ipMacHashMap = disableProcNetMethod ? ARPInfo.getAllIPandMACAddressesFromIPSleigh() : ARPInfo.getAllIPAndMACAddressesInARPCache();

            ExecutorService executor = Executors.newFixedThreadPool(noThreads);

            for (final String add : addresses) {
                Runnable worker = new SubnetDeviceFinderRunnable(add);
                executor.execute(worker);
            }

            // This will make the executor accept no new threads
            // and finish all existing threads in the queue
            executor.shutdown();
            // Wait until all threads are finish
            try {
                executor.awaitTermination(1, TimeUnit.HOURS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Loop over devices found and add in the MAC addresses if missing.
            // We do this after scanning for all devices as /proc/net/arp may add info
            // because of the scan.
            ipMacHashMap = ARPInfo.getAllIPAndMACAddressesInARPCache();
            for (Device device : devicesFound) {
                if (device.mac == null && ipMacHashMap.containsKey(device.ip)) {
                    device.mac = ipMacHashMap.get(device.ip);
                }
            }
            listener.onFinished(devicesFound);
        }).start();

        return this;
    }

    private synchronized void subnetDeviceFound(Device device) {
        devicesFound.add(device);
        listener.onDeviceFound(device);
    }

    public class SubnetDeviceFinderRunnable implements Runnable {
        private final String address;

        SubnetDeviceFinderRunnable(String address) {
            this.address = address;
        }

        @Override
        public void run() {
            if (cancelled) return;

            try {
                InetAddress ia = InetAddress.getByName(address);
                int timeOutMillis = 2500;
                PingResult pingResult = Ping.onAddress(ia).setTimeOutMillis(timeOutMillis).doPing();
                if (pingResult.isReachable) {
                    Device device = new Device(ia);
                    // Add the device MAC address if it is in the cache
                    if (ipMacHashMap.containsKey(ia.getHostAddress())) {
                        device.mac = ipMacHashMap.get(ia.getHostAddress());
                        device.vendor = getVendor(device.mac);
                    }

                    device.time = pingResult.timeTaken;
                    subnetDeviceFound(device);
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

}