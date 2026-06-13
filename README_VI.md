# WIDT - WiFi Inspector & Dictionary Tester

**Tác giả: Black Candy 🍫**

**WIDT** (WiFi Inspector & Dictionary Tester) là ứng dụng Android cho mục đích học tập, giúp người dùng phân tích mạng WiFi và kiểm tra độ an toàn của mật khẩu WiFi của chính mình.

> ⚠️ **TUYÊN BỐ MIỄN TRỪ TRÁCH NHIỆM**: Ứng dụng này chỉ dùng cho **MỤC ĐÍCH HỌC TẬP**. Chỉ sử dụng để kiểm tra mạng WiFi CỦA BẠN. Không sử dụng để tấn công mạng của người khác. Tác giả không chịu trách nhiệm cho bất kỳ hành vi lạm dụng nào.

---

## 📱 Tính Năng

### 1. WiFi Analyzer Pro
- Quét và hiển thị các mạng WiFi xung quanh
- Hiển thị SSID, BSSID, cường độ tín hiệu (dBm), kênh, tần số
- Nhận diện nhà sản xuất router từ BSSID (cơ sở dữ liệu OUI)
- Phát hiện loại mã hóa (WEP, WPA, WPA2, WPA3, Open)
- Lọc theo băng tần (2.4 GHz / 5 GHz)
- Đề xuất kênh ít nhiễu nhất

### 2. Dictionary Tester
- Kiểm tra độ mạnh mật khẩu bằng dictionary attack
- **5 connection engines** với cơ chế tự động chọn:
  - `cmd wifi` (Android 12+, cần Shizuku) - Nhanh nhất
  - `WifiConfiguration` (cần Shizuku) - Hoạt động trên mọi phiên bản
  - Device Owner (cần ADB 1 lần) - Không có dialog
  - Suggestion + Accessibility (auto-click dự phòng)
  - Manual (mở Settings WiFi)
- Thanh tiến trình với thời gian ước tính
- Dừng / tiếp tục kiểm tra
- Ưu tiên mật khẩu thông minh dựa trên nhà sản xuất router

### 3. Offline Cracker (Beta)
- Triển khai PBKDF2 cho bẻ khóa offline
- Nhập file PCAP chứa handshake
- Tấn công dictionary mà không cần kết nối WiFi liên tục

### 4. Smart Dictionary Engine
- Cơ sở dữ liệu OUI để nhận diện nhà sản xuất router
- Mật khẩu mặc định theo nhà sản xuất (TP-LINK, Tenda, Huawei, ...)
- Mật khẩu phổ biến tại Việt Nam
- Phát hiện pattern dựa trên SSID

### 5. Cài đặt & Công cụ
- Tích hợp Shizuku để có quyền cao hơn (không cần root)
- Giao diện sáng / tối
- Chọn file dictionary tùy chỉnh
- Dịch vụ Accessibility cho auto-click (dự phòng)

---

## 📋 Yêu Cầu Hệ Thống

| Yêu cầu | Tối thiểu |
|---------|-----------|
| Phiên bản Android | 6.0 (API 23) |
| RAM | 2GB |
| Phần cứng WiFi | Bắt buộc |
| Dung lượng lưu trữ | 10MB + file dictionary |

### Quyền bắt buộc:
- **Quyền Vị trí (Location)** — cần để quét WiFi trên Android 8+
- **GPS / Dịch vụ Vị trí phải được BẬT** — Android 10+ yêu cầu Location Services ở cấp hệ thống phải bật, không chỉ cấp quyền cho app. Nếu GPS tắt, `scanResults` trả về danh sách rỗng.

> 💡 **Lưu ý quan trọng:** Kéo thanh thông báo xuống → nhấn vào biểu tượng **Định vị / GPS** để bật trước khi quét WiFi.

### Tùy chọn cho tính năng nâng cao:
- **Shizuku** (để test nhanh) - [Tải xuống](https://github.com/RikkaApps/Shizuku/releases)
- **Dịch vụ Accessibility** (cho auto-click dự phòng)
- **Device Owner** (để test không dialog)

---

## 🔧 Cài Đặt

### Cách 1: Build từ mã nguồn
```bash
git clone https://github.com/yourusername/WIDT.git
cd WIDT
# Mở trong Android Studio
# Build → Build APK
```

### Cách 2: Tải APK
*(Sắp ra mắt - chỉ cho mục đích học tập)*

### Cách 3: Cài đặt Shizuku (khuyến nghị để test nhanh)
1. Tải Shizuku từ [GitHub](https://github.com/RikkaApps/Shizuku/releases)
2. Cài đặt và khởi động Shizuku (wireless ADB hoặc USB)
3. Mở WIDT → Cài đặt → Cấp quyền

---

## 🚀 Hướng Dẫn Sử Dụng

### Bước 1: Cấp quyền & Bật GPS
- Cấp quyền **Vị trí (Location)** khi app yêu cầu
- ⚠️ **Bật GPS / Dịch vụ Vị trí** trên thiết bị (kéo thanh thông báo → nhấn biểu tượng Định vị)
  - Đây là yêu cầu bắt buộc trên Android 10+ — nếu GPS tắt, quét WiFi sẽ không có kết quả
- Quyền WiFi state và change (tự động)

### Bước 2: Quét mạng
- Chuyển đến tab **Analyzer**
- Đảm bảo WiFi đang BẬT và GPS đang BẬT
- Nhấn **"Scan WiFi"**
- Xem danh sách mạng với thông tin nhà sản xuất

### Bước 3: Kiểm tra mạng
- Nhấn vào bất kỳ mạng nào trong danh sách
- Tự động chuyển đến tab **Tester**
- Chọn dictionary (mặc định: top400.txt)
- Nhấn **"Start Test"**
- Chờ kết quả

### Bước 4: (Tùy chọn) Cài đặt Shizuku để test nhanh hơn
- Cài ứng dụng Shizuku
- Khởi động Shizuku qua wireless ADB hoặc USB
- Cấp quyền khi được yêu cầu
- Tốc độ test cải thiện từ ~8s xuống ~2s mỗi mật khẩu

---

## 📁 File Dictionary

| File | Vị trí | Mô tả |
|------|--------|-------|
| `top400.txt` | `/res/raw/` | 400 mật khẩu phổ biến nhất toàn cầu |
| `vietnam.txt` | `/res/raw/` | Mật khẩu phổ biến tại Việt Nam |
| `router_defaults.txt` | `/res/raw/` | Mật khẩu mặc định theo nhà sản xuất router |
| Custom | Người dùng chọn | Bất kỳ file .txt nào với mỗi dòng là một mật khẩu |

---

## 🏗️ Kiến Trúc

```
WIDT/
├── analyzer/          # Quét và hiển thị WiFi
├── tester/            # Dictionary testing với Connection Engine
│   └── engine/        # 5 chiến lược kết nối
├── offline/           # PBKDF2 bẻ khóa offline
├── dictionary/        # Smart dictionary engine
├── settings/          # Cài đặt ứng dụng
└── common/            # Tiện ích dùng chung (Shizuku, Accessibility)
```

### Connection Engine (Tự động chọn phương án tốt nhất)

| Engine | Tốc độ | Yêu cầu | Dialog |
|--------|--------|---------|--------|
| cmd wifi | ⭐⭐⭐⭐⭐ | Shizuku + Android 12+ | ❌ |
| WifiConfiguration | ⭐⭐⭐⭐⭐ | Shizuku | ❌ |
| Device Owner | ⭐⭐⭐⭐⭐ | ADB (1 lần) | ❌ |
| Suggestion + A11y | ⭐⭐⭐ | Accessibility | ✅ (auto-click) |
| Manual | ⭐ | Không | ✅ (người dùng click) |

---

## ⚠️ Lưu Ý Pháp Lý & Đạo Đức

- **KHÔNG** sử dụng ứng dụng này để tấn công mạng không phải của bạn
- **KHÔNG** sử dụng cho các hoạt động bất hợp pháp
- **KHÔNG** cố gắng bẻ khóa mật khẩu WiFi của hàng xóm
- **CHỈ** kiểm tra mạng của chính bạn để nâng cao bảo mật
- Ứng dụng này cho **mục đích học tập** để hiểu về bảo mật WiFi

Khi sử dụng ứng dụng này, bạn đồng ý:
- Chỉ sử dụng trên mạng bạn sở hữu hoặc được phép kiểm tra
- Chịu hoàn toàn trách nhiệm về hành động của mình
- Không khiếu nại tác giả về bất kỳ hành vi lạm dụng nào

---

## 🐛 Lỗi Đã Biết & Giải Pháp

| Vấn đề | Trạng thái | Giải pháp |
|--------|-----------|-----------|
| **Không scan được WiFi (danh sách trống)** | ✅ Đã ghi nhận | **Bật GPS** — Android 10+ bắt buộc Location Services phải BẬT ở cấp hệ thống |
| WifiConfiguration deprecated trên Android 10+ | ✅ Đã fix | Dùng Shizuku hoặc cmd wifi |
| Dialog xác nhận cho mạng mới | ✅ Đã fix | Dùng Shizuku hoặc auto-click |
| Giới hạn quét WiFi (4 lần/2 phút) | ⚠️ Giới hạn | App tự dùng cached results, không ảnh hưởng kết quả |
| Offline cracking chưa hoàn chỉnh | 🚧 Đang phát triển | Dùng online tester thay thế |
| PC Bridge chưa có | 📅 Dự kiến | Sẽ ra mắt sau |

---

## 📊 Hiệu Năng

| Kích thước dictionary | Thời gian (không Shizuku) | Thời gian (có Shizuku) |
|----------------------|---------------------------|------------------------|
| 50 mật khẩu | ~4 phút | ~1.5 phút |
| 100 mật khẩu | ~8 phút | ~3 phút |
| 400 mật khẩu | ~32 phút | ~12 phút |

*Thời gian ước tính dựa trên 3-5s mỗi lần thử (không Shizuku) vs 2s mỗi lần thử (có Shizuku)*

---

## 🤝 Đóng Góp

Chào đón mọi đóng góp! Vui lòng:
1. Fork repository
2. Tạo nhánh tính năng
3. Gửi pull request

**Các lĩnh vực cần trợ giúp:**
- Triển khai offline cracking đầy đủ (PTK + MIC)
- PC Bridge engine (máy chủ Python)
- Thêm file dictionary
- Cải thiện giao diện

---

## 📄 Giấy Phép

Dự án này chỉ cho **mục đích học tập**.  
Không có giấy phép rõ ràng - liên hệ tác giả để được cấp phép.

---

## 📞 Liên Hệ

Để được hỗ trợ:
- GitHub Issues: [Tạo issue](https://github.com/BlackCandy001/WIFI-By-Pass/issues)
- Email: (sắp có)

---

## 🙏 Lời Cảm Ơn

- Shizuku bởi RikkaApps cho quyền cao hơn không cần root
- MPAndroidChart bởi PhilJay cho thư viện biểu đồ
- Các dự án tham khảo từ các công cụ WiFi mã nguồn mở

---

**Được tạo với ❤️ cho mục đích học tập**  
*Hãy nhớ: Càng mạnh mẽ càng phải có trách nhiệm. Sử dụng khôn ngoan.*