package com.widt.tester

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import java.io.BufferedReader
import java.io.InputStreamReader

class DictionaryLoader(private val context: Context) {
    
    companion object {
        const val DEFAULT_DICT = "top400.txt"
    }
    
    fun loadDefaultDictionary(): List<String> {
        return loadFromRaw(DEFAULT_DICT)
    }
    
    fun loadFromRaw(fileName: String): List<String> {
        return try {
            val resourceId = context.resources.getIdentifier(fileName, "raw", context.packageName)
            if (resourceId == 0) return emptyList()
            
            val inputStream = context.resources.openRawResource(resourceId)
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.readLines().filter { it.isNotBlank() }
            // Note: WPA2 requires min 8 chars, but we keep all for educational purposes
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun loadFromFile(path: String): List<String> {
        return try {
            val file = java.io.File(path)
            if (!file.exists()) return emptyList()
            
            file.readLines().filter { it.isNotBlank() }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun loadFromUri(uri: Uri): List<String> {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return emptyList()
            val reader = BufferedReader(InputStreamReader(inputStream))
            val lines = reader.readLines().filter { it.isNotBlank() }
            inputStream.close()
            lines
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun saveCustomDictionary(passwords: List<String>, fileName: String): Boolean {
        return try {
            val file = java.io.File(context.filesDir, fileName)
            file.writeText(passwords.joinToString("\n"))
            true
        } catch (e: Exception) {
            false
        }
    }
    
    fun getAvailableDictionaries(): List<DictInfo> {
        val dicts = mutableListOf<DictInfo>()
        
        // Check raw resources
        val rawFiles = listOf("top400.txt", "vietnam.txt", "router_defaults.txt")
        rawFiles.forEach { name ->
            val id = context.resources.getIdentifier(name, "raw", context.packageName)
            if (id != 0) {
                dicts.add(DictInfo(name, "Raw resource", true))
            }
        }
        
        // Check files directory
        val filesDir = context.filesDir
        filesDir.listFiles()?.forEach { file ->
            if (file.name.endsWith(".txt")) {
                dicts.add(DictInfo(file.name, "Custom dictionary", true))
            }
        }
        
        return dicts
    }
    
    data class DictInfo(
        val name: String,
        val source: String,
        val isAvailable: Boolean
    )
}