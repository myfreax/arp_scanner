import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:arp_scanner/arp_scanner.dart';

void main() {
  const MethodChannel channel = MethodChannel('arp_scanner');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {});
}
