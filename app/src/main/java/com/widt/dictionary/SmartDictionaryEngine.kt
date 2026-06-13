package com.widt.dictionary

import com.widt.model.WifiNetwork
import com.widt.utils.OuiDatabase

object SmartDictionaryEngine {
    
    // Vietnamese common passwords
    private val VN_TOP_PASSWORDS = listOf(
        "12345678", "123456789", "1234567890", "password",
        "1234567", "12345678910", "qwerty123", "11111111",
        "00000000", "abcdefgh", "matkhau1", "admin123",
        "viettel123", "viettel1", "fpt12345", "vnpt1234"
    )
    
    // Router default passwords by vendor
    private val DEFAULT_PASSWORDS = mapOf(
        "TP-LINK" to listOf("12345678", "adminadmin", "password", "tplink123"),
        "Tenda" to listOf("12345678", "tendawifi", "tenda123", "admin"),
        "TOTOLINK" to listOf("1234567890", "totolink123", "admin"),
        "Huawei" to listOf("12345678", "huawei123", "admin"),
        "ZTE" to listOf("12345678", "zte12345", "admin"),
        "Linksys" to listOf("admin", "password", "linksys", "12345678"),
        "Netgear" to listOf("password", "12345678", "admin", "netgear"),
        "ASUS" to listOf("admin", "password", "12345678", "asus"),
        "D-Link" to listOf("admin", "password", "12345678", "dlink"),
        "Xiaomi" to listOf("12345678", "xiaomi", "admin", "password")
    )
    
    // SSID-based patterns
    private fun getSsidPatterns(ssid: String): List<String> {
        val lower = ssid.lowercase()
        return when {
            lower.contains("viettel") -> listOf("viettel123", "viettel1", "viettel@123")
            lower.contains("vnpt") -> listOf("vnpt1234", "vnpt@123", "12345678")
            lower.contains("fpt") -> listOf("fpt12345", "fpt@123", "1234567890")
            lower.contains("tendawifi") -> listOf("tendawifi", "tenda123", "12345678")
            else -> emptyList()
        }
    }
    
    fun optimize(network: WifiNetwork, fullDict: List<String>): List<String> {
        val prioritized = mutableListOf<String>()
        
        // 1. Add vendor default passwords
        network.vendor?.let { vendor ->
            DEFAULT_PASSWORDS[vendor]?.let { prioritized.addAll(it) }
        }
        
        // 2. Add SSID-based patterns
        prioritized.addAll(getSsidPatterns(network.ssid))
        
        // 3. Add Vietnamese top passwords
        prioritized.addAll(VN_TOP_PASSWORDS)
        
        // 4. Add remaining dictionary entries (excluding duplicates)
        val remaining = fullDict.filter { !prioritized.contains(it) }
        
        // Return prioritized + remaining (limit to reasonable size)
        return (prioritized + remaining).distinct().take(500)
    }
    
    fun getRecommendedDictionaries(): List<Pair<String, String>> {
        return listOf(
            "top400.txt" to "Top 400 most common passwords worldwide",
            "vietnam.txt" to "Common Vietnamese passwords",
            "router-defaults.txt" to "Default router passwords by vendor",
            "custom.txt" to "Your custom dictionary"
        )
    }
}