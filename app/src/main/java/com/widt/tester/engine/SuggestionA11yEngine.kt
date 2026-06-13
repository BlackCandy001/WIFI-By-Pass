package com.widt.tester.engine

import android.content.Context
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import androidx.annotation.RequiresApi

class SuggestionA11yEngine(private val context: Context) : ConnectionEngine {
    
    override val name = "Suggestion + Accessibility"
    override val isAvailable: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                com.widt.common.a11y.AutoClickService.isEnabled
    override val requiresSetup = true
    override val speedRating = 3
    override val description = "Shows dialog but auto-click can handle it. Requires Accessibility Service."
    
    @RequiresApi(Build.VERSION_CODES.Q)
    override suspend fun testPassword(ssid: String, password: String, timeoutMs: Long): TestResult {
        return kotlinx.coroutines.withTimeoutOrNull(timeoutMs) {
            try {
                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                
                val suggestion = WifiNetworkSuggestion.Builder()
                    .setSsid(ssid)
                    .setWpa2Passphrase(password)
                    .setIsAppInteractionRequired(true)  // This shows dialog
                    .build()
                
                val result = wifiManager.addNetworkSuggestions(listOf(suggestion))
                
                if (result == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
                    // Wait for connection (auto-click service will handle dialog)
                    Thread.sleep(3000)
                    val connectionInfo = wifiManager.connectionInfo
                    if (connectionInfo.ssid == "\"$ssid\"") {
                        TestResult.Connected
                    } else {
                        TestResult.WrongPassword
                    }
                } else {
                    TestResult.Error("Failed to add network suggestion, status code: $result")
                }
            } catch (e: Exception) {
                TestResult.Error(e.message ?: "Unknown error")
            }
        } ?: TestResult.Timeout
    }
}