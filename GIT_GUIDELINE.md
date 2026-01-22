# QUY CHUẨN GIT FLOW & NAMING CONVENTION

Tài liệu này quy định cách thức quản lý mã nguồn, đặt tên nhánh và quy trình đưa code từ máy cá nhân lên môi trường Production.

---

## 1. Mô hình nhánh (Branching Model)

Chúng ta sử dụng mô hình **Git Flow** tối giản với 2 nhánh chính vĩnh viễn:

### 1.1. Các nhánh chính (Long-lived Branches)

- **`main` (hoặc `master`):**
  - Chứa source code đang chạy trên **Production**.
  - Trạng thái: Luôn ổn định (Stable).
  - **CẤM:** Commit trực tiếp. Chỉ được merge từ `develop` hoặc `hotfix`.
- **`develop`:**
  - Chứa source code mới nhất đang được phát triển/kiểm thử (Staging/Dev).
  - Là nơi hội tụ của tất cả các tính năng mới trước khi release.
  - **CẤM:** Commit trực tiếp. Phải merge qua Pull Request (PR).

### 1.2. Các nhánh tạm thời (Short-lived Branches)

- `feature/*`: Dùng để phát triển tính năng mới. Tách ra từ `develop`.
- `fix/*` (hoặc `bugfix/*`): Dùng để sửa lỗi khi đang dev. Tách ra từ `develop`.
- `hotfix/*`: Dùng để sửa lỗi khẩn cấp trên Production. Tách ra từ `main`.

---

## 2. Quy tắc đặt tên nhánh (Naming Convention)

Format chuẩn: `type/ticket-id-short-description`

- **Separator:** Sử dụng dấu gạch chéo `/` để phân loại folder trong các tool Git, và dấu gạch ngang `-` để nối các từ.
- **Ticket ID:** (Tùy chọn) Mã task trên Jira/Trello (VD: `JIRA-123`).

### Các loại Type quy định:

| Type         | Mục đích                             | Nhánh gốc (Source) | Merge về (Target)  | Ví dụ chuẩn                                                     |
| :----------- | :----------------------------------- | :----------------- | :----------------- | :-------------------------------------------------------------- |
| **feat**     | Phát triển tính năng mới             | `develop`          | `develop`          | `feat/payment-momo-integration`<br>`feat/JIRA-101-login-screen` |
| **fix**      | Sửa lỗi trong quá trình dev/test     | `develop`          | `develop`          | `fix/wrong-price-calculation`<br>`fix/JIRA-105-header-css`      |
| **refactor** | Cấu trúc lại code, không đổi logic   | `develop`          | `develop`          | `refactor/user-service-logic`                                   |
| **chore**    | Cấu hình, setup library, rác         | `develop`          | `develop`          | `chore/update-pom-xml`<br>`chore/setup-docker`                  |
| **hotfix**   | **Lỗi nghiêm trọng trên Production** | `main`             | `main` & `develop` | `hotfix/payment-gateway-crash`                                  |

> **❌ SAI:** `login`, `fix-bug`, `dev-hieu`, `feature_login` (Sai dấu gạch dưới).

---

## 3. Quy trình làm việc (Workflow)

### 3.1. Quy trình phát triển tính năng (Happy Path)

1.  **Đồng bộ code:**
    ```bash
    git checkout develop
    git pull origin develop
    ```
2.  **Tạo nhánh mới:**
    ```bash
    git checkout -b feat/course-management
    ```
3.  **Code & Commit:** (Xem quy tắc commit bên dưới).
4.  **Push lên Server:**
    ```bash
    git push origin feat/course-management
    ```
5.  **Tạo Pull Request (PR):**
    - Target branch: `develop`
    - Reviewer: Ít nhất 1 leader hoặc senior dev.
    - Điều kiện merge: Pass CI/CD (nếu có) và được Approve.

### 3.2. Quy trình Release (Đưa lên Production)

Khi `develop` đã ổn định và muốn release phiên bản mới:

1.  Leader tạo PR từ `develop` -> `main`.
2.  Sau khi merge, đánh tag version (VD: `v1.0.0`).

### 3.3. Quy trình Hotfix (Sửa lỗi khẩn cấp)

1.  Từ `main`, tạo nhánh `hotfix/tên-lỗi`.
2.  Fix lỗi và commit.
3.  Merge nhánh hotfix vào **CẢ HAI** nhánh: `main` (để sửa prod) và `develop` (để đồng bộ code fix cho lần release sau).

---

## 4. Quy tắc Commit Message (Conventional Commits)

Format: `[Type] Short description` hoặc `Type: Short description`

**Các Type hợp lệ:**

- `[FEAT]` hoặc `feat`: Tính năng mới.
- `[FIX]` hoặc `fix`: Sửa lỗi.
- `[DOCS]` hoặc `docs`: Thêm/sửa tài liệu.
- `[STYLE]`: Sửa format (space, semi-colon), không ảnh hưởng code.
- `[REFACTOR]`: Sửa code nhưng không sửa logic.
- `[TEST]`: Thêm test case.

**Ví dụ:**

- ✅ `[FEAT] Add caching for product list`
- ✅ `fix: handle null pointer in User service`
- ❌ `update code` (Quá chung chung)
- ❌ `fix bug` (Bug gì?)

---

## 5. Các điều CẤM KỴ

1.  **Cấm Force Push (`git push -f`)** lên `develop` và `main`.
2.  **Cấm Commit file cấu hình cá nhân** (`.idea`, `.vscode`, `target/`, `node_modules/`). Phải dùng `.gitignore`.
3.  **Cấm Rebase nhánh public** (`develop`). Chỉ dùng Merge.
4.  **Cấm Merge code lỗi.** Hãy tự test local trước khi tạo PR.
