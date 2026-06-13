package com.widt.tester.engine

import android.content.Context
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import androidx.annotation.RequiresApi

class DeviceOwnerEngine(private val context: Context) : ConnectionEngine {
    
    override val name = "Device Owner"
    override val isAvailable: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                isDeviceOwner(context)
    override val requiresSetup = true
    override val speedRating = 5
    override val description = "No dialog, very fast. Requires ADB setup once."
    
    @RequiresApi(Build.VERSION_CODES.Q)
    override suspend fun testPassword(ssid: String, password: String, timeoutMs: Long): TestResult {
        return kotlinx.coroutines.withTimeoutOrNull(timeoutMs) {
            try {
                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                
                val suggestion = WifiNetworkSuggestion.Builder()
                    .setSsid(ssid)
                    .setWpa2Passphrase(password)
                    .build()
                
                val result = wifiManager.addNetworkSuggestions(listOf(suggestion))
                
                if (result == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
                    Thread.sleep(2000)
                    val connectionInfo = wifiManager.connectionInfo
                    if (connectionInfo.ssid == "\"$ssid\"") {
                        TestResult.Connected
                    } else {
                        TestResult.WrongPassword
                    }
                } else {
                    TestResult.Error("Device Owner failed to add suggestion, status code: $result")
                }
            } catch (e: Exception) {
                TestResult.Error(e.message ?: "Unknown error")
            }
        } ?: TestResult.Timeout
    }
    
    private fun isDeviceOwner(context: Context): Boolean {
        return try {
            val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE)
            val method = devicePolicyManager?.javaClass?.getMethod("isDeviceOwnerApp", String::class.java)
            method?.invoke(devicePolicyManager, context.packageName) as? Boolean ?: false
        } catch (e: Exception) {
            false
        }
    }
}