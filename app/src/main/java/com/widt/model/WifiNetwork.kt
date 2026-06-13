package com.widt.model

import android.net.wifi.ScanResult

data class WifiNetwork(
    val ssid: String,
    val bssid: String,
    val capabilities: String,
    val frequency: Int,
    val level: Int,
    val timestamp: Long,
    val vendor: String? = null
) {
    val signalStrength: SignalStrength
        get() = when {
            level > -50 -> SignalStrength.EXCELLENT
            level > -60 -> SignalStrength.GOOD
            level > -70 -> SignalStrength.FAIR
            level > -80 -> SignalStrength.WEAK
            else -> SignalStrength.POOR
        }
    
    val encryptionType: String
        get() = when {
            capabilities.contains("WPA3") -> "WPA3"
            capabilities.contains("WPA2") -> "WPA2"
            capabilities.contains("WPA") -> "WPA"
            capabilities.contains("WEP") -> "WEP"
            else -> "Open"
        }
    
    val channel: Int
        get() = when {
            frequency in 2412..2484 -> (frequency - 2412) / 5 + 1
            frequency in 5170..5825 -> (frequency - 5170) / 5 + 34
            else -> 0
        }
    
    val band: String
        get() = if (frequency < 5000) "2.4 GHz" else "5 GHz"
    
    // toScanResult() removed because ScanResult is immutable
    // Use the original ScanResult reference if needed
    
    companion object {
        fun fromScanResult(result: ScanResult, vendor: String? = null): WifiNetwork {
            return WifiNetwork(
                ssid = result.SSID ?: "(hidden)",
                bssid = result.BSSID,
                capabilities = result.capabilities,
                frequency = result.frequency,
                level = result.level,
                timestamp = result.timestamp,
                vendor = vendor
            )
        }
    }
}

enum class SignalStrength {
    EXCELLENT, GOOD, FAIR, WEAK, POOR;
    
    fun getColorRes(): Int {
        return when (this) {
            EXCELLENT -> android.R.color.holo_green_dark
            GOOD -> android.R.color.holo_green_light
            FAIR -> android.R.color.holo_orange_light
            WEAK -> android.R.color.holo_orange_dark
            POOR -> android.R.color.holo_red_dark
        }
    }
}