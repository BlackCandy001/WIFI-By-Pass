package com.widt.tester.engine

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EngineSelector(private val context: Context) {
    
    private val engines: List<ConnectionEngine> = listOf(
        CmdWifiEngine(context),
        WifiConfigEngine(context),
        DeviceOwnerEngine(context),
        SuggestionA11yEngine(context),
        ManualEngine(context)
    )
    
    fun getBestEngine(): ConnectionEngine? {
        // Check availability dynamically each time
        return engines
            .filter { it.isAvailable }
            .maxByOrNull { it.speedRating }
    }
    
    fun getAllEngines(): List<EngineStatus> {
        return engines.map { 
            EngineStatus(it.name, it.isAvailable, it.requiresSetup, it.speedRating, it.description)
        }
    }
    
    suspend fun testWithBestEngine(ssid: String, password: String, timeoutMs: Long = 3000): TestResult {
        val engine = getBestEngine()
        return if (engine != null) {
            withContext(Dispatchers.IO) {
                engine.testPassword(ssid, password, timeoutMs)
            }
        } else {
            TestResult.Error("No connection engine available")
        }
    }
}