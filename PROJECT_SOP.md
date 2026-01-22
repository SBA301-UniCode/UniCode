# QUY CHUẨN KỸ THUẬT & LUỒNG PHÁT TRIỂN (SOP)

Tài liệu này quy định các tiêu chuẩn kỹ thuật bắt buộc cho dự án, bao gồm thiết kế API, quy tắc đặt tên, xử lý lỗi và luồng dữ liệu.

---

## 1. Quy chuẩn thiết kế API (RESTful Standards)

Tuyệt đối tuân thủ chuẩn RESTful. API là giao diện giao tiếp, cần thống nhất để Frontend không phải "đoán".

### Quy tắc đặt URL (Endpoint Naming)

1.  **Chỉ dùng Danh từ (Nouns), KHÔNG dùng Động từ (Verbs).**
    - ✅ `POST /api/v1/courses` (Tạo khóa học)
    - ❌ `POST /api/v1/create-course`

2.  **Dùng số nhiều (Plural) cho tên tài nguyên chính.**
    - ✅ `/users`, `/orders`, `/products`
    - ❌ `/user`, `/order`

3.  **Quan hệ cha-con (Hierarchy):**
    - Lấy tài nguyên con của một tài nguyên cha: `/resources/{id}/sub-resources`
    - ✅ `GET /courses/10/lessons` (Lấy danh sách bài học của khóa học ID 10)

4.  **Kebab-case cho URL:**
    - ✅ `/api/v1/payment-methods`
    - ❌ `/api/v1/paymentMethods` hay `/payment_methods`

### Quy tắc sử dụng HTTP Method

- **GET**: Chỉ lấy dữ liệu. Tuyệt đối không thay đổi data.
- **POST**: Tạo mới (Create).
- **PUT**: Cập nhật toàn bộ (Replace) hoặc cập nhật lớn.
- **PATCH**: Cập nhật một phần (Partial Update - ví dụ chỉ đổi trạng thái).
- **DELETE**: Xóa (Soft delete hoặc Hard delete tùy logic).

---

## 2. Quy tắc phân định: Constants vs Enums

Đây là phần các dev hay nhầm lẫn nhất. Sử dụng Decision Matrix dưới đây:

| Tiêu chí     | Dùng ENUM                                                                     | Dùng CONSTANT                                                                  |
| :----------- | :---------------------------------------------------------------------------- | :----------------------------------------------------------------------------- |
| **Bản chất** | Một tập hợp các giá trị hữu hạn, có tính logic và ngữ nghĩa.                  | Giá trị tĩnh, cấu hình, giới hạn vật lý, hoặc chuỗi magic string.              |
| **Thay đổi** | Ít khi thay đổi, nếu đổi thường ảnh hưởng logic code (switch-case).           | Có thể thay đổi giá trị mà không làm hỏng logic luồng (vd: tăng limit upload). |
| **Ví dụ**    | Trạng thái (Active/Inactive), Role (Admin/User), Loại thanh toán (Momo/Visa). | `MAX_UPLOAD_SIZE`, `DEFAULT_PAGE_SIZE`, `API_TIMEOUT`, `DATE_FORMAT`.          |
| **Phạm vi**  | **BẮT BUỘC MIRROR** (Phải có file định nghĩa ở cả BE và FE).                  | Thường định nghĩa riêng biệt tùy ngữ cảnh BE hay FE.                           |

### Quy định đặt tên

- **Enums:** `PascalCase` cho tên Class, `UPPER_CASE` cho giá trị.
  - _Vd:_ `UserStatus.ACTIVE`, `UserStatus.BANNED`.
- **Constants:** `UPPER_SNAKE_CASE` hoàn toàn.
  - _Vd:_ `MAX_RETRY_COUNT = 3`.

---

## 3. Quy chuẩn xử lý lỗi & Error Codes

Không trả về chuỗi lỗi tự do (Hardcoded Strings). Mọi lỗi business phải được quy hoạch thành Mã lỗi (Error Code).

### Cấu trúc Mã lỗi (Format)

Format: `MODULE_ENTITY_ERROR_TYPE`

- `USR_001`: User not found.
- `AUTH_002`: Invalid password.
- `CRS_003`: Course is not published.

### Quy trình khai báo lỗi (Workflow)

1.  **Bước 1 (Backend):** Khi dev backend phát hiện một case lỗi nghiệp vụ mới -> Vào file `ErrorCodeEnum` (hoặc file properties) khai báo mã mới và message mặc định.
2.  **Bước 2 (API Response):** Ném ra exception chứa mã lỗi đó.
    - _Vd:_ `throw new BusinessException(ErrorCode.COURSE_NOT_ENROLLED);`
3.  **Bước 3 (Frontend):** FE nhận mã lỗi (ví dụ `CRS_003`) từ API -> Tra cứu trong file map ngôn ngữ (i18n) để hiển thị thông báo thân thiện cho user ("Bạn chưa đăng ký khóa học này").

> **Lưu ý:** Tuyệt đối **KHÔNG** hiển thị message gốc từ Exception (ví dụ: `NullPointerException`) ra cho user cuối.

---

## 4. Cấu trúc Response (Unified Wrapper)

Mọi API (trừ file download) đều phải trả về đúng 1 cấu trúc JSON duy nhất.

### Java Implementation

```java
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Quan trọng: Bỏ qua trường null khi convert sang JSON
public class ApiResponse<T> {

    private boolean success;          // True nếu xử lý thành công
    private String message;            // Thông báo debug hoặc success message
    private T data;                   // Dữ liệu chính (UserDTO, CourseDTO...)
    private String errorCode;          // Mã lỗi nghiệp vụ (VD: USR_001), null nếu success

    // Gán giá trị mặc định là thời gian hiện tại của server khi tạo object
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Helper method cho response thành công
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .message("Success")
            .data(data)
            .build();
    }

    /**
     * Helper method cho response lỗi
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
            .success(false)
            .errorCode(code)
            .message(message)
            .build();
    }
}
```
