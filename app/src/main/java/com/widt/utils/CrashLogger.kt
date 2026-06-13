package com.widt.utils

import android.content.Context
import android.os.Build
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CrashLogger {
    private const val TAG = "CrashLogger"
    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return
        isInitialized = true

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                writeCrashToFile(context, thread, throwable)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to write crash to file", e)
            }

            // Forward to default handler
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun writeCrashToFile(context: Context, thread: Thread, throwable: Throwable) {
        try {
            val logDir = context.getExternalFilesDir(null) ?: context.filesDir
            val logFile = File(logDir, "crash_log.txt")

            val sw = StringWriter()
            val pw = PrintWriter(sw)
            throwable.printStackTrace(pw)
            val stackTrace = sw.toString()

            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val writer = FileWriter(logFile, true) // Append mode

            writer.use {
                it.write("========================================\n")
                it.write("Timestamp: $timestamp\n")
                it.write("Device: ${Build.MANUFACTURER} ${Build.MODEL} (Android ${Build.VERSION.RELEASE}, API ${Build.VERSION.SDK_INT})\n")
                it.write("Thread: ${thread.name} (ID: ${thread.id})\n")
                it.write("Exception: ${throwable.javaClass.name}\n")
                it.write("Message: ${throwable.message}\n")
                it.write("Stack Trace:\n")
                it.write(stackTrace)
                it.write("========================================\n\n")
            }
            Log.d(TAG, "Crash log written to: ${logFile.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Error writing crash log", e)
        }
    }
}
