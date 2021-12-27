import 'dart:async';
import 'dart:convert';

import 'package:arp_scanner/device.dart';
import 'package:flutter/services.dart';

class ArpScanner {
  static const MethodChannel _channel = MethodChannel('arp_scanner');
  static const EventChannel _scanningEventChannel =
      EventChannel('arp_scanning');

  static const EventChannel _scanFinishedEventChannel =
      EventChannel('arp_scanFinished');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<bool> scan() async {
    return await _channel.invokeMethod('scan');
  }

  static Stream<Device> get onScanning {
    return _scanningEventChannel
        .receiveBroadcastStream()
        .map((e) => Device.fromJson(jsonDecode(e)));
  }

  static Stream<List<Device>> get onScanFinished {
    return _scanFinishedEventChannel.receiveBroadcastStream().map((e) {
      List<dynamic> devices = jsonDecode(e);
      return devices.map((e) => Device.fromJson(e)).toList();
    });
  }
}
