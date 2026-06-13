package com.widt.utils

object OuiDatabase {
    
    // OUI (Organizationally Unique Identifier) to Vendor mapping
    // Source: IEEE OUI database
    private val ouiMap = mapOf(
        // TP-LINK
        "C8:3A:35" to "TP-LINK",
        "50:C7:BF" to "TP-LINK",
        "60:32:B1" to "TP-LINK",
        "F4:F2:6D" to "TP-LINK",
        "14:CC:20" to "TP-LINK",
        "28:F3:66" to "TP-LINK",
        "30:46:9A" to "TP-LINK",
        "38:83:45" to "TP-LINK",
        "40:16:7E" to "TP-LINK",
        "48:5B:39" to "TP-LINK",
        "54:27:1E" to "TP-LINK",
        "5C:57:C8" to "TP-LINK",
        "64:09:80" to "TP-LINK",
        "68:72:51" to "TP-LINK",
        "70:62:B8" to "TP-LINK",
        "78:A3:E4" to "TP-LINK",
        "80:12:42" to "TP-LINK",
        "84:16:F9" to "TP-LINK",
        "88:2A:5F" to "TP-LINK",
        "8C:21:0A" to "TP-LINK",
        "94:0C:6D" to "TP-LINK",
        "98:09:CF" to "TP-LINK",
        "9C:9C:1C" to "TP-LINK",
        "A0:99:9B" to "TP-LINK",
        "A4:F4:91" to "TP-LINK",
        "A8:15:4D" to "TP-LINK",
        "AC:84:C6" to "TP-LINK",
        "B0:48:7A" to "TP-LINK",
        "B4:75:0E" to "TP-LINK",
        "B8:48:3A" to "TP-LINK",
        "BC:46:99" to "TP-LINK",
        "C0:25:06" to "TP-LINK",
        "C4:6E:1F" to "TP-LINK",
        "C8:BE:19" to "TP-LINK",
        "CC:6D:A1" to "TP-LINK",
        "D0:17:C2" to "TP-LINK",
        "D4:5D:DF" to "TP-LINK",
        "D8:4D:52" to "TP-LINK",
        "DC:9F:DB" to "TP-LINK",
        "E0:3F:49" to "TP-LINK",
        "E4:6B:7F" to "TP-LINK",
        "E8:B4:11" to "TP-LINK",
        "EC:61:1E" to "TP-LINK",
        "F0:5C:9B" to "TP-LINK",
        "F4:CA:E5" to "TP-LINK",
        "F8:D1:11" to "TP-LINK",
        "FC:2A:9B" to "TP-LINK",
        
        // Tenda
        "D8:32:14" to "Tenda",
        "C8:02:8F" to "Tenda",
        "98:0D:67" to "Tenda",
        "28:F6:13" to "Tenda",
        "58:3C:25" to "Tenda",
        "68:DB:54" to "Tenda",
        "88:8C:92" to "Tenda",
        "94:1D:BC" to "Tenda",
        "A8:48:FA" to "Tenda",
        "BC:2F:97" to "Tenda",
        "C4:37:8C" to "Tenda",
        "D4:6A:91" to "Tenda",
        "E8:4D:9C" to "Tenda",
        "EC:A1:BE" to "Tenda",
        "F0:2C:85" to "Tenda",
        
        // TOTOLINK
        "20:6D:31" to "TOTOLINK",
        "50:3E:AA" to "TOTOLINK",
        "00:11:6B" to "TOTOLINK",
        "04:3D:2D" to "TOTOLINK",
        "08:36:C9" to "TOTOLINK",
        "0C:22:0B" to "TOTOLINK",
        "10:7B:44" to "TOTOLINK",
        "14:75:90" to "TOTOLINK",
        "18:A9:05" to "TOTOLINK",
        "1C:1D:20" to "TOTOLINK",
        "20:68:6D" to "TOTOLINK",
        "24:62:AB" to "TOTOLINK",
        "28:32:1D" to "TOTOLINK",
        "2C:AB:33" to "TOTOLINK",
        "30:3A:64" to "TOTOLINK",
        
        // Huawei
        "5C:DB:70" to "Huawei",
        "08:E8:4F" to "Huawei",
        "00:1E:10" to "Huawei",
        "04:EE:03" to "Huawei",
        "08:29:3A" to "Huawei",
        "0C:96:BF" to "Huawei",
        "10:4C:A9" to "Huawei",
        "14:14:4B" to "Huawei",
        "18:0A:12" to "Huawei",
        "1C:3E:84" to "Huawei",
        "20:4E:7F" to "Huawei",
        "24:07:57" to "Huawei",
        "28:6C:44" to "Huawei",
        "2C:75:4F" to "Huawei",
        "30:0B:5A" to "Huawei",
        
        // ZTE
        "88:28:B3" to "ZTE",
        "8C:21:0A" to "ZTE",
        "00:1C:37" to "ZTE",
        "04:2B:52" to "ZTE",
        "08:67:4E" to "ZTE",
        "0C:82:68" to "ZTE",
        "10:27:56" to "ZTE",
        "14:1E:60" to "ZTE",
        "18:03:2F" to "ZTE",
        "1C:1C:1C" to "ZTE",
        "20:52:AF" to "ZTE",
        "24:4C:0F" to "ZTE",
        "28:51:32" to "ZTE",
        "2C:21:3B" to "ZTE",
        "30:32:8D" to "ZTE",
        
        // Cisco / Linksys
        "00:0C:41" to "Linksys",
        "00:18:F8" to "Linksys",
        "00:1A:70" to "Linksys",
        "00:1D:7E" to "Linksys",
        "00:1E:E5" to "Linksys",
        "00:22:6B" to "Linksys",
        "00:24:C4" to "Linksys",
        "00:25:9C" to "Linksys",
        "0C:37:96" to "Linksys",
        "10:C6:1F" to "Linksys",
        "14:91:82" to "Linksys",
        "18:3B:7D" to "Linksys",
        "1C:65:9C" to "Linksys",
        "20:72:AF" to "Linksys",
        "24:7E:68" to "Linksys",
        
        // Netgear
        "00:04:20" to "Netgear",
        "00:09:5B" to "Netgear",
        "00:14:6C" to "Netgear",
        "00:18:4D" to "Netgear",
        "00:1C:1E" to "Netgear",
        "00:20:4C" to "Netgear",
        "00:24:99" to "Netgear",
        "08:96:D7" to "Netgear",
        "0C:84:DC" to "Netgear",
        "10:98:C3" to "Netgear",
        "14:59:C0" to "Netgear",
        "18:59:33" to "Netgear",
        "1C:49:7B" to "Netgear",
        "20:8D:7C" to "Netgear",
        "24:15:9D" to "Netgear",
        
        // Asus
        "00:0C:6E" to "ASUS",
        "00:18:F3" to "ASUS",
        "00:1D:60" to "ASUS",
        "00:22:15" to "ASUS",
        "00:24:8D" to "ASUS",
        "04:7E:76" to "ASUS",
        "08:6D:41" to "ASUS",
        "0C:41:4A" to "ASUS",
        "10:7B:EF" to "ASUS",
        "14:7D:DA" to "ASUS",
        "18:31:BF" to "ASUS",
        "1C:5A:C8" to "ASUS",
        "20:F2:90" to "ASUS",
        "24:0A:64" to "ASUS",
        "28:C2:DD" to "ASUS",
        
        // D-Link
        "00:0F:3D" to "D-Link",
        "00:13:46" to "D-Link",
        "00:17:9A" to "D-Link",
        "00:1B:11" to "D-Link",
        "00:1E:6E" to "D-Link",
        "00:22:B0" to "D-Link",
        "00:26:5E" to "D-Link",
        "04:92:26" to "D-Link",
        "08:10:74" to "D-Link",
        "0C:37:DC" to "D-Link",
        "10:78:D2" to "D-Link",
        "14:C2:13" to "D-Link",
        "18:4D:26" to "D-Link",
        "1C:54:64" to "D-Link",
        "20:2B:C1" to "D-Link",
        
        // Xiaomi
        "88:C3:97" to "Xiaomi",
        "8C:BE:BE" to "Xiaomi",
        "90:2E:16" to "Xiaomi",
        "94:90:7D" to "Xiaomi",
        "98:2C:BE" to "Xiaomi",
        "9C:9E:2B" to "Xiaomi",
        "A0:E4:53" to "Xiaomi",
        "A4:63:24" to "Xiaomi",
        "A8:54:B2" to "Xiaomi",
        "AC:B3:7C" to "Xiaomi",
        "B0:FC:0D" to "Xiaomi",
        "B4:0C:25" to "Xiaomi",
        "B8:9A:2A" to "Xiaomi",
        "BC:0B:A4" to "Xiaomi",
        "C0:2A:D0" to "Xiaomi",
        
        // Samsung
        "00:12:37" to "Samsung",
        "00:1C:C0" to "Samsung",
        "00:23:69" to "Samsung",
        "04:5E:CF" to "Samsung",
        "08:EC:8C" to "Samsung",
        "0C:0B:0E" to "Samsung",
        "10:29:8D" to "Samsung",
        "14:51:C7" to "Samsung",
        "18:68:CC" to "Samsung",
        "1C:10:C3" to "Samsung",
        "20:12:6D" to "Samsung",
        "24:0A:2E" to "Samsung",
        "28:0B:E9" to "Samsung",
        "2C:19:99" to "Samsung",
        "30:16:3F" to "Samsung",
        
        // Google
        "00:1A:11" to "Google",
        "00:1C:2D" to "Google",
        "00:23:6F" to "Google",
        "04:AB:36" to "Google",
        "08:9E:01" to "Google",
        "0C:8B:FD" to "Google",
        "10:9F:A9" to "Google",
        "14:0A:A0" to "Google",
        "18:1B:EB" to "Google",
        "1C:4D:E2" to "Google",
        "20:46:AC" to "Google",
        "24:77:03" to "Google",
        "28:18:78" to "Google",
        "2C:38:20" to "Google",
        "30:23:03" to "Google",
        
        // Apple
        "00:1C:B3" to "Apple",
        "00:22:32" to "Apple",
        "00:25:00" to "Apple",
        "04:0C:CE" to "Apple",
        "08:00:07" to "Apple",
        "0C:1D:AF" to "Apple",
        "10:40:F3" to "Apple",
        "14:2D:27" to "Apple",
        "18:64:72" to "Apple",
        "1C:36:BB" to "Apple",
        "20:72:C0" to "Apple",
        "24:92:7E" to "Apple",
        "28:CF:DA" to "Apple",
        "2C:2A:6A" to "Apple",
        "30:13:74" to "Apple"
    )
    
    fun lookup(bssid: String): String? {
        val normalized = bssid.uppercase()
        return ouiMap.entries.firstOrNull { normalized.startsWith(it.key) }?.value
    }
    
    fun getDefaultPasswords(vendor: String): List<String> {
        return when (vendor) {
            "TP-LINK" -> listOf("12345678", "adminadmin", "password", "tplink123")
            "Tenda" -> listOf("12345678", "tendawifi", "tenda123", "admin")
            "TOTOLINK" -> listOf("1234567890", "totolink123", "admin")
            "Huawei" -> listOf("12345678", "huawei123", "admin")
            "ZTE" -> listOf("12345678", "zte12345", "admin")
            "Linksys" -> listOf("admin", "password", "linksys", "12345678")
            "Netgear" -> listOf("password", "12345678", "admin", "netgear")
            "ASUS" -> listOf("admin", "password", "12345678", "asus")
            "D-Link" -> listOf("admin", "password", "12345678", "dlink")
            "Xiaomi" -> listOf("12345678", "xiaomi", "admin", "password")
            else -> emptyList()
        }
    }
}