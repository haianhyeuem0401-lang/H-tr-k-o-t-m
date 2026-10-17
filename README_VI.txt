QBAO TWEAK ADR - SOURCE JAVA CHO AIDE/ANDROID STUDIO

Cấu trúc:
- settings.gradle, build.gradle
- app/build.gradle
- app/src/main/AndroidManifest.xml
- app/src/main/java/com/qbao/tweakadr/MainActivity.java
- logo-QBao-Sadz.jpeg (logo tham khảo bạn gửi)

Mở thư mục project bằng Android Studio hoặc tạo project tương ứng trong AIDE.
Ứng dụng Java thuần, không cần thư viện ngoài. Android Gradle Plugin 8.2.2 yêu cầu môi trường Gradle/JDK tương thích; AIDE cũ có thể cần tạo project mới trong AIDE rồi chép MainActivity.java và Manifest vào.

QUAN TRỌNG - CHECK KEY:
Website https://qbaonew1.onrender.com/ đang hiển thị giao diện nhập key, nhưng source này chưa xác minh được endpoint API mà trang dùng. Trong MainActivity.java, KEY_CHECK_API đang là placeholder:
https://qbaonew1.onrender.com/api/check-key
Backend phải nhận POST JSON {"key":"..."} và trả JSON {"valid":true} hoặc {"valid":false}. Nếu endpoint thực tế khác, sửa hằng số và parse JSON đúng theo phản hồi API. Không bật truy cập mặc định khi API lỗi.

Tính năng:
- 3 tab Aim / Máy / Admin.
- 10 công tắc, lưu trạng thái và phát âm báo.
- Lưu kích thước/màu tâm xem trước.
- Hiển thị số CPU core, RAM tối đa của app, refresh rate màn hình, Android version.
- Gợi ý tối ưu an toàn; không thay đổi hệ thống tự động.
- Mở TikTok admin qua link đã cung cấp.

GIỚI HẠN:
- Các nút chức năng là preset/ghi nhớ trạng thái trong ứng dụng, không điều khiển Free Fire.
- Tâm ngắm trong bản mẫu chỉ xem trước trong app; lớp phủ nổi qua ứng dụng khác cần phát triển service riêng và quyền overlay.
- Không có APK đã biên dịch trong gói này; đây là source project.
