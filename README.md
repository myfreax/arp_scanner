# arp_scanner
A Flutter plugin for discovers devices on local network by Address Resolution Protocol (ARP).
## Limit
- plugin only support Android Sdk Version 29

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