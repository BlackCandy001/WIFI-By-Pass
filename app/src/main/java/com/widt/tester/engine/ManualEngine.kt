package com.widt.tester.engine

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.provider.Settings
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class ManualEngine(private val context: Context) : ConnectionEngine {
    
    override val name = "Manual (User Action)"
    override val isAvailable: Boolean = true  // Always available
    override val requiresSetup = false
    override val speedRating = 1
    override val description = "Opens WiFi Settings for user to manually connect."
    
    override suspend fun testPassword(ssid: String, password: String, timeoutMs: Long): TestResult {
        return kotlinx.coroutines.withTimeoutOrNull(timeoutMs) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Connect to $ssid manually with password: $password",
                    Toast.LENGTH_LONG
                ).show()
                
                // Open WiFi settings
                val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            }
            
            // Wait for user to connect manually
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            var attempts = 0
            while (attempts < 40) {  // ~10 seconds max
                if (wifiManager.connectionInfo.ssid == "\"$ssid\"") {
                    return@withTimeoutOrNull TestResult.Connected
                }
                delay(250)
                attempts++
            }
            TestResult.WrongPassword
        } ?: TestResult.Timeout
    }
}