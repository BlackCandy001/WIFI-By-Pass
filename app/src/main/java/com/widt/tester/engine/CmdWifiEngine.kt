package com.widt.tester.engine

import android.content.Context
import android.os.Build
import rikka.shizuku.Shizuku
import kotlinx.coroutines.withTimeoutOrNull

class CmdWifiEngine(private val context: Context) : ConnectionEngine {
    
    override val name = "cmd wifi (Shizuku)"
    override val isAvailable: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                com.widt.common.shizuku.ShizukuHelper.isShizukuRunning() &&
                com.widt.common.shizuku.ShizukuHelper.hasPermission()
    override val requiresSetup = true
    override val speedRating = 5
    override val description = "Fastest method (Android 12+). Requires Shizuku running."
    
    override suspend fun testPassword(ssid: String, password: String, timeoutMs: Long): TestResult {
        return withTimeoutOrNull(timeoutMs) {
            try {
                // Command: cmd wifi connect-network <SSID> wpa2 <password>
                val command = arrayOf("sh", "-c", "cmd wifi connect-network \"$ssid\" wpa2 \"$password\"")
                val process = Shizuku.newProcess(command, null, null)
                val exitCode = process.waitFor()
                
                if (exitCode == 0) {
                    // Check if actually connected
                    Thread.sleep(1000)
                    TestResult.Connected
                } else {
                    TestResult.Error("Shell command failed with exit code: $exitCode")
                }
            } catch (e: Exception) {
                TestResult.Error(e.message ?: "Unknown error")
            }
        } ?: TestResult.Timeout
    }
}