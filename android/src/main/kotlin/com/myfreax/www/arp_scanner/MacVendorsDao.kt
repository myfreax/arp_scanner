package com.myfreax.www.arp_scanner

import androidx.room.Dao
import androidx.room.Query

@Dao
interface MacVendorsDao {
    @Query("SELECT * FROM macvendor WHERE mac = :mac")
    fun findByMac(mac: String): MacVendor
}
