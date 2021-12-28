import 'package:arp_scanner/device.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:arp_scanner/arp_scanner.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _result = '';

  @override
  void initState() {
    super.initState();
    ArpScanner.onScanning.listen((Device device) {
      setState(() {
        _result =
            "${_result}Mac:${device.mac} ip:${device.ip} hostname:${device.hostname} time:${device.time} vendor:${device.vendor} \n";
      });
    });
    ArpScanner.onScanFinished.listen((List<Device> devices) {
      setState(() {
        _result = "${_result}total: ${devices.length}";
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        floatingActionButton: FloatingActionButton(
            child: const Icon(Icons.scanner_sharp),
            onPressed: () async {
              //scan sub net devices
              await ArpScanner.scan();
              setState(() {
                _result = "";
              });
            }),
        appBar: AppBar(
          actions: <Widget>[
            // action button
            IconButton(
              icon: const Icon(Icons.cancel),
              onPressed: () async {
                await ArpScanner.cancel();
              },
            ),
          ],
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text(_result),
        ),
      ),
    );
  }
}
