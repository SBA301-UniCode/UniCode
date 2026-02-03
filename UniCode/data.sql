-- Kích hoạt extension để tạo UUID nếu chưa có (thường cần thiết cho gen_random_uuid)
CREATE
    EXTENSION IF NOT EXISTS "pgcrypto";

-- 1. Bảng PRIVILEGE (Quyền hạn)
INSERT INTO privilege (privilege_code, privilege_name, description, created_by, created_at, deleted)
VALUES ('P_VIEW_COURSE', 'Xem khóa học', 'Quyền xem nội dung khóa học', 'system', NOW(), false),
       ('P_CREATE_COURSE', 'Tạo khóa học', 'Quyền tạo mới khóa học', 'system', NOW(), false),
       ('P_EDIT_COURSE', 'Sửa khóa học', 'Quyền chỉnh sửa khóa học', 'system', NOW(), false),
       ('P_DELETE_COURSE', 'Xóa khóa học', 'Quyền xóa khóa học', 'system', NOW(), false),
       ('P_MANAGE_USER', 'Quản lý người dùng', 'Quyền quản lý học viên và giảng viên', 'system', NOW(), false),
       ('P_VIEW_ANALYTICS', 'Xem thống kê', 'Xem báo cáo doanh thu', 'system', NOW(), false),
       ('P_GRADE_EXAM', 'Chấm thi', 'Quyền chấm điểm bài thi tự luận', 'system', NOW(), false),
       ('P_UPLOAD_VIDEO', 'Tải video', 'Quyền tải lên bài giảng video', 'system', NOW(), false),
       ('P_COMMENT', 'Bình luận', 'Quyền bình luận trong bài học', 'system', NOW(), false),
       ('P_SYSTEM_CONFIG', 'Cấu hình hệ thống', 'Quyền admin cao nhất', 'system', NOW(), false);

-- 2. Bảng ROLES (Vai trò)
INSERT INTO roles (role_code, role_name, description, created_by, created_at, deleted)
VALUES ('ADMIN', 'Quản trị viên', 'Quản trị toàn bộ hệ thống', 'system', NOW(), false),
       ('INSTRUCTOR', 'Giảng viên', 'Người tạo và quản lý khóa học', 'system', NOW(), false),
       ('LEARNER', 'Học viên', 'Người tham gia học tập', 'system', NOW(), false),
       ('SUPPORT', 'Hỗ trợ viên', 'Hỗ trợ khách hàng', 'system', NOW(), false),
       ('CONTENT_CREATOR', 'Người tạo nội dung', 'Chỉ tạo nội dung, không quản lý', 'system', NOW(), false),
       ('EDITOR', 'Biên tập viên', 'Chỉnh sửa nội dung', 'system', NOW(), false),
       ('MODERATOR', 'Kiểm duyệt viên', 'Kiểm duyệt bình luận', 'system', NOW(), false),
       ('TESTER', 'Người kiểm thử', 'Tài khoản dùng để test', 'system', NOW(), false),
       ('GUEST', 'Khách', 'Người dùng chưa đăng nhập', 'system', NOW(), false),
       ('PARTNER', 'Đối tác', 'Đối tác liên kết', 'system', NOW(), false);

-- 3. Bảng ROLE_PRIVILEGE (Phân quyền)
INSERT INTO role_privilege (role_code, privilege_code)
VALUES ('ADMIN', 'P_SYSTEM_CONFIG'),
       ('ADMIN', 'P_MANAGE_USER'),
       ('INSTRUCTOR', 'P_CREATE_COURSE'),
       ('INSTRUCTOR', 'P_EDIT_COURSE'),
       ('LEARNER', 'P_VIEW_COURSE'),
       ('LEARNER', 'P_COMMENT'),
       ('SUPPORT', 'P_MANAGE_USER'),
       ('CONTENT_CREATOR', 'P_UPLOAD_VIDEO'),
       ('MODERATOR', 'P_DELETE_COURSE'),
       ('PARTNER', 'P_VIEW_ANALYTICS');

-- 4. Bảng USERS (Người dùng) - Tạo ID cố định để dùng lại
INSERT INTO users (user_id, name, email, password, is_active, token_version, created_at, deleted)
VALUES ('00000000-0000-0000-0000-000000000001', 'Nguyễn Văn Admin', 'admin@coursera.vn', '$2a$10$QGAY3pmmE7qZzbthlxY7YuneYp4VAN2iE.OLJoJee9ktXYrvxlmB.', true, 1, NOW(),
        false),
       ('00000000-0000-0000-0000-000000000002', 'Trần Thầy Giáo', 'teacher1@coursera.vn', '$2a$10$QGAY3pmmE7qZzbthlxY7YuneYp4VAN2iE.OLJoJee9ktXYrvxlmB.', true, 1, NOW(),
        false),
       ('00000000-0000-0000-0000-000000000003', 'Lê Cô Giáo', 'teacher2@coursera.vn', '$2a$10$QGAY3pmmE7qZzbthlxY7YuneYp4VAN2iE.OLJoJee9ktXYrvxlmB.', true, 1, NOW(),
        false),
       ('00000000-0000-0000-0000-000000000004', 'Phạm Học Viên A', 'student1@coursera.vn', '$2a$10$QGAY3pmmE7qZzbthlxY7YuneYp4VAN2iE.OLJoJee9ktXYrvxlmB.', true, 1,
        NOW(), false),
       ('00000000-0000-0000-0000-000000000005', 'Hoàng Học Viên B', 'student2@coursera.vn', '$2a$10$QGAY3pmmE7qZzbthlxY7YuneYp4VAN2iE.OLJoJee9ktXYrvxlmB.', true, 1,
        NOW(), false),
       ('00000000-0000-0000-0000-000000000006', 'Vũ Học Viên C', 'student3@coursera.vn', '$2a$10$QGAY3pmmE7qZzbthlxY7YuneYp4VAN2iE.OLJoJee9ktXYrvxlmB.', true, 1, NOW(),
        false),
       ('00000000-0000-0000-0000-000000000007', 'Đặng Học Viên D', 'student4@coursera.vn', '$2a$10$QGAY3pmmE7qZzbthlxY7YuneYp4VAN2iE.OLJoJee9ktXYrvxlmB.', true, 1,
        NOW(), false),
       ('00000000-0000-0000-0000-000000000008', 'Bùi Học Viên E', 'student5@coursera.vn', '$2a$10$QGAY3pmmE7qZzbthlxY7YuneYp4VAN2iE.OLJoJee9ktXYrvxlmB.', true, 1, NOW(),
        false),
       ('00000000-0000-0000-0000-000000000009', 'Đỗ Hỗ Trợ', 'support@coursera.vn', '$2a$10$QGAY3pmmE7qZzbthlxY7YuneYp4VAN2iE.OLJoJee9ktXYrvxlmB.', true, 1, NOW(),
        false),
       ('00000000-0000-0000-0000-000000000010', 'Ngô Kiểm Duyệt', 'mod@coursera.vn', '$2a$10$QGAY3pmmE7qZzbthlxY7YuneYp4VAN2iE.OLJoJee9ktXYrvxlmB.', true, 1, NOW(),
        false);

-- 5. Bảng USER_ROLE
INSERT INTO user_role (user_id, role_code)
VALUES ('00000000-0000-0000-0000-000000000001', 'ADMIN'),
       ('00000000-0000-0000-0000-000000000002', 'INSTRUCTOR'),
       ('00000000-0000-0000-0000-000000000003', 'INSTRUCTOR'),
       ('00000000-0000-0000-0000-000000000004', 'LEARNER'),
       ('00000000-0000-0000-0000-000000000005', 'LEARNER'),
       ('00000000-0000-0000-0000-000000000006', 'LEARNER'),
       ('00000000-0000-0000-0000-000000000007', 'LEARNER'),
       ('00000000-0000-0000-0000-000000000008', 'LEARNER'),
       ('00000000-0000-0000-0000-000000000009', 'SUPPORT'),
       ('00000000-0000-0000-0000-000000000010', 'MODERATOR');

-- 6. Bảng SYLABUS (Giáo trình)
INSERT INTO sylabus (sylabus_id, course_content, method, reference_material, created_at, deleted)
VALUES ('SYL001', 'Lập trình Java cơ bản đến nâng cao', 'Online Video', 'Sách Java Core', NOW(), false),
       ('SYL002', 'Python cho phân tích dữ liệu', 'Thực hành Lab', 'Kaggle Datasets', NOW(), false),
       ('SYL003', 'Thiết kế Web với ReactJS', 'Project based', 'React Docs', NOW(), false),
       ('SYL004', 'Tiếng Anh giao tiếp công sở', 'Video + Audio', 'Oxford Business', NOW(), false),
       ('SYL005', 'Quản trị dự án Agile', 'Lý thuyết + Case study', 'Jira Guide', NOW(), false),
       ('SYL006', 'Marketing căn bản', 'Slide bài giảng', 'Philip Kotler', NOW(), false),
       ('SYL007', 'Excel cho kế toán', 'Thực hành Excel', 'Microsoft Help', NOW(), false),
       ('SYL008', 'SQL Server toàn tập', 'Truy vấn Database', 'SQLBolt', NOW(), false),
       ('SYL009', 'Đồ họa Photoshop', 'Video hướng dẫn', 'Adobe Support', NOW(), false),
       ('SYL010', 'Kỹ năng mềm: Quản lý thời gian', 'Trắc nghiệm', 'Pomodoro', NOW(), false);

INSERT INTO coursera (course_id, title, description, price, instructor_id, sylabus_id, created_at, deleted)
VALUES
    ('11111111-0000-0000-0000-000000000001', 'Java Masterclass', 'Khóa học Java đầy đủ nhất', 1500000, '00000000-0000-0000-0000-000000000002', 'SYL001', NOW(), false),
    ('11111111-0000-0000-0000-000000000002', 'Python Data Science', 'Học Python để làm Data', 2000000, '00000000-0000-0000-0000-000000000002', 'SYL002', NOW(), false),
    ('11111111-0000-0000-0000-000000000003', 'ReactJS Frontend', 'Xây dựng web hiện đại', 1200000, '00000000-0000-0000-0000-000000000003', 'SYL003', NOW(), false),
    ('11111111-0000-0000-0000-000000000004', 'Business English', 'Tiếng Anh đi làm', 500000, '00000000-0000-0000-0000-000000000003', 'SYL004', NOW(), false),
    ('11111111-0000-0000-0000-000000000005', 'Agile Project Management', 'Quản lý dự án chuẩn quốc tế', 3000000, '00000000-0000-0000-0000-000000000002', 'SYL005', NOW(), false),
    ('11111111-0000-0000-0000-000000000006', 'Digital Marketing', 'Marketing thời đại số', 800000, '00000000-0000-0000-0000-000000000003', 'SYL006', NOW(), false),
    ('11111111-0000-0000-0000-000000000007', 'Excel Advanced', 'Excel nâng cao cho kế toán', 400000, '00000000-0000-0000-0000-000000000002', 'SYL007', NOW(), false),
    ('11111111-0000-0000-0000-000000000008', 'SQL Database', 'Truy vấn dữ liệu chuyên sâu', 1000000, '00000000-0000-0000-0000-000000000002', 'SYL008', NOW(), false),
    ('11111111-0000-0000-0000-000000000009', 'Photoshop Mastery', 'Thành thạo Photoshop trong 30 ngày', 600000, '00000000-0000-0000-0000-000000000003', 'SYL009', NOW(), false),
    ('11111111-0000-0000-0000-000000000010', 'Soft Skills', 'Kỹ năng quản lý thời gian', 300000, '00000000-0000-0000-0000-000000000003', 'SYL010', NOW(), false)
ON CONFLICT (course_id) DO NOTHING;
-- 8. Bảng CHAPTER (Chương học)
INSERT INTO chapter (chapter_id, course_id, title, order_index, created_at, deleted)
VALUES ('22222222-0000-0000-0000-000000000001', '11111111-0000-0000-0000-000000000001', 'Chương 1: Giới thiệu Java', 1,
        NOW(), false),
       ('22222222-0000-0000-0000-000000000002', '11111111-0000-0000-0000-000000000001',
        'Chương 2: Hướng đối tượng (OOP)', 2, NOW(), false),
       ('22222222-0000-0000-0000-000000000003', '11111111-0000-0000-0000-000000000001', 'Chương 3: Java Collections', 3,
        NOW(), false),
       ('22222222-0000-0000-0000-000000000004', '11111111-0000-0000-0000-000000000002', 'Chương 1: Cài đặt Python', 1,
        NOW(), false),
       ('22222222-0000-0000-0000-000000000005', '11111111-0000-0000-0000-000000000002', 'Chương 2: Pandas & Numpy', 2,
        NOW(), false),
       ('22222222-0000-0000-0000-000000000006', '11111111-0000-0000-0000-000000000003', 'Chương 1: React Components', 1,
        NOW(), false),
       ('22222222-0000-0000-0000-000000000007', '11111111-0000-0000-0000-000000000003', 'Chương 2: React Hooks', 2,
        NOW(), false),
       ('22222222-0000-0000-0000-000000000008', '11111111-0000-0000-0000-000000000004', 'Chương 1: Viết Email', 1,
        NOW(), false),
       ('22222222-0000-0000-0000-000000000009', '11111111-0000-0000-0000-000000000005', 'Chương 1: Scrum Framework', 1,
        NOW(), false),
       ('22222222-0000-0000-0000-000000000010', '11111111-0000-0000-0000-000000000008', 'Chương 1: SELECT Statement', 1,
        NOW(), false);

-- 9. Bảng LESSON (Bài học)
INSERT INTO lesson (lesson_id, chapter_id, title, order_index, created_at, deleted)
VALUES ('33333333-0000-0000-0000-000000000001', '22222222-0000-0000-0000-000000000001', 'Cài đặt JDK', 1, NOW(), false),
       ('33333333-0000-0000-0000-000000000002', '22222222-0000-0000-0000-000000000001', 'Hello World', 2, NOW(), false),
       ('33333333-0000-0000-0000-000000000003', '22222222-0000-0000-0000-000000000002', 'Class và Object', 1, NOW(),
        false),
       ('33333333-0000-0000-0000-000000000004', '22222222-0000-0000-0000-000000000002', 'Kế thừa và Đa hình', 2, NOW(),
        false),
       ('33333333-0000-0000-0000-000000000005', '22222222-0000-0000-0000-000000000004', 'Cài đặt Anaconda', 1, NOW(),
        false),
       ('33333333-0000-0000-0000-000000000006', '22222222-0000-0000-0000-000000000005', 'DataFrame cơ bản', 1, NOW(),
        false),
       ('33333333-0000-0000-0000-000000000007', '22222222-0000-0000-0000-000000000006', 'JSX là gì', 1, NOW(), false),
       ('33333333-0000-0000-0000-000000000008', '22222222-0000-0000-0000-000000000008', 'Cấu trúc Email trang trọng', 1,
        NOW(), false),
       ('33333333-0000-0000-0000-000000000009', '22222222-0000-0000-0000-000000000009', 'Sprint Planning', 1, NOW(),
        false),
       ('33333333-0000-0000-0000-000000000010', '22222222-0000-0000-0000-000000000010', 'Mệnh đề WHERE', 1, NOW(),
        false);

-- 10. Bảng CONTENT (Nội dung - Chứa type cho Video/Doc/Exam)
-- 0: Video, 1: Document, 2: Exam
INSERT INTO content (content_id, lesson_id, content_type, created_at, deleted)
VALUES ('44444444-0000-0000-0000-000000000001', '33333333-0000-0000-0000-000000000001', 0, NOW(), false), -- Video
       ('44444444-0000-0000-0000-000000000002', '33333333-0000-0000-0000-000000000002', 0, NOW(), false), -- Video
       ('44444444-0000-0000-0000-000000000003', '33333333-0000-0000-0000-000000000003', 1, NOW(), false), -- Doc
       ('44444444-0000-0000-0000-000000000004', '33333333-0000-0000-0000-000000000004', 2, NOW(), false), -- Exam (Quiz)
       ('44444444-0000-0000-0000-000000000005', '33333333-0000-0000-0000-000000000005', 0, NOW(), false), -- Video
       ('44444444-0000-0000-0000-000000000006', '33333333-0000-0000-0000-000000000006', 1, NOW(), false), -- Doc
       ('44444444-0000-0000-0000-000000000007', '33333333-0000-0000-0000-000000000007', 0, NOW(), false), -- Video
       ('44444444-0000-0000-0000-000000000008', '33333333-0000-0000-0000-000000000008', 1, NOW(), false), -- Doc
       ('44444444-0000-0000-0000-000000000009', '33333333-0000-0000-0000-000000000009', 0, NOW(), false), -- Video
       ('44444444-0000-0000-0000-000000000010', '33333333-0000-0000-0000-000000000010', 2, NOW(), false);
-- Exam

-- 11. Bảng VIDEO (Chi tiết Video) - Link với ContentType 0
INSERT INTO video (video_id, content_id, video_url, duration, created_at, deleted)
VALUES (gen_random_uuid(), '44444444-0000-0000-0000-000000000001', 'https://s3.aws/java-install.mp4', 600, NOW(),
        false),
       (gen_random_uuid(), '44444444-0000-0000-0000-000000000002', 'https://s3.aws/hello-world.mp4', 300, NOW(), false),
       (gen_random_uuid(), '44444444-0000-0000-0000-000000000005', 'https://s3.aws/anaconda.mp4', 450, NOW(), false),
       (gen_random_uuid(), '44444444-0000-0000-0000-000000000007', 'https://s3.aws/jsx-intro.mp4', 900, NOW(), false),
       (gen_random_uuid(), '44444444-0000-0000-0000-000000000009', 'https://s3.aws/scrum.mp4', 1200, NOW(), false);

-- 12. Bảng DOCUMENT (Chi tiết Tài liệu) - Link với ContentType 1
INSERT INTO document (document_id, content_id, title, document_url, created_at, deleted)
VALUES (gen_random_uuid(), '44444444-0000-0000-0000-000000000003', 'Slide bài giảng OOP', 'https://doc.com/oop.pdf',
        NOW(), false),
       (gen_random_uuid(), '44444444-0000-0000-0000-000000000006', 'Pandas CheatSheet', 'https://doc.com/pandas.pdf',
        NOW(), false),
       (gen_random_uuid(), '44444444-0000-0000-0000-000000000008', 'Mẫu Email Business', 'https://doc.com/email.docx',
        NOW(), false);

-- 13. Bảng EXAM (Bài kiểm tra) - Link với ContentType 2
INSERT INTO exam (exam_id, content_id, duration, pass_score, created_at, deleted)
VALUES ('55555555-0000-0000-0000-000000000001', '44444444-0000-0000-0000-000000000004', 15, 8.0, NOW(), false),
       ('55555555-0000-0000-0000-000000000002', '44444444-0000-0000-0000-000000000010', 30, 7.0, NOW(), false);

-- 14. Bảng QUESTION_BANK (Ngân hàng câu hỏi)
INSERT INTO question_bank (question_bank_id, lesson_id, question_text, question_type, number_answers, created_at,
                           deleted)
VALUES ('66666666-0000-0000-0000-000000000001', '33333333-0000-0000-0000-000000000004',
        'Tính chất nào KHÔNG thuộc OOP?', 'MULTIPLE_CHOICE', 4, NOW(), false),
       ('66666666-0000-0000-0000-000000000002', '33333333-0000-0000-0000-000000000004', 'Java hỗ trợ đa kế thừa?',
        'TRUE_FALSE', 2, NOW(), false),
       ('66666666-0000-0000-0000-000000000003', '33333333-0000-0000-0000-000000000010', 'SQL là viết tắt của gì?',
        'SHORT_ANSWER', 1, NOW(), false),
       ('66666666-0000-0000-0000-000000000004', '33333333-0000-0000-0000-000000000010', 'Câu lệnh lấy dữ liệu?',
        'MULTIPLE_CHOICE', 4, NOW(), false),
       ('66666666-0000-0000-0000-000000000005', '33333333-0000-0000-0000-000000000001', 'JDK gồm JRE và công cụ dev?',
        'TRUE_FALSE', 2, NOW(), false),
       ('66666666-0000-0000-0000-000000000006', '33333333-0000-0000-0000-000000000002', 'Hàm main trả về gì?',
        'MULTIPLE_CHOICE', 4, NOW(), false),
       ('66666666-0000-0000-0000-000000000007', '33333333-0000-0000-0000-000000000005', 'Anaconda dùng để làm gì?',
        'SHORT_ANSWER', 1, NOW(), false),
       ('66666666-0000-0000-0000-000000000008', '33333333-0000-0000-0000-000000000006',
        'Pandas dùng để xử lý dữ liệu bảng?', 'TRUE_FALSE', 2, NOW(), false),
       ('66666666-0000-0000-0000-000000000009', '33333333-0000-0000-0000-000000000007',
        'React là Framework hay Library?', 'MULTIPLE_CHOICE', 2, NOW(), false),
       ('66666666-0000-0000-0000-000000000010', '33333333-0000-0000-0000-000000000008',
        'Tiêu đề email nên viết hoa toàn bộ?', 'TRUE_FALSE', 2, NOW(), false);

-- 15. Bảng QUESTION_OPTION (Đáp án cho câu hỏi)
INSERT INTO question_option (option_id, question_bank_id, answer_text, is_correct, created_at, deleted)
VALUES (gen_random_uuid(), '66666666-0000-0000-0000-000000000001', 'Đóng gói', false, NOW(), false),
       (gen_random_uuid(), '66666666-0000-0000-0000-000000000001', 'Kế thừa', false, NOW(), false),
       (gen_random_uuid(), '66666666-0000-0000-0000-000000000001', 'Đa hình', false, NOW(), false),
       (gen_random_uuid(), '66666666-0000-0000-0000-000000000001', 'Biến đổi', true, NOW(), false),
       (gen_random_uuid(), '66666666-0000-0000-0000-000000000002', 'True', false, NOW(), false),
       (gen_random_uuid(), '66666666-0000-0000-0000-000000000002', 'False', true, NOW(), false),
       (gen_random_uuid(), '66666666-0000-0000-0000-000000000004', 'SELECT', true, NOW(), false),
       (gen_random_uuid(), '66666666-0000-0000-0000-000000000004', 'INSERT', false, NOW(), false),
       (gen_random_uuid(), '66666666-0000-0000-0000-000000000006', 'void', true, NOW(), false),
       (gen_random_uuid(), '66666666-0000-0000-0000-000000000006', 'int', false, NOW(), false);

-- 16. Bảng QUESTION_EXAM (Liên kết câu hỏi vào bài thi)
INSERT INTO question_exam (question_id, exam_id, question_bank_id, created_at, deleted)
VALUES (gen_random_uuid(), '55555555-0000-0000-0000-000000000001', '66666666-0000-0000-0000-000000000002', NOW(),
        false),
       (gen_random_uuid(), '55555555-0000-0000-0000-000000000001', '66666666-0000-0000-0000-000000000005', NOW(),
        false),
       (gen_random_uuid(), '55555555-0000-0000-0000-000000000001', '66666666-0000-0000-0000-000000000006', NOW(),
        false),
       (gen_random_uuid(), '55555555-0000-0000-0000-000000000002', '66666666-0000-0000-0000-000000000003', NOW(),
        false),
       (gen_random_uuid(), '55555555-0000-0000-0000-000000000002', '66666666-0000-0000-0000-000000000004', NOW(),
        false),
       (gen_random_uuid(), '55555555-0000-0000-0000-000000000002', '66666666-0000-0000-0000-000000000007', NOW(),
        false),
       (gen_random_uuid(), '55555555-0000-0000-0000-000000000002', '66666666-0000-0000-0000-000000000008', NOW(),
        false),
       (gen_random_uuid(), '55555555-0000-0000-0000-000000000002', '66666666-0000-0000-0000-000000000009', NOW(),
        false),
       (gen_random_uuid(), '55555555-0000-0000-0000-000000000002', '66666666-0000-0000-0000-000000000010', NOW(),
        false);
-- 20. Bảng CERTIFICATE (Chứng chỉ)
INSERT INTO certificate (certificate_id, course_id, learner_id, certificate_date, created_at, deleted)
VALUES (gen_random_uuid(), '11111111-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000008', NOW(), NOW(),
        false),
       (gen_random_uuid(), '11111111-0000-0000-0000-000000000007', '00000000-0000-0000-0000-000000000007', NOW(), NOW(),
        false),
       (gen_random_uuid(), '11111111-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000008', NOW(), NOW(),
        false),
       (gen_random_uuid(), '11111111-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000005', NOW(), NOW(),
        false),
       (gen_random_uuid(), '11111111-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000004', NOW(), NOW(),
        false),
       (gen_random_uuid(), '11111111-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000006', NOW(), NOW(),
        false),
       (gen_random_uuid(), '11111111-0000-0000-0000-000000000006', '00000000-0000-0000-0000-000000000004', NOW(), NOW(),
        false),
       (gen_random_uuid(), '11111111-0000-0000-0000-000000000008', '00000000-0000-0000-0000-000000000004', NOW(), NOW(),
        false),
       (gen_random_uuid(), '11111111-0000-0000-0000-000000000009', '00000000-0000-0000-0000-000000000007', NOW(), NOW(),
        false);

-- 23. Bảng RATE_CONDITION (Điều kiện đánh giá)
INSERT INTO rate_condition (rate_condition_id, course_id, rate, description, created_at, deleted)
VALUES ('99999999-0000-0000-0000-000000000001', '11111111-0000-0000-0000-000000000001', 50, 'Hoàn thành 50% nội dung',
        NOW(), false),
       ('99999999-0000-0000-0000-000000000002', '11111111-0000-0000-0000-000000000001', 80, 'Điểm thi trung bình > 8.0',
        NOW(), false),
       ('99999999-0000-0000-0000-000000000003', '11111111-0000-0000-0000-000000000002', 100, 'Xem hết video', NOW(),
        false),
       ('99999999-0000-0000-0000-000000000004', '11111111-0000-0000-0000-000000000002', 70, 'Nộp bài tập cuối khóa',
        NOW(), false),
       ('99999999-0000-0000-0000-000000000005', '11111111-0000-0000-0000-000000000003', 60, 'Tham gia thảo luận', NOW(),
        false),
       ('99999999-0000-0000-0000-000000000006', '11111111-0000-0000-0000-000000000004', 90, 'Hoàn thành 90% bài học',
        NOW(), false),
       ('99999999-0000-0000-0000-000000000007', '11111111-0000-0000-0000-000000000005', 100, 'Pass kỳ thi chứng chỉ',
        NOW(), false),
       ('99999999-0000-0000-0000-000000000008', '11111111-0000-0000-0000-000000000008', 50, 'Thực hành lab đầy đủ',
        NOW(), false),
       ('99999999-0000-0000-0000-000000000009', '11111111-0000-0000-0000-000000000009', 80, 'Nộp sản phẩm thiết kế',
        NOW(), false),
       ('99999999-0000-0000-0000-000000000010', '11111111-0000-0000-0000-000000000010', 100, 'Hoàn thành khóa học',
        NOW(), false);

-- 25. Bảng QUESTION (Có thể là bảng cũ, nhưng vẫn thêm cho đầy đủ theo script)
INSERT INTO question_exam (question_id, exam_id, question_bank_id)
VALUES (gen_random_uuid(), '55555555-0000-0000-0000-000000000001', '66666666-0000-0000-0000-000000000001'),
       (gen_random_uuid(), '55555555-0000-0000-0000-000000000001', '66666666-0000-0000-0000-000000000002'),
       (gen_random_uuid(), '55555555-0000-0000-0000-000000000002', '66666666-0000-0000-0000-000000000003'),
       (gen_random_uuid(), '55555555-0000-0000-0000-000000000002', '66666666-0000-0000-0000-000000000004'),
       (gen_random_uuid(), '55555555-0000-0000-0000-000000000001', '66666666-0000-0000-0000-000000000005'),
       (gen_random_uuid(), '55555555-0000-0000-0000-000000000001', '66666666-0000-0000-0000-000000000006'),
       (gen_random_uuid(), '55555555-0000-0000-0000-000000000002', '66666666-0000-0000-0000-000000000007'),
       (gen_random_uuid(), '55555555-0000-0000-0000-000000000002', '66666666-0000-0000-0000-000000000008'),
       (gen_random_uuid(), '55555555-0000-0000-0000-000000000002', '66666666-0000-0000-0000-000000000009'),
       (gen_random_uuid(), '55555555-0000-0000-0000-000000000002', '66666666-0000-0000-0000-000000000010');
