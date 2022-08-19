# arp_scanner
A Flutter plugin for discovers devices on local network. It return IP v4 address, Mac Address,Interface Vendor and host name.
## Limit
- Plugin only support Android SDK Version 29 and below
- The hostname resolve only work in 2.4G Wifi, otherwise return null
## Usage
To use this plugin, add arp_scanner as a [dependency in your pubspec.yaml file](https://pub.dev/packages/arp_scanner).
### build.gradle for App
Change compileSdkVersion of build.gradle build script to 29.
```gradle
android {
    ....
    compileSdkVersion 29
    ....
}
```
### build.gradle for project
Change kotlin version of build.gradle build script to 1.5.30 or newer.
```
buildscript {
  ...
  ext.kotlin_version = '1.5.30'
  ...
}
```


## Example
The example You can find [here](https://pub.dev/packages/arp_scanner/example).

## Update Mac vendor database
Clone repo to local. Then
```bash
 go mod tidy 
 go run createMacVendorDB.go
```
Add local repo as your project dependency after Mac vendor database is updated.

## Fix `Duplicate class com.google.gson`
```
android {
  configurations {
    all {
      exclude module:'gson'
    }
  }
}
```
