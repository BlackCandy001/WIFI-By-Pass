package com.widt.tester

import android.app.Service
import android.content.Intent
import android.os.IBinder

class TesterService : Service() {
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // TODO: Implement foreground service for dictionary testing
        return START_NOT_STICKY
    }
}