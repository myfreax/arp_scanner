package com.myfreax.www.arp_scanner.db

import androidx.room.Entity

@Entity(primaryKeys = ["name", "mac"])
data class MacVendor(val name: String, val mac: String)