package com.widt.utils

import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import com.widt.model.WifiNetwork

object WifiUtils {
    
    fun getWifiManager(context: Context): WifiManager? {
        return context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
    }
    
    fun isWifiEnabled(context: Context): Boolean {
        return try {
            getWifiManager(context)?.isWifiEnabled == true
        } catch (e: SecurityException) {
            false
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Triggers a WiFi scan and always returns true so the caller proceeds to read results.
     *
     * On Android 9+ (API 28+), WifiManager.startScan() is deprecated and heavily throttled.
     * On ColorOS (OPPO), it almost always returns false even with proper permissions.
     * The return value is therefore meaningless — we call it as a "hint" to the OS
     * and always proceed to read scanResults regardless of the outcome.
     * Fresh results will arrive via SCAN_RESULTS_AVAILABLE_ACTION broadcast.
     */
    fun startScan(context: Context): Boolean {
        try {
            val wifiManager = getWifiManager(context) ?: return false
            // Call startScan() as a hint to the OS. Ignore the return value —
            // it is throttled to false on most Android 9+ / OEM devices.
            @Suppress("DEPRECATION")
            wifiManager.startScan()
        } catch (e: SecurityException) {
            // No Location permission — cannot scan
            return false
        } catch (e: Exception) {
            // Ignore other exceptions (throttle-related RuntimeException on OEM)
        }
        // Always return true: caller will read scanResults immediately after
        return true
    }
    
    fun getScanResults(context: Context): List<WifiNetwork> {
        return try {
            val wifiManager = getWifiManager(context) ?: return emptyList()
            val results = wifiManager.scanResults ?: return emptyList()
            
            results
                .filter { it.SSID != null && it.SSID.isNotBlank() }
                .mapNotNull { result ->
                    val vendor = OuiDatabase.lookup(result.BSSID)
                    WifiNetwork.fromScanResult(result, vendor)
                }
                .distinctBy { it.bssid }
                .sortedByDescending { it.level }
        } catch (e: SecurityException) {
            emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun getEncryptionType(capabilities: String): String {
        return when {
            capabilities.contains("WPA3") -> "WPA3"
            capabilities.contains("WPA2") -> "WPA2"
            capabilities.contains("WPA") -> "WPA"
            capabilities.contains("WEP") -> "WEP"
            else -> "Open"
        }
    }
    
    fun hasWps(capabilities: String): Boolean {
        return capabilities.contains("[WPS]") || capabilities.contains("WPS")
    }
    
    fun getChannelFromFrequency(frequency: Int): Int {
        return when {
            frequency in 2412..2484 -> (frequency - 2412) / 5 + 1
            frequency in 5170..5825 -> (frequency - 5170) / 5 + 34
            else -> 0
        }
    }
    
    fun getBandFromFrequency(frequency: Int): String {
        return if (frequency < 5000) "2.4 GHz" else "5 GHz"
    }
    
    fun getSignalStrengthLevel(level: Int): Int {
        return when {
            level > -50 -> 4 // Excellent
            level > -60 -> 3 // Good
            level > -70 -> 2 // Fair
            level > -80 -> 1 // Weak
            else -> 0 // Poor
        }
    }
    
    fun getSignalStrengthPercentage(level: Int): Int {
        // dBm ranges from -100 to -30
        val normalized = (level + 100).coerceIn(0, 70)
        return (normalized * 100 / 70).coerceIn(0, 100)
    }
    
    fun suggestBestChannel(networks: List<WifiNetwork>, band: String = "2.4 GHz"): Int {
        val filtered = networks.filter { it.band == band }
        val channelUsage = mutableMapOf<Int, Int>()
        
        for (network in filtered) {
            val channel = network.channel
            // Channels have 20 MHz width, so adjacent channels also interfere
            for (offset in -2..2) {
                val adjChannel = channel + offset
                if (adjChannel in 1..14) {
                    channelUsage[adjChannel] = (channelUsage[adjChannel] ?: 0) + 1
                }
            }
        }
        
        // Recommended channels: 1, 6, 11 for 2.4 GHz to minimize overlap
        val recommended = when (band) {
            "2.4 GHz" -> listOf(1, 6, 11)
            else -> listOf(36, 40, 44, 48, 149, 153, 157, 161)
        }
        
        return recommended.minByOrNull { channelUsage[it] ?: 0 } ?: recommended.first()
    }
}