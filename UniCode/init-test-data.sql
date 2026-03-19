-- =====================================================
-- UniCode Test Data — Khởi tạo đầy đủ
-- Chạy sau khi Hibernate ddl-auto=update tạo bảng
-- student1@coursera.vn (ID ...0004) enrolled all courses
-- Password tất cả user: 12345 (bcrypt)
-- =====================================================
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ===================== CLEAN UP =====================
TRUNCATE TABLE question_exam, question_option, question_bank,
               exam, document, video, content, process,
               enrollment, rate_condition, feed_back,
               lesson, chapter, sylabus, coursera,
               user_role, role_privilege, users, roles, privilege
CASCADE;

-- ===================== 1. PRIVILEGE =====================
INSERT INTO privilege (privilege_code, privilege_name, description, created_by, created_at, deleted) VALUES
('P_VIEW_COURSE','Xem khóa học','Quyền xem nội dung khóa học','system',NOW(),false),
('P_CREATE_COURSE','Tạo khóa học','Quyền tạo mới khóa học','system',NOW(),false),
('P_EDIT_COURSE','Sửa khóa học','Quyền chỉnh sửa khóa học','system',NOW(),false),
('P_DELETE_COURSE','Xóa khóa học','Quyền xóa khóa học','system',NOW(),false),
('P_MANAGE_USER','Quản lý người dùng','Quyền quản lý học viên và giảng viên','system',NOW(),false),
('P_VIEW_ANALYTICS','Xem thống kê','Xem báo cáo doanh thu','system',NOW(),false),
('P_GRADE_EXAM','Chấm thi','Quyền chấm điểm bài thi','system',NOW(),false),
('P_UPLOAD_VIDEO','Tải video','Quyền tải lên bài giảng video','system',NOW(),false),
('P_COMMENT','Bình luận','Quyền bình luận trong bài học','system',NOW(),false),
('P_SYSTEM_CONFIG','Cấu hình hệ thống','Quyền admin cao nhất','system',NOW(),false);

-- ===================== 2. ROLES =====================
INSERT INTO roles (role_code, role_name, description, created_by, created_at, deleted) VALUES
('ADMIN','Quản trị viên','Quản trị toàn bộ hệ thống','system',NOW(),false),
('INSTRUCTOR','Giảng viên','Người tạo và quản lý khóa học','system',NOW(),false),
('LEARNER','Học viên','Người tham gia học tập','system',NOW(),false),
('SUPPORT','Hỗ trợ viên','Hỗ trợ khách hàng','system',NOW(),false);

-- ===================== 3. ROLE_PRIVILEGE =====================
INSERT INTO role_privilege (role_code, privilege_code) VALUES
('ADMIN','P_SYSTEM_CONFIG'),('ADMIN','P_MANAGE_USER'),
('INSTRUCTOR','P_CREATE_COURSE'),('INSTRUCTOR','P_EDIT_COURSE'),('INSTRUCTOR','P_UPLOAD_VIDEO'),
('LEARNER','P_VIEW_COURSE'),('LEARNER','P_COMMENT');

-- ===================== 4. USERS =====================
-- Mật khẩu: 12345 → bcrypt hash
INSERT INTO users (user_id, name, email, password, is_active, token_version, created_at, deleted) VALUES
('00000000-0000-0000-0000-000000000001','Nguyễn Văn Admin','admin@coursera.vn','$2a$10$QGAY3pmmE7qZzbthlxY7YuneYp4VAN2iE.OLJoJee9ktXYrvxlmB.',true,1,NOW(),false),
('00000000-0000-0000-0000-000000000002','Trần Thầy Giáo','teacher1@coursera.vn','$2a$10$QGAY3pmmE7qZzbthlxY7YuneYp4VAN2iE.OLJoJee9ktXYrvxlmB.',true,1,NOW(),false),
('00000000-0000-0000-0000-000000000003','Lê Cô Giáo','teacher2@coursera.vn','$2a$10$QGAY3pmmE7qZzbthlxY7YuneYp4VAN2iE.OLJoJee9ktXYrvxlmB.',true,1,NOW(),false),
('00000000-0000-0000-0000-000000000004','Phạm Học Viên A','student1@coursera.vn','$2a$10$QGAY3pmmE7qZzbthlxY7YuneYp4VAN2iE.OLJoJee9ktXYrvxlmB.',true,1,NOW(),false),
('00000000-0000-0000-0000-000000000005','Hoàng Học Viên B','student2@coursera.vn','$2a$10$QGAY3pmmE7qZzbthlxY7YuneYp4VAN2iE.OLJoJee9ktXYrvxlmB.',true,1,NOW(),false);

-- ===================== 5. USER_ROLE =====================
INSERT INTO user_role (user_id, role_code) VALUES
('00000000-0000-0000-0000-000000000001','ADMIN'),
('00000000-0000-0000-0000-000000000002','INSTRUCTOR'),
('00000000-0000-0000-0000-000000000003','INSTRUCTOR'),
('00000000-0000-0000-0000-000000000004','LEARNER'),
('00000000-0000-0000-0000-000000000005','LEARNER');

-- ===================== 6. SYLABUS =====================
INSERT INTO sylabus (sylabus_id, course_content, method, reference_material, created_at, deleted) VALUES
('SYL001','Lập trình Java cơ bản đến nâng cao','Online Video + Lab','Java Core Docs',NOW(),false),
('SYL002','Python cho phân tích dữ liệu','Thực hành Lab','Kaggle Datasets',NOW(),false),
('SYL003','Thiết kế Web với ReactJS','Project based','React Official Docs',NOW(),false);

-- ===================== 7. COURSES (3 khóa đầy đủ) =====================
INSERT INTO coursera (course_id, title, description, price, instructor_id, sylabus_id, created_at, deleted) VALUES
('11111111-0000-0000-0000-000000000001','Java Masterclass','Khóa học Java từ cơ bản đến nâng cao, bao gồm OOP, Collections, Streams và Spring Boot.',1500000,'00000000-0000-0000-0000-000000000002','SYL001',NOW(),false),
('11111111-0000-0000-0000-000000000002','Python Data Science','Học Python với Pandas, Numpy, Matplotlib để phân tích và trực quan hóa dữ liệu.',2000000,'00000000-0000-0000-0000-000000000002','SYL002',NOW(),false),
('11111111-0000-0000-0000-000000000003','ReactJS Frontend','Xây dựng web app hiện đại với React 18, Hooks, Router và state management.',1200000,'00000000-0000-0000-0000-000000000003','SYL003',NOW(),false);

-- ===================== 8. CHAPTERS =====================
-- Course 1: Java (3 chương)
INSERT INTO chapter (chapter_id, course_id, title, order_index, created_at, deleted) VALUES
('22222222-0000-0000-0000-000000000001','11111111-0000-0000-0000-000000000001','Chương 1: Nhập môn Java',0,NOW(),false),
('22222222-0000-0000-0000-000000000002','11111111-0000-0000-0000-000000000001','Chương 2: OOP trong Java',1,NOW(),false),
('22222222-0000-0000-0000-000000000003','11111111-0000-0000-0000-000000000001','Chương 3: Java Collections',2,NOW(),false),
-- Course 2: Python (2 chương)
('22222222-0000-0000-0000-000000000004','11111111-0000-0000-0000-000000000002','Chương 1: Cài đặt Python & IDE',0,NOW(),false),
('22222222-0000-0000-0000-000000000005','11111111-0000-0000-0000-000000000002','Chương 2: Pandas & Numpy',1,NOW(),false),
-- Course 3: ReactJS (2 chương)
('22222222-0000-0000-0000-000000000006','11111111-0000-0000-0000-000000000003','Chương 1: React Fundamentals',0,NOW(),false),
('22222222-0000-0000-0000-000000000007','11111111-0000-0000-0000-000000000003','Chương 2: React Hooks & Router',1,NOW(),false);

-- ===================== 9. LESSONS =====================
-- Java Ch1 (2 lessons)
INSERT INTO lesson (lesson_id, chapter_id, title, order_index, created_at, deleted) VALUES
('33333333-0000-0000-0000-000000000001','22222222-0000-0000-0000-000000000001','Cài đặt JDK và IDE',0,NOW(),false),
('33333333-0000-0000-0000-000000000002','22222222-0000-0000-0000-000000000001','Chương trình Hello World',1,NOW(),false),
-- Java Ch2 (2 lessons)
('33333333-0000-0000-0000-000000000003','22222222-0000-0000-0000-000000000002','Class và Object',0,NOW(),false),
('33333333-0000-0000-0000-000000000004','22222222-0000-0000-0000-000000000002','Kế thừa và Đa hình',1,NOW(),false),
-- Java Ch3 (1 lesson)
('33333333-0000-0000-0000-000000000005','22222222-0000-0000-0000-000000000003','List, Set và Map',0,NOW(),false),
-- Python Ch1 (2 lessons)
('33333333-0000-0000-0000-000000000006','22222222-0000-0000-0000-000000000004','Cài đặt Anaconda',0,NOW(),false),
('33333333-0000-0000-0000-000000000007','22222222-0000-0000-0000-000000000004','Python cơ bản',1,NOW(),false),
-- Python Ch2 (2 lessons)
('33333333-0000-0000-0000-000000000008','22222222-0000-0000-0000-000000000005','DataFrame với Pandas',0,NOW(),false),
('33333333-0000-0000-0000-000000000009','22222222-0000-0000-0000-000000000005','Numpy Arrays',1,NOW(),false),
-- React Ch1 (2 lessons)
('33333333-0000-0000-0000-000000000010','22222222-0000-0000-0000-000000000006','JSX là gì',0,NOW(),false),
('33333333-0000-0000-0000-000000000011','22222222-0000-0000-0000-000000000006','Components & Props',1,NOW(),false),
-- React Ch2 (2 lessons)
('33333333-0000-0000-0000-000000000012','22222222-0000-0000-0000-000000000007','useState & useEffect',0,NOW(),false),
('33333333-0000-0000-0000-000000000013','22222222-0000-0000-0000-000000000007','React Router',1,NOW(),false);

-- ===================== 10. CONTENT =====================
-- ContentType ordinal: 0=VIDEO, 1=DOCUMENT, 2=QUIZ
-- Mỗi lesson có 3 content: VIDEO + DOCUMENT + QUIZ
-- Java Ch1 L1
INSERT INTO content (content_id, lesson_id, content_type, created_at, deleted) VALUES
('44444444-0000-0000-0000-000000000001','33333333-0000-0000-0000-000000000001',0,NOW(),false),
('44444444-0000-0000-0000-000000000002','33333333-0000-0000-0000-000000000001',1,NOW(),false),
('44444444-0000-0000-0000-000000000003','33333333-0000-0000-0000-000000000001',2,NOW(),false),
-- Java Ch1 L2
('44444444-0000-0000-0000-000000000004','33333333-0000-0000-0000-000000000002',0,NOW(),false),
('44444444-0000-0000-0000-000000000005','33333333-0000-0000-0000-000000000002',1,NOW(),false),
('44444444-0000-0000-0000-000000000006','33333333-0000-0000-0000-000000000002',2,NOW(),false),
-- Java Ch2 L1
('44444444-0000-0000-0000-000000000007','33333333-0000-0000-0000-000000000003',0,NOW(),false),
('44444444-0000-0000-0000-000000000008','33333333-0000-0000-0000-000000000003',1,NOW(),false),
-- Java Ch2 L2
('44444444-0000-0000-0000-000000000009','33333333-0000-0000-0000-000000000004',0,NOW(),false),
('44444444-0000-0000-0000-000000000010','33333333-0000-0000-0000-000000000004',2,NOW(),false),
-- Java Ch3 L1
('44444444-0000-0000-0000-000000000011','33333333-0000-0000-0000-000000000005',0,NOW(),false),
('44444444-0000-0000-0000-000000000012','33333333-0000-0000-0000-000000000005',1,NOW(),false),
-- Python Ch1 L1
('44444444-0000-0000-0000-000000000013','33333333-0000-0000-0000-000000000006',0,NOW(),false),
('44444444-0000-0000-0000-000000000014','33333333-0000-0000-0000-000000000006',1,NOW(),false),
-- Python Ch1 L2
('44444444-0000-0000-0000-000000000015','33333333-0000-0000-0000-000000000007',0,NOW(),false),
('44444444-0000-0000-0000-000000000016','33333333-0000-0000-0000-000000000007',2,NOW(),false),
-- Python Ch2 L1
('44444444-0000-0000-0000-000000000017','33333333-0000-0000-0000-000000000008',0,NOW(),false),
('44444444-0000-0000-0000-000000000018','33333333-0000-0000-0000-000000000008',1,NOW(),false),
-- Python Ch2 L2
('44444444-0000-0000-0000-000000000019','33333333-0000-0000-0000-000000000009',0,NOW(),false),
-- React Ch1 L1
('44444444-0000-0000-0000-000000000020','33333333-0000-0000-0000-000000000010',0,NOW(),false),
('44444444-0000-0000-0000-000000000021','33333333-0000-0000-0000-000000000010',1,NOW(),false),
-- React Ch1 L2
('44444444-0000-0000-0000-000000000022','33333333-0000-0000-0000-000000000011',0,NOW(),false),
-- React Ch2 L1
('44444444-0000-0000-0000-000000000023','33333333-0000-0000-0000-000000000012',0,NOW(),false),
('44444444-0000-0000-0000-000000000024','33333333-0000-0000-0000-000000000012',2,NOW(),false),
-- React Ch2 L2
('44444444-0000-0000-0000-000000000025','33333333-0000-0000-0000-000000000013',0,NOW(),false),
('44444444-0000-0000-0000-000000000026','33333333-0000-0000-0000-000000000013',1,NOW(),false);

-- ===================== 11. VIDEO =====================
-- NOTE: Dùng video public trên Cloudinary sample để test stream proxy
-- Nếu cần test thật, upload video qua ManageCourseVideos để có publicId hợp lệ
INSERT INTO video (video_id, content_id, video_url, public_id, duration, created_at, deleted) VALUES
-- Java videos
('55550000-0000-0000-0000-000000000001','44444444-0000-0000-0000-000000000001','https://res.cloudinary.com/demo/video/upload/v1/samples/elephants.mp4','samples/elephants',60,NOW(),false),
('55550000-0000-0000-0000-000000000002','44444444-0000-0000-0000-000000000004','https://res.cloudinary.com/demo/video/upload/v1/samples/sea-turtle.mp4','samples/sea-turtle',45,NOW(),false),
('55550000-0000-0000-0000-000000000003','44444444-0000-0000-0000-000000000007','https://res.cloudinary.com/demo/video/upload/v1/samples/elephants.mp4','samples/elephants',90,NOW(),false),
('55550000-0000-0000-0000-000000000004','44444444-0000-0000-0000-000000000009','https://res.cloudinary.com/demo/video/upload/v1/samples/sea-turtle.mp4','samples/sea-turtle',75,NOW(),false),
('55550000-0000-0000-0000-000000000005','44444444-0000-0000-0000-000000000011','https://res.cloudinary.com/demo/video/upload/v1/samples/elephants.mp4','samples/elephants',120,NOW(),false),
-- Python videos
('55550000-0000-0000-0000-000000000006','44444444-0000-0000-0000-000000000013','https://res.cloudinary.com/demo/video/upload/v1/samples/sea-turtle.mp4','samples/sea-turtle',50,NOW(),false),
('55550000-0000-0000-0000-000000000007','44444444-0000-0000-0000-000000000015','https://res.cloudinary.com/demo/video/upload/v1/samples/elephants.mp4','samples/elephants',55,NOW(),false),
('55550000-0000-0000-0000-000000000008','44444444-0000-0000-0000-000000000017','https://res.cloudinary.com/demo/video/upload/v1/samples/sea-turtle.mp4','samples/sea-turtle',80,NOW(),false),
('55550000-0000-0000-0000-000000000009','44444444-0000-0000-0000-000000000019','https://res.cloudinary.com/demo/video/upload/v1/samples/elephants.mp4','samples/elephants',65,NOW(),false),
-- React videos
('55550000-0000-0000-0000-000000000010','44444444-0000-0000-0000-000000000020','https://res.cloudinary.com/demo/video/upload/v1/samples/sea-turtle.mp4','samples/sea-turtle',70,NOW(),false),
('55550000-0000-0000-0000-000000000011','44444444-0000-0000-0000-000000000022','https://res.cloudinary.com/demo/video/upload/v1/samples/elephants.mp4','samples/elephants',85,NOW(),false),
('55550000-0000-0000-0000-000000000012','44444444-0000-0000-0000-000000000023','https://res.cloudinary.com/demo/video/upload/v1/samples/sea-turtle.mp4','samples/sea-turtle',95,NOW(),false),
('55550000-0000-0000-0000-000000000013','44444444-0000-0000-0000-000000000025','https://res.cloudinary.com/demo/video/upload/v1/samples/elephants.mp4','samples/elephants',100,NOW(),false);

-- ===================== 12. DOCUMENT =====================
INSERT INTO document (document_id, content_id, title, document_url, created_at, deleted) VALUES
(gen_random_uuid(),'44444444-0000-0000-0000-000000000002','Hướng dẫn cài JDK','https://docs.oracle.com/en/java/javase/21/install/overview-jdk-installation.html',NOW(),false),
(gen_random_uuid(),'44444444-0000-0000-0000-000000000005','Slide Hello World','https://docs.oracle.com/javase/tutorial/getStarted/cupojava/index.html',NOW(),false),
(gen_random_uuid(),'44444444-0000-0000-0000-000000000008','Slide OOP Java','https://docs.oracle.com/javase/tutorial/java/concepts/index.html',NOW(),false),
(gen_random_uuid(),'44444444-0000-0000-0000-000000000012','Java Collections Cheatsheet','https://docs.oracle.com/javase/tutorial/collections/index.html',NOW(),false),
(gen_random_uuid(),'44444444-0000-0000-0000-000000000014','Hướng dẫn Anaconda','https://docs.anaconda.com/anaconda/install/',NOW(),false),
(gen_random_uuid(),'44444444-0000-0000-0000-000000000018','Pandas Cheatsheet','https://pandas.pydata.org/docs/getting_started/intro_tutorials/index.html',NOW(),false),
(gen_random_uuid(),'44444444-0000-0000-0000-000000000021','JSX Documentation','https://react.dev/learn/writing-markup-with-jsx',NOW(),false),
(gen_random_uuid(),'44444444-0000-0000-0000-000000000026','React Router Guide','https://reactrouter.com/en/main/start/tutorial',NOW(),false);

-- ===================== 13. EXAM =====================
INSERT INTO exam (exam_id, content_id, name, duration, pass_score, number_questions, created_at, deleted) VALUES
('55555555-0000-0000-0000-000000000001','44444444-0000-0000-0000-000000000003','Kiểm tra: Cài đặt JDK',300,60,3,NOW(),false),
('55555555-0000-0000-0000-000000000002','44444444-0000-0000-0000-000000000006','Kiểm tra: Hello World',300,60,3,NOW(),false),
('55555555-0000-0000-0000-000000000003','44444444-0000-0000-0000-000000000010','Kiểm tra: Kế thừa',600,60,4,NOW(),false),
('55555555-0000-0000-0000-000000000004','44444444-0000-0000-0000-000000000016','Kiểm tra: Python cơ bản',300,60,3,NOW(),false),
('55555555-0000-0000-0000-000000000005','44444444-0000-0000-0000-000000000024','Kiểm tra: React Hooks',600,60,3,NOW(),false);

-- ===================== 14. QUESTION_BANK =====================
-- Java Lesson 1: Cài JDK (3 câu)
INSERT INTO question_bank (question_bank_id, lesson_id, question_text, question_type, number_answers, created_at, deleted) VALUES
('66660000-0000-0000-0000-000000000001','33333333-0000-0000-0000-000000000001','JDK bao gồm?','MULTIPLE_CHOICE',4,NOW(),false),
('66660000-0000-0000-0000-000000000002','33333333-0000-0000-0000-000000000001','JRE có thể biên dịch Java?','TRUE_FALSE',2,NOW(),false),
('66660000-0000-0000-0000-000000000003','33333333-0000-0000-0000-000000000001','IDE phổ biến cho Java?','MULTIPLE_CHOICE',4,NOW(),false),
-- Java Lesson 2: Hello World (3 câu)
('66660000-0000-0000-0000-000000000004','33333333-0000-0000-0000-000000000002','Hàm main trả về kiểu gì?','MULTIPLE_CHOICE',4,NOW(),false),
('66660000-0000-0000-0000-000000000005','33333333-0000-0000-0000-000000000002','System.out.println in ra console?','TRUE_FALSE',2,NOW(),false),
('66660000-0000-0000-0000-000000000006','33333333-0000-0000-0000-000000000002','Lệnh biên dịch Java?','MULTIPLE_CHOICE',4,NOW(),false),
-- Java Lesson 4: Kế thừa (4 câu)
('66660000-0000-0000-0000-000000000007','33333333-0000-0000-0000-000000000004','Java hỗ trợ đa kế thừa class?','TRUE_FALSE',2,NOW(),false),
('66660000-0000-0000-0000-000000000008','33333333-0000-0000-0000-000000000004','Keyword nào dùng cho kế thừa?','MULTIPLE_CHOICE',4,NOW(),false),
('66660000-0000-0000-0000-000000000009','33333333-0000-0000-0000-000000000004','Override là gì?','MULTIPLE_CHOICE',4,NOW(),false),
('66660000-0000-0000-0000-000000000010','33333333-0000-0000-0000-000000000004','abstract class có thể tạo object?','TRUE_FALSE',2,NOW(),false),
-- Python Lesson 2: Python cơ bản (3 câu)
('66660000-0000-0000-0000-000000000011','33333333-0000-0000-0000-000000000007','Python là ngôn ngữ biên dịch?','TRUE_FALSE',2,NOW(),false),
('66660000-0000-0000-0000-000000000012','33333333-0000-0000-0000-000000000007','Kiểu dữ liệu nào là immutable?','MULTIPLE_CHOICE',4,NOW(),false),
('66660000-0000-0000-0000-000000000013','33333333-0000-0000-0000-000000000007','Cú pháp print trong Python 3?','MULTIPLE_CHOICE',4,NOW(),false),
-- React Lesson 3: Hooks (3 câu)
('66660000-0000-0000-0000-000000000014','33333333-0000-0000-0000-000000000012','useState trả về gì?','MULTIPLE_CHOICE',4,NOW(),false),
('66660000-0000-0000-0000-000000000015','33333333-0000-0000-0000-000000000012','useEffect chạy khi nào?','MULTIPLE_CHOICE',4,NOW(),false),
('66660000-0000-0000-0000-000000000016','33333333-0000-0000-0000-000000000012','Hook chỉ dùng trong function component?','TRUE_FALSE',2,NOW(),false);

-- ===================== 15. QUESTION_OPTION =====================
-- Q1: JDK bao gồm?
INSERT INTO question_option (option_id, question_bank_id, answer_text, is_correct, created_at, deleted) VALUES
(gen_random_uuid(),'66660000-0000-0000-0000-000000000001','JRE + Compiler + Tools',true,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000001','Chỉ JRE',false,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000001','Chỉ Compiler',false,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000001','Chỉ JVM',false,NOW(),false),
-- Q2: JRE biên dịch?
(gen_random_uuid(),'66660000-0000-0000-0000-000000000002','True',false,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000002','False',true,NOW(),false),
-- Q3: IDE phổ biến?
(gen_random_uuid(),'66660000-0000-0000-0000-000000000003','IntelliJ IDEA',true,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000003','Photoshop',false,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000003','Excel',false,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000003','PowerPoint',false,NOW(),false),
-- Q4: Hàm main trả về?
(gen_random_uuid(),'66660000-0000-0000-0000-000000000004','void',true,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000004','int',false,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000004','String',false,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000004','boolean',false,NOW(),false),
-- Q5: println in console?
(gen_random_uuid(),'66660000-0000-0000-0000-000000000005','True',true,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000005','False',false,NOW(),false),
-- Q6: Lệnh biên dịch?
(gen_random_uuid(),'66660000-0000-0000-0000-000000000006','javac',true,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000006','java',false,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000006','jar',false,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000006','jdb',false,NOW(),false),
-- Q7: Đa kế thừa class?
(gen_random_uuid(),'66660000-0000-0000-0000-000000000007','True',false,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000007','False',true,NOW(),false),
-- Q8: Keyword kế thừa?
(gen_random_uuid(),'66660000-0000-0000-0000-000000000008','extends',true,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000008','implements',false,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000008','inherits',false,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000008','super',false,NOW(),false),
-- Q9: Override?
(gen_random_uuid(),'66660000-0000-0000-0000-000000000009','Ghi đè method cha',true,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000009','Tạo method mới',false,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000009','Xóa method cha',false,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000009','Copy method',false,NOW(),false),
-- Q10: abstract tạo object?
(gen_random_uuid(),'66660000-0000-0000-0000-000000000010','True',false,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000010','False',true,NOW(),false),
-- Q11: Python biên dịch?
(gen_random_uuid(),'66660000-0000-0000-0000-000000000011','True',false,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000011','False',true,NOW(),false),
-- Q12: Immutable type?
(gen_random_uuid(),'66660000-0000-0000-0000-000000000012','tuple',true,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000012','list',false,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000012','dict',false,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000012','set',false,NOW(),false),
-- Q13: Cú pháp print?
(gen_random_uuid(),'66660000-0000-0000-0000-000000000013','print("hello")',true,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000013','echo "hello"',false,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000013','console.log("hello")',false,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000013','printf("hello")',false,NOW(),false),
-- Q14: useState trả về?
(gen_random_uuid(),'66660000-0000-0000-0000-000000000014','[state, setState]',true,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000014','state object',false,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000014','setState function',false,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000014','undefined',false,NOW(),false),
-- Q15: useEffect chạy khi nào?
(gen_random_uuid(),'66660000-0000-0000-0000-000000000015','Sau mỗi render',true,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000015','Trước render',false,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000015','Chỉ lần đầu',false,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000015','Không bao giờ',false,NOW(),false),
-- Q16: Hook chỉ function component?
(gen_random_uuid(),'66660000-0000-0000-0000-000000000016','True',true,NOW(),false),
(gen_random_uuid(),'66660000-0000-0000-0000-000000000016','False',false,NOW(),false);

-- ===================== 16. QUESTION_EXAM =====================
-- Exam 1 (JDK) ← Q1, Q2, Q3
INSERT INTO question_exam (question_id, exam_id, question_bank_id, created_at, deleted) VALUES
(gen_random_uuid(),'55555555-0000-0000-0000-000000000001','66660000-0000-0000-0000-000000000001',NOW(),false),
(gen_random_uuid(),'55555555-0000-0000-0000-000000000001','66660000-0000-0000-0000-000000000002',NOW(),false),
(gen_random_uuid(),'55555555-0000-0000-0000-000000000001','66660000-0000-0000-0000-000000000003',NOW(),false),
-- Exam 2 (Hello World) ← Q4, Q5, Q6
(gen_random_uuid(),'55555555-0000-0000-0000-000000000002','66660000-0000-0000-0000-000000000004',NOW(),false),
(gen_random_uuid(),'55555555-0000-0000-0000-000000000002','66660000-0000-0000-0000-000000000005',NOW(),false),
(gen_random_uuid(),'55555555-0000-0000-0000-000000000002','66660000-0000-0000-0000-000000000006',NOW(),false),
-- Exam 3 (Kế thừa) ← Q7, Q8, Q9, Q10
(gen_random_uuid(),'55555555-0000-0000-0000-000000000003','66660000-0000-0000-0000-000000000007',NOW(),false),
(gen_random_uuid(),'55555555-0000-0000-0000-000000000003','66660000-0000-0000-0000-000000000008',NOW(),false),
(gen_random_uuid(),'55555555-0000-0000-0000-000000000003','66660000-0000-0000-0000-000000000009',NOW(),false),
(gen_random_uuid(),'55555555-0000-0000-0000-000000000003','66660000-0000-0000-0000-000000000010',NOW(),false),
-- Exam 4 (Python) ← Q11, Q12, Q13
(gen_random_uuid(),'55555555-0000-0000-0000-000000000004','66660000-0000-0000-0000-000000000011',NOW(),false),
(gen_random_uuid(),'55555555-0000-0000-0000-000000000004','66660000-0000-0000-0000-000000000012',NOW(),false),
(gen_random_uuid(),'55555555-0000-0000-0000-000000000004','66660000-0000-0000-0000-000000000013',NOW(),false),
-- Exam 5 (React Hooks) ← Q14, Q15, Q16
(gen_random_uuid(),'55555555-0000-0000-0000-000000000005','66660000-0000-0000-0000-000000000014',NOW(),false),
(gen_random_uuid(),'55555555-0000-0000-0000-000000000005','66660000-0000-0000-0000-000000000015',NOW(),false),
(gen_random_uuid(),'55555555-0000-0000-0000-000000000005','66660000-0000-0000-0000-000000000016',NOW(),false);

-- ===================== 17. ENROLLMENT =====================
-- student1 (ID ...0004) enrolled in ALL 3 courses
INSERT INTO enrollment (enrollment_id, course_id, learner_id, enrollment_date, status_course, created_at, deleted) VALUES
('AAAAAAAA-0000-0000-0000-000000000001','11111111-0000-0000-0000-000000000001','00000000-0000-0000-0000-000000000004',NOW(),'IN_PROGRESS',NOW(),false),
('AAAAAAAA-0000-0000-0000-000000000002','11111111-0000-0000-0000-000000000002','00000000-0000-0000-0000-000000000004',NOW(),'IN_PROGRESS',NOW(),false),
('AAAAAAAA-0000-0000-0000-000000000003','11111111-0000-0000-0000-000000000003','00000000-0000-0000-0000-000000000004',NOW(),'NOT_STARTED',NOW(),false),
-- student2 enrolled in Java
('AAAAAAAA-0000-0000-0000-000000000004','11111111-0000-0000-0000-000000000001','00000000-0000-0000-0000-000000000005',NOW(),'IN_PROGRESS',NOW(),false);

-- ===================== 18. FEEDBACK =====================
INSERT INTO feed_back (feed_back_id, course_id, learner_id, comment, rating, created_at, deleted) VALUES
(gen_random_uuid(),'11111111-0000-0000-0000-000000000001','00000000-0000-0000-0000-000000000004','Khóa học rất chi tiết, giảng viên dạy dễ hiểu!',5,NOW(),false),
(gen_random_uuid(),'11111111-0000-0000-0000-000000000002','00000000-0000-0000-0000-000000000004','Nội dung thực tế, có nhiều ví dụ hay.',4,NOW(),false),
(gen_random_uuid(),'11111111-0000-0000-0000-000000000001','00000000-0000-0000-0000-000000000005','Bài tập hơi khó nhưng giúp hiểu sâu hơn.',4,NOW(),false);

-- ===================== DONE =====================
-- Cây phả hệ dữ liệu:
-- Course 1: Java Masterclass
--   ├── Ch1: Nhập môn Java
--   │   ├── L1: Cài JDK → VIDEO + DOC + QUIZ(3q)
--   │   └── L2: Hello World → VIDEO + DOC + QUIZ(3q)
--   ├── Ch2: OOP trong Java
--   │   ├── L1: Class & Object → VIDEO + DOC
--   │   └── L2: Kế thừa → VIDEO + QUIZ(4q)
--   └── Ch3: Collections
--       └── L1: List/Set/Map → VIDEO + DOC
--
-- Course 2: Python Data Science
--   ├── Ch1: Cài đặt Python
--   │   ├── L1: Anaconda → VIDEO + DOC
--   │   └── L2: Python cơ bản → VIDEO + QUIZ(3q)
--   └── Ch2: Pandas & Numpy
--       ├── L1: DataFrame → VIDEO + DOC
--       └── L2: Numpy → VIDEO
--
-- Course 3: ReactJS Frontend
--   ├── Ch1: Fundamentals
--   │   ├── L1: JSX → VIDEO + DOC
--   │   └── L2: Components → VIDEO
--   └── Ch2: Hooks & Router
--       ├── L1: Hooks → VIDEO + QUIZ(3q)
--       └── L2: Router → VIDEO + DOC
