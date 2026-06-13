package com.widt.common.shizuku

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import rikka.shizuku.Shizuku

object ShizukuHelper {
    
    private const val SHIZUKU_PERMISSION_REQUEST_CODE = 100
    private var permissionGrantedListener: (() -> Unit)? = null
    private var binderReceivedListener: (() -> Unit)? = null
    
    private val shizukuBinderReceivedListener = Shizuku.OnBinderReceivedListener {
        binderReceivedListener?.invoke()
    }
    
    fun isShizukuRunning(): Boolean {
        return try {
            Shizuku.pingBinder()
        } catch (e: Throwable) {
            false
        }
    }
    
    fun getShizukuVersion(): String {
        return try {
            if (Shizuku.pingBinder() && hasPermission()) {
                "v${Shizuku.getVersion()}"
            } else if (Shizuku.pingBinder()) {
                "Running (Need Permission)"
            } else {
                "Not running"
            }
        } catch (e: Throwable) {
            "Error"
        }
    }
    
    fun hasPermission(): Boolean {
        return try {
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        } catch (e: Throwable) {
            false
        }
    }
    
    fun requestPermission() {
        if (!hasPermission() && isShizukuRunning()) {
            try {
                android.util.Log.d("ShizukuHelper", "Requesting Shizuku permission...")
                Shizuku.requestPermission(SHIZUKU_PERMISSION_REQUEST_CODE)
            } catch (e: Throwable) {
                android.util.Log.e("ShizukuHelper", "Error requesting Shizuku permission", e)
                e.printStackTrace()
            }
        }
    }
    
    private val requestPermissionResultListener = Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
        if (requestCode == SHIZUKU_PERMISSION_REQUEST_CODE && grantResult == PackageManager.PERMISSION_GRANTED) {
            permissionGrantedListener?.invoke()
        }
    }
    
    fun addPermissionListener(listener: (() -> Unit)?) {
        permissionGrantedListener = listener
        try {
            Shizuku.addRequestPermissionResultListener(requestPermissionResultListener)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
    
    fun removePermissionListener() {
        permissionGrantedListener = null
        try {
            Shizuku.removeRequestPermissionResultListener(requestPermissionResultListener)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
    
    fun addBinderReceivedListener(listener: () -> Unit) {
        binderReceivedListener = listener
        try {
            Shizuku.addBinderReceivedListener(shizukuBinderReceivedListener)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
    
    fun removeBinderReceivedListener() {
        binderReceivedListener = null
        try {
            Shizuku.removeBinderReceivedListener(shizukuBinderReceivedListener)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
    
    fun openShizukuSettings(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("shizuku://settings")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Throwable) {
            // Shizuku not installed
            try {
                val playIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://github.com/RikkaApps/Shizuku/releases")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(playIntent)
            } catch (e2: Throwable) {
                e2.printStackTrace()
            }
        }
    }
    
    fun getSetupGuide(): String {
        return """How to setup Shizuku:

Option 1: Wireless ADB (Android 11+)
1. Install Shizuku from GitHub
2. Enable Developer Options
3. Enable Wireless Debugging
4. Pair device with code
5. Start Shizuku

Option 2: ADB via PC
1. Connect phone to PC with USB
2. Run: adb shell sh /storage/emulated/0/Android/data/moe.shizuku.privileged.api/start.sh
3. Shizuku will start

After Shizuku is running, return to this app."""
    }
}