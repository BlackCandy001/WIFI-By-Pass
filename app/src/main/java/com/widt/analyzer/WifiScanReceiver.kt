package com.widt.analyzer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import com.widt.utils.WifiUtils

class WifiScanReceiver(
    private val onScanResults: (List<com.widt.model.WifiNetwork>) -> Unit
) : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
            val networks = WifiUtils.getScanResults(context)
            onScanResults(networks)
        }
    }
}