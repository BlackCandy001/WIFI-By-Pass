package com.widt

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.widt.common.shizuku.ShizukuHelper
import com.widt.databinding.ActivityMainBinding
import com.widt.utils.CrashLogger
import com.widt.utils.LanguageHelper
import com.widt.utils.NotificationHelper
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.NEARBY_WIFI_DEVICES)
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }.toTypedArray()
    }

    override fun attachBaseContext(newBase: android.content.Context) {
        super.attachBaseContext(LanguageHelper.applyLanguageOnStart(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Init crash logger as early as possible
        CrashLogger.init(applicationContext)
        
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NotificationHelper.createChannel(this)
        checkPermissions()
        initShizuku()
        checkAndShowCrashLog()

        val navView: BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        navView.setupWithNavController(navController)
    }
    
    private fun initShizuku() {
        ShizukuHelper.addPermissionListener {
            runOnUiThread {
                if (!isFinishing && !isDestroyed) {
                    Toast.makeText(this, "Shizuku permission granted", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        ShizukuHelper.addBinderReceivedListener {
            // Only listen to binder received, do not automatically request permission here to avoid crash on startup
        }
    }

    private fun checkAndShowCrashLog() {
        val logDir = getExternalFilesDir(null) ?: filesDir
        val logFile = java.io.File(logDir, "crash_log.txt")
        if (logFile.exists()) {
            val logText = try {
                logFile.readText()
            } catch (e: Exception) {
                null
            }
            
            if (!logText.isNullOrEmpty()) {
                val scrollView = android.widget.ScrollView(this).apply {
                    val padding = (16 * resources.displayMetrics.density).toInt()
                    setPadding(padding, padding, padding, padding)
                    
                    val textView = android.widget.TextView(context).apply {
                        text = logText
                        textSize = 12f
                        setTextIsSelectable(true)
                        typeface = android.graphics.Typeface.MONOSPACE
                    }
                    addView(textView)
                }

                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Ứng dụng bị sập ở lần chạy trước")
                    .setView(scrollView)
                    .setCancelable(false)
                    .setPositiveButton("Sao chép & Xóa log") { _, _ ->
                        try {
                            val clipboard = getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("Crash Log", logText)
                            clipboard.setPrimaryClip(clip)
                            logFile.delete()
                            Toast.makeText(this, "Đã sao chép và xóa file log", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    .setNegativeButton("Xóa log") { _, _ ->
                        logFile.delete()
                        Toast.makeText(this, "Đã xóa file log", Toast.LENGTH_SHORT).show()
                    }
                    .setNeutralButton("Đóng") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    private fun checkPermissions() {
        val missingPermissions = REQUIRED_PERMISSIONS.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val deniedPermissions = permissions.filterIndexed { index, _ ->
                grantResults[index] != PackageManager.PERMISSION_GRANTED
            }
            if (deniedPermissions.isNotEmpty()) {
                Toast.makeText(
                    this,
                    "Some permissions denied. WiFi scanning may not work properly.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        ShizukuHelper.removePermissionListener()
        ShizukuHelper.removeBinderReceivedListener()
    }
}