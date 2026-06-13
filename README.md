# WIDT - WiFi Inspector & Dictionary Tester

**WIDT** (WiFi Inspector & Dictionary Tester) is an Android application for educational purposes that helps users analyze WiFi networks and test the security of their own WiFi passwords.

> ⚠️ **DISCLAIMER**: This app is for **EDUCATIONAL PURPOSES ONLY**. Only use it to test YOUR OWN WiFi networks. Do NOT use to attack networks you don't own. The author is not responsible for any misuse.

---

## 📱 Features

### 1. WiFi Analyzer Pro
- Scan and display nearby WiFi networks
- Show SSID, BSSID, signal strength (dBm), channel, frequency
- Identify router vendor from BSSID (OUI database)
- Detect encryption type (WEP, WPA, WPA2, WPA3, Open)
- Filter by band (2.4 GHz / 5 GHz)
- Suggest best channel for less interference

### 2. Dictionary Tester
- Test password strength using dictionary attacks
- **5 connection engines** with auto-selection:
  - `cmd wifi` (Android 12+, requires Shizuku) - Fastest
  - `WifiConfiguration` (requires Shizuku) - Works on all versions
  - Device Owner (requires ADB setup once) - No dialog
  - Suggestion + Accessibility (auto-click fallback)
  - Manual (opens WiFi Settings)
- Progress bar with ETA
- Stop/resume testing
- Smart dictionary prioritization based on router vendor

### 3. Offline Cracker (Beta)
- PBKDF2 implementation for offline cracking
- Import PCAP files containing handshake
- Dictionary attack without continuous WiFi connection

### 4. Smart Dictionary Engine
- OUI database for router vendor identification
- Vendor-specific default passwords (TP-LINK, Tenda, Huawei, etc.)
- Vietnamese common passwords
- SSID-based pattern detection

### 5. Settings & Tools
- Shizuku integration for elevated permissions
- Dark/Light theme
- Custom dictionary file picker
- Accessibility Service for auto-click (fallback)

---

## 📋 Requirements

| Requirement | Minimum |
|-------------|---------|
| Android version | 6.0 (API 23) |
| RAM | 2GB |
| WiFi hardware | Required |
| Storage | 10MB + dictionary files |

### Optional for advanced features:
- **Shizuku** (for fast testing) - [Download](https://github.com/RikkaApps/Shizuku/releases)
- **Accessibility Service** (for auto-click fallback)
- **Device Owner** (for no-dialog testing)

---

## 🔧 Installation

### Option 1: Build from source
```bash
git clone https://github.com/yourusername/WIDT.git
cd WIDT
# Open in Android Studio
# Build → Build APK
```

### Option 2: Download APK
*(Coming soon - for educational purposes only)*

### Option 3: Install Shizuku (recommended for speed)
1. Download Shizuku from [GitHub](https://github.com/RikkaApps/Shizuku/releases)
2. Install and start Shizuku (wireless ADB or USB)
3. Open WIDT → Settings → Grant permission

---

## 🚀 Usage Guide

### Step 1: Grant Permissions
- Location permission (required for WiFi scanning on Android 8+)
- WiFi state and change permissions

### Step 2: Scan Networks
- Navigate to **Analyzer** tab
- Tap **"Scan WiFi"**
- View list of nearby networks with vendor information

### Step 3: Test a Network
- Tap on any network in the list
- Automatically navigates to **Tester** tab
- Select dictionary (default: top400.txt)
- Tap **"Start Test"**
- Wait for results

### Step 4: (Optional) Setup Shizuku for Speed
- Install Shizuku app
- Start Shizuku via wireless ADB or USB
- Grant permission when prompted
- Testing speed improves from ~8s to ~2s per password

---

## 📁 Dictionary Files

| File | Location | Description |
|------|----------|-------------|
| `top400.txt` | `/res/raw/` | Top 400 most common passwords worldwide |
| `vietnam.txt` | `/res/raw/` | Vietnamese common passwords |
| `router_defaults.txt` | `/res/raw/` | Default passwords by router vendor |
| Custom | User-selected | Any .txt file with one password per line |

---

## 🏗️ Architecture

```
WIDT/
├── analyzer/          # WiFi scanning and display
├── tester/            # Dictionary testing with Connection Engine
│   └── engine/        # 5 connection strategies
├── offline/           # PBKDF2 offline cracking
├── dictionary/        # Smart dictionary engine
├── settings/          # App settings
└── common/            # Shared utilities (Shizuku, Accessibility)
```

### Connection Engine (Auto-select best available)

| Engine | Speed | Requires | Dialog |
|--------|-------|----------|--------|
| cmd wifi | ⭐⭐⭐⭐⭐ | Shizuku + Android 12+ | ❌ |
| WifiConfiguration | ⭐⭐⭐⭐⭐ | Shizuku | ❌ |
| Device Owner | ⭐⭐⭐⭐⭐ | ADB (1 time) | ❌ |
| Suggestion + A11y | ⭐⭐⭐ | Accessibility | ✅ (auto-click) |
| Manual | ⭐ | Nothing | ✅ (user click) |

---

## ⚠️ Legal & Ethical Notice

- **DO NOT** use this app to attack networks you don't own
- **DO NOT** use for illegal activities
- **DO NOT** attempt to crack neighbors' WiFi
- **ONLY** test your own networks to improve security
- This app is for **educational purposes** to understand WiFi security

By using this app, you agree to:
- Use only on networks you own or have permission to test
- Accept full responsibility for your actions
- Not hold the author liable for any misuse

---

## 🐛 Known Issues & Limitations

| Issue | Status | Workaround |
|-------|--------|------------|
| WifiConfiguration deprecated on Android 10+ | ✅ Fixed | Use Shizuku or cmd wifi |
| Dialog confirmation for new networks | ✅ Fixed | Use Shizuku or auto-click |
| WiFi scan throttled (4 scans/2 min) | ⚠️ Limitation | Manual refresh only |
| Offline cracking incomplete | 🚧 In progress | Use online tester instead |
| PC Bridge not implemented | 📅 Planned | Coming soon |

---

## 📊 Performance

| Dictionary size | Time (no Shizuku) | Time (with Shizuku) |
|----------------|-------------------|---------------------|
| 50 passwords | ~4 minutes | ~1.5 minutes |
| 100 passwords | ~8 minutes | ~3 minutes |
| 400 passwords | ~32 minutes | ~12 minutes |

*Times are estimates based on 3-5s per attempt (no Shizuku) vs 2s per attempt (with Shizuku)*

---

## 🤝 Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Submit a pull request

**Areas needing help:**
- Full offline cracking implementation (PTK + MIC)
- PC Bridge engine (Python server)
- More dictionary files
- UI improvements

---

## 📄 License

This project is for **educational purposes only**.  
No explicit license - contact author for permissions.

---

## 📞 Contact

For questions or support:
- GitHub Issues: [Create an issue](https://github.com/yourusername/WIDT/issues)
- Email: (coming soon)

---

## 🙏 Acknowledgments

- Shizuku by RikkaApps for elevated permissions without root
- MPAndroidChart by PhilJay for charting library
- Project references from analyzed open-source WiFi tools

---

**Made with ❤️ for educational purposes**  
*Remember: With great power comes great responsibility. Use wisely.*