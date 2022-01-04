package com.myfreax.www.arp_scanner

import android.content.Context
import android.os.Handler
import android.util.Log
import androidx.annotation.NonNull
import com.myfreax.www.arp_scanner.subnet.Device
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.util.ArrayList
import com.google.gson.Gson
import android.os.Looper

/** ArpScannerPlugin */
class ArpScannerPlugin : FlutterPlugin, MethodCallHandler {
    companion object{
        const val TAG= "ArpScannerPlugin"
    }
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private var uiThreadHandler: Handler? = Handler(Looper.getMainLooper())
    private val gson by lazy {
        Gson()
    }
    private var subnetDevices:SubnetDevices? = null
    private lateinit var appContext: Context
    private lateinit var channel: MethodChannel
    private lateinit var scanningEventChannel: EventChannel
    private lateinit var scanFinishedEventChannel: EventChannel
    private var scanFinishedEventSink: EventChannel.EventSink? = null
    private var scanningEventSink: EventChannel.EventSink? = null

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        appContext = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "arp_scanner")
        channel.setMethodCallHandler(this)

        scanningEventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "arp_scanning")
        scanningEventChannel.setStreamHandler(object : EventChannel.StreamHandler {
            override fun onCancel(arguments: Any?) {
                scanningEventSink = null
            }

            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                scanningEventSink = events
            }
        })
        scanFinishedEventChannel =
            EventChannel(flutterPluginBinding.binaryMessenger, "arp_scanFinished")
        scanFinishedEventChannel.setStreamHandler(object : EventChannel.StreamHandler {
            override fun onCancel(arguments: Any?) {
                scanFinishedEventSink = null
            }

            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                scanFinishedEventSink = events
            }
        })
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "scan" -> findSubnetDevices(result)
            "cancel" -> cancel(result)
            else -> result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        scanningEventChannel.setStreamHandler(null)
    }

    private fun findSubnetDevices(result: Result) {
        result.success(true)
        subnetDevices = SubnetDevices.fromLocalAddress().findDevices(this.appContext, object : SubnetDevices.OnSubnetDeviceFound {
                override fun onDeviceFound(foundedDevice: Device) {
                    Log.d(
                        TAG,
                        "hostname:${foundedDevice.hostname} Mac:${foundedDevice.mac} " +
                                "IP:${foundedDevice.ip} time:${foundedDevice.time} " +
                                "vendor:${foundedDevice.vendor}"
                    )
                    uiThreadHandler?.post {
                        scanningEventSink?.success(gson.toJson(foundedDevice))
                    }
                }

                override fun onFinished(foundedDevices: ArrayList<Device>) {
                    Log.d(TAG, foundedDevices.size.toString())
                    uiThreadHandler?.post {
                        scanFinishedEventSink?.success(gson.toJson(foundedDevices))
                    }
                }
            })
    }
    private fun cancel(result: Result){
        Log.d(TAG,"scan canceled")
        subnetDevices?.cancel()
        result.success(true)
    }
}
