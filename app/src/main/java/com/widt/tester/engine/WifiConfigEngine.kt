package com.widt.tester.engine

import android.content.Context
import android.net.wifi.WifiManager
import rikka.shizuku.Shizuku

class WifiConfigEngine(private val context: Context) : ConnectionEngine {
    
    override val name = "cmd wifi (Shizuku)"
    override val isAvailable: Boolean
        get() = com.widt.common.shizuku.ShizukuHelper.isShizukuRunning() &&
                com.widt.common.shizuku.ShizukuHelper.hasPermission()
    override val requiresSetup = true
    override val speedRating = 5
    override val description = "Uses cmd wifi command via Shizuku. Fastest method."
    
    override suspend fun testPassword(ssid: String, password: String, timeoutMs: Long): TestResult {
        return kotlinx.coroutines.withTimeoutOrNull(timeoutMs) {
            try {
                val command = arrayOf(
                    "sh", "-c",
                    "cmd wifi connect-network \"$ssid\" wpa2 \"$password\""
                )
                val process = Shizuku.newProcess(command, null, null)
                val exitCode = process.waitFor()
                
                if (exitCode == 0) {
                    // Wait for connection to establish
                    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    var attempts = 0
                    while (attempts < 10) {
                        if (wifiManager.connectionInfo.ssid == "\"$ssid\"") {
                            return@withTimeoutOrNull TestResult.Connected
                        }
                        Thread.sleep(300)
                        attempts++
                    }
                    TestResult.WrongPassword
                } else {
                    TestResult.Error("Shell command failed with exit code: $exitCode")
                }
            } catch (e: Exception) {
                TestResult.Error(e.message ?: "Unknown error")
            }
        } ?: TestResult.Timeout
    }
}