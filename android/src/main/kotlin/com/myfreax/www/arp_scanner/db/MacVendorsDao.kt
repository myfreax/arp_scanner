package com.myfreax.www.arp_scanner.db

import androidx.room.Dao
import androidx.room.Query
import com.myfreax.www.arp_scanner.db.MacVendor

@Dao
interface MacVendorsDao {
    @Query("SELECT * FROM macvendor WHERE mac = :mac")
    fun findByMac(mac: String): MacVendor
}
