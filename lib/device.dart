class Device {
  String? ip;
  String? hostname;
  String? mac;
  double time = 0;

  Device(this.ip, this.hostname, this.mac, this.time);
  Device.fromJson(Map<String, dynamic> json) {
    Device(json["ip"], json["hostname"], json["mac"], json["time"]);
  }

  Map<String, dynamic> toJson() =>
      {'hostname': hostname, 'ip': ip, 'time': time, 'mac': mac};
}
