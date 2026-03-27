package com.example.unicode.mapper;

import com.example.unicode.dto.request.*;
import com.example.unicode.entity.*;
import com.example.unicode.enums.ContentType;
import com.example.unicode.enums.QuestionType;
import com.example.unicode.enums.StatusContent;
import com.example.unicode.enums.StatusPayment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RemainingMappersTest {

    private final ContentMapper contentMapper = Mappers.getMapper(ContentMapper.class);
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private final RoleMapper roleMapper = Mappers.getMapper(RoleMapper.class);
    private final PrivilegeMapper privilegeMapper = Mappers.getMapper(PrivilegeMapper.class);
    private final CourseMapper courseMapper = Mappers.getMapper(CourseMapper.class);
    private final QuestionBankMapper questionBankMapper = Mappers.getMapper(QuestionBankMapper.class);
    private final QuestionOptionMapper questionOptionMapper = Mappers.getMapper(QuestionOptionMapper.class);
    private final EnrollmentMapper enrollmentMapper = Mappers.getMapper(EnrollmentMapper.class);
    private final ExamMapper examMapper = Mappers.getMapper(ExamMapper.class);
    private final ExamAttemptMapper examAttemptMapper = Mappers.getMapper(ExamAttemptMapper.class);
    private final FeedBackMapper feedBackMapper = Mappers.getMapper(FeedBackMapper.class);
    private final ImageMapper imageMapper = Mappers.getMapper(ImageMapper.class);
    private final ProcessMapper processMapper = Mappers.getMapper(ProcessMapper.class);
    private final SubcriptionMapper subcriptionMapper = Mappers.getMapper(SubcriptionMapper.class);
    private final SumariesMapper sumariesMapper = Mappers.getMapper(SumariesMapper.class);
    private final SylabusMapper sylabusMapper = Mappers.getMapper(SylabusMapper.class);

    @BeforeEach
    void setUpMapperDependencies() {
        // MapStruct spring-style mappers require manual wiring in plain unit tests.
        ReflectionTestUtils.setField(roleMapper, "privilegeMapper", privilegeMapper);
        ReflectionTestUtils.setField(userMapper, "roleMapper", roleMapper);
        ReflectionTestUtils.setField(enrollmentMapper, "courseMapper", courseMapper);
        ReflectionTestUtils.setField(feedBackMapper, "imageMapper", imageMapper);
        ReflectionTestUtils.setField(feedBackMapper, "userMapper", userMapper);
    }

    @Test
    void contentMapperShouldMapEntityResponseAndList() {
        ContentCreateRequest request = new ContentCreateRequest(ContentType.VIDEO, UUID.randomUUID());
        Content entity = contentMapper.toEntity(request);
        assertEquals(ContentType.VIDEO, entity.getContentType());

        Lesson lesson = new Lesson();
        lesson.setLessonId(UUID.randomUUID());
        Content content = new Content();
        content.setContentId(UUID.randomUUID());
        content.setContentType(ContentType.DOCUMENT);
        content.setLesson(lesson);

        var response = contentMapper.toResponse(content);
        assertEquals(content.getContentId(), response.getContentId());
        assertEquals(ContentType.DOCUMENT, response.getContentType());
        assertEquals(lesson.getLessonId(), response.getLessonId());

        assertEquals(1, contentMapper.toResponseList(List.of(content)).size());
        assertNull(contentMapper.toResponseList(null));
    }

    @Test
    void userMapperShouldMapCreateUpdateAndResponse() {
        UserCreateRequest createRequest = new UserCreateRequest("mail@x.com", "secret", "Alice", "avatar", Set.of("ADMIN"));
        Users entity = userMapper.toEntity(createRequest);

        assertEquals("mail@x.com", entity.getEmail());
        assertEquals("Alice", entity.getName());
        assertNull(entity.getPassword());
        assertNull(entity.getRolesList());

        Role role = new Role();
        role.setRoleCode("ADMIN");
        role.setRoleName("Admin");
        Users user = new Users();
        user.setUserId(UUID.randomUUID());
        user.setEmail("a@b.com");
        user.setName("Bob");
        user.setAvatarUrl("old-avatar");
        user.setActive(true);
        user.setRolesList(Set.of(role));

        var response = userMapper.toResponse(user);
        assertEquals(user.getUserId(), response.getUserId());
        assertEquals(1, response.getRoles().size());

        userMapper.updateEntity(new UserUpdateRequest("New Name", null, false, Set.of("USER")), user);
        assertEquals("New Name", user.getName());
        assertEquals("old-avatar", user.getAvatarUrl());
        assertFalse(user.isActive());
        assertEquals(1, user.getRolesList().size());

        assertEquals(1, userMapper.toResponseList(List.of(user)).size());
        assertNull(userMapper.toResponseList(null));
    }

    @Test
    void roleAndPrivilegeMappersShouldMapAndIgnoreConfiguredFields() {
        PrivilegeCreateRequest createPrivilege = new PrivilegeCreateRequest("P1", "Privilege", "desc");
        Privilege privilege = privilegeMapper.toEntity(createPrivilege);
        assertEquals("P1", privilege.getPrivilegeCode());
        assertEquals("Privilege", privilege.getPrivilegeName());

        var privilegeResponse = privilegeMapper.toResponse(privilege);
        assertEquals("P1", privilegeResponse.getPrivilegeCode());

        Privilege targetPrivilege = new Privilege();
        targetPrivilege.setPrivilegeCode("P2");
        targetPrivilege.setPrivilegeName("Old");
        targetPrivilege.setDescription("Old desc");
        privilegeMapper.updateEntity(new PrivilegeUpdateRequest(null, "New desc"), targetPrivilege);
        assertEquals("Old", targetPrivilege.getPrivilegeName());
        assertEquals("New desc", targetPrivilege.getDescription());

        RoleCreateRequest createRole = new RoleCreateRequest("ADMIN", "Admin", "role-desc", Set.of("P1"));
        Role role = roleMapper.toEntity(createRole);
        assertEquals("ADMIN", role.getRoleCode());
        assertEquals("Admin", role.getRoleName());
        assertTrue(role.getPrivileges().isEmpty());

        var roleResponse = roleMapper.toResponse(role);
        assertEquals("ADMIN", roleResponse.getRoleCode());

        roleMapper.updateEntity(new RoleUpdateRequest(null, "updated", Set.of("P2")), role);
        assertEquals("Admin", role.getRoleName());
        assertEquals("updated", role.getDescription());
        assertTrue(role.getPrivileges().isEmpty());

        assertEquals(1, roleMapper.toResponseList(List.of(role)).size());
        assertNull(roleMapper.toResponseList(null));
        assertEquals(1, privilegeMapper.toResponseList(List.of(privilege)).size());
        assertNull(privilegeMapper.toResponseList(null));
    }

    @Test
    void questionMappersShouldMapEntityAndNestedOptions() {
        QuestionOptionRequest optionRequest = new QuestionOptionRequest(UUID.randomUUID(), "A", true);
        QuestionOption optionEntity = questionOptionMapper.toEntity(optionRequest);
        assertEquals("A", optionEntity.getAnswerText());
        assertTrue(optionEntity.isCorrect());

        assertNotNull(questionOptionMapper.toResponse(optionEntity));

        QuestionBankRequest questionRequest = new QuestionBankRequest(
                "What is Java?",
                "img",
                1,
                QuestionType.MULTIPLE_CHOICE,
                List.of(optionRequest)
        );
        QuestionBank questionBank = questionBankMapper.toEntity(questionRequest);
        assertEquals("What is Java?", questionBank.getQuestionText());
        assertEquals(1, questionBank.getNumberAnswers());

        Lesson lesson = new Lesson();
        lesson.setLessonId(UUID.randomUUID());
        QuestionOption option = new QuestionOption();
        option.setOptionId(UUID.randomUUID());
        option.setAnswerText("A");
        option.setCorrect(true);

        questionBank.setQuestionBankId(UUID.randomUUID());
        questionBank.setLesson(lesson);
        questionBank.setQuestionOptionList(List.of(option));
        var response = questionBankMapper.toResponse(questionBank);

        assertEquals(lesson.getLessonId(), response.getLessonId());
        assertEquals(1, response.getOptions().size());
        assertEquals("A", response.getOptions().get(0).getAnswerText());

        assertEquals(1, questionBankMapper.toResponseList(List.of(questionBank)).size());
        assertNull(questionBankMapper.toResponseList(null));
    }

    @Test
    void enrollmentMapperShouldMapCourseToCourseResponse() {
        Course course = new Course();
        course.setCourseId(UUID.randomUUID());
        course.setTitle("Spring Course");

        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(UUID.randomUUID());
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setCourse(course);

        var response = enrollmentMapper.entityToResponse(enrollment);
        assertEquals(enrollment.getEnrollmentId(), response.getEnrollmentId());
        assertEquals("Spring Course", response.getCourseResponse().getTitle());
    }

    @Test
    void examAndExamAttemptMappersShouldMapExpectedFields() {
        ExamRequest examRequest = new ExamRequest("Midterm", 60, 70.0, 20);
        Exam exam = examMapper.toEntity(examRequest);
        assertEquals("Midterm", exam.getName());
        assertEquals(60, exam.getDuration());

        exam.setExamId(UUID.randomUUID());
        exam.setDeleted(true);
        var examResponse = examMapper.toResponse(exam);
        assertEquals("Midterm", examResponse.getName());
        assertTrue(examResponse.isDeleted());

        Users learner = new Users();
        learner.setName("Student");
        ExamAttempt attempt = new ExamAttempt();
        attempt.setExamAttemptId(UUID.randomUUID());
        attempt.setExam(exam);
        attempt.setLearner(learner);
        attempt.setScore(8.5);
        attempt.setPassed(true);

        var attemptResponse = examAttemptMapper.toResponse(attempt);
        assertEquals("Midterm", attemptResponse.getExamName());
        assertEquals("Student", attemptResponse.getLearnerName());

        var resultResponse = examAttemptMapper.toResultsResponse(attempt);
        assertEquals("Midterm", resultResponse.getExamName());
        assertEquals(8.5, resultResponse.getScore());
        assertTrue(resultResponse.getPassed());
    }

    @Test
    void feedbackAndImageMappersShouldMapRequestsAndNestedData() {
        Image image = new Image();
        image.setImageId(UUID.randomUUID());
        image.setImageUrl("https://img");
        var imageResponse = imageMapper.entityToResponse(image);
        assertEquals(image.getImageId(), imageResponse.getImageId());
        assertEquals("https://img", imageResponse.getImageUrl());

        FeedbackRequest request = new FeedbackRequest();
        request.setComment("good");
        request.setRating(5);
        Feedback feedbackFromRequest = feedBackMapper.requestToEntity(request);
        assertEquals("good", feedbackFromRequest.getComment());
        assertEquals(5, feedbackFromRequest.getRating());

        UpdateFeedbackRequest updateRequest = new UpdateFeedbackRequest();
        updateRequest.setComment("updated");
        updateRequest.setRating(4);
        Feedback feedbackFromUpdate = feedBackMapper.updateRequestToEntity(updateRequest);
        assertEquals("updated", feedbackFromUpdate.getComment());
        assertEquals(4, feedbackFromUpdate.getRating());

        Users learner = new Users();
        learner.setUserId(UUID.randomUUID());
        learner.setName("User");
        learner.setEmail("u@x.com");

        Feedback feedback = new Feedback();
        feedback.setFeedBackId(UUID.randomUUID());
        feedback.setComment("great");
        feedback.setRating(5);
        feedback.setLearner(learner);
        feedback.setImages(List.of(image));

        var response = feedBackMapper.entityToResponse(feedback);
        assertEquals(feedback.getFeedBackId(), response.getFeedBackId());
        assertEquals("User", response.getUserResponse().getName());
        assertEquals(1, response.getImageResponses().size());
    }

    @Test
    void processSubcriptionSummaryAndSylabusMappersShouldMapFields() {
        com.example.unicode.entity.Process process = new com.example.unicode.entity.Process();
        process.setProcessId(UUID.randomUUID());
        process.setStatusContent(StatusContent.COMPLETED);
        process.setCreatedAt(LocalDateTime.now());
        var processResponse = processMapper.entityToResponse(process);
        assertEquals(process.getProcessId(), processResponse.getProcessId());
        assertEquals(StatusContent.COMPLETED, processResponse.getStatusContent());

        Users buyer = new Users();
        buyer.setUserId(UUID.randomUUID());
        Course course = new Course();
        course.setCourseId(UUID.randomUUID());
        Subcription subcription = new Subcription();
        subcription.setSubcriptionId(UUID.randomUUID());
        subcription.setSubcriptionPrice(100_000L);
        subcription.setStatusPayment(StatusPayment.SUCCESS);
        subcription.setCourse(course);
        subcription.setLearner(buyer);

        var subResponse = subcriptionMapper.entityToResponse(subcription);
        assertEquals(course.getCourseId(), subResponse.getCourseraId());
        assertEquals(buyer.getUserId(), subResponse.getBuyerId());

        Summaries summaries = new Summaries();
        summaries.setLocalDate(LocalDate.now());
        summaries.setTotalAmount(1000L);
        summaries.setTotalPayment(10L);
        summaries.setSuccess(8L);
        summaries.setError(2L);

        var summaryResponse = sumariesMapper.entityToResponse(summaries);
        assertEquals(1000L, summaryResponse.getTotalAmount());
        assertEquals(8L, summaryResponse.getSuccess());

        SylabusCreateRequest createRequest = new SylabusCreateRequest(UUID.randomUUID(), "content", "online", "docs");
        Sylabus sylabus = sylabusMapper.toEntity(createRequest);
        assertEquals("content", sylabus.getCourseContent());
        assertNull(sylabus.getCourse());

        Course mappedCourse = new Course();
        mappedCourse.setCourseId(UUID.randomUUID());
        mappedCourse.setTitle("Java 101");
        sylabus.setCourse(mappedCourse);
        sylabus.setSylabusId("SYL-01");
        var sylabusResponse = sylabusMapper.toResponse(sylabus);
        assertEquals("SYL-01", sylabusResponse.getSylabusId());
        assertEquals(mappedCourse.getCourseId(), sylabusResponse.getCourseId());
        assertEquals("Java 101", sylabusResponse.getCourseTitle());

        sylabusMapper.updateEntity(new SylabusUpdateRequest(null, "hybrid", null), sylabus);
        assertEquals("content", sylabus.getCourseContent());
        assertEquals("hybrid", sylabus.getMethod());

        assertEquals(1, sylabusMapper.toResponseList(List.of(sylabus)).size());
        assertNull(sylabusMapper.toResponseList(null));
    }

    @Test
    void mappersShouldReturnNullForNullInputs() {
        assertNull(contentMapper.toEntity(null));
        assertNull(contentMapper.toResponse(null));
        assertNull(contentMapper.toResponseList(null));

        assertNull(userMapper.toEntity(null));
        assertNull(userMapper.toResponse(null));
        assertNull(userMapper.toResponseList(null));

        assertNull(roleMapper.toEntity(null));
        assertNull(roleMapper.toResponse(null));
        assertNull(roleMapper.toResponseList(null));

        assertNull(privilegeMapper.toEntity(null));
        assertNull(privilegeMapper.toResponse(null));
        assertNull(privilegeMapper.toResponseList(null));

        assertNull(questionOptionMapper.toEntity(null));
        assertNull(questionOptionMapper.toResponse(null));

        assertNull(questionBankMapper.toEntity(null));
        assertNull(questionBankMapper.toResponse(null));
        assertNull(questionBankMapper.toResponseList(null));

        assertNull(enrollmentMapper.entityToResponse(null));

        assertNull(examMapper.toEntity(null));
        assertNull(examMapper.toResponse(null));

        assertNull(examAttemptMapper.toResponse(null));
        assertNull(examAttemptMapper.toResultsResponse(null));

        assertNull(feedBackMapper.entityToResponse(null));
        assertNull(feedBackMapper.requestToEntity(null));
        assertNull(feedBackMapper.updateRequestToEntity(null));

        assertNull(imageMapper.entityToResponse(null));
        assertNull(processMapper.entityToResponse(null));
        assertNull(subcriptionMapper.entityToResponse(null));
        assertNull(sumariesMapper.entityToResponse(null));

        assertNull(sylabusMapper.toEntity(null));
        assertNull(sylabusMapper.toResponse(null));
        assertNull(sylabusMapper.toResponseList(null));
    }

    @Test
    void mappersShouldHandleNestedNullBranches() {
        Content contentNoLesson = new Content();
        contentNoLesson.setContentId(UUID.randomUUID());
        contentNoLesson.setContentType(ContentType.VIDEO);
        assertNull(contentMapper.toResponse(contentNoLesson).getLessonId());

        Content contentLessonNoId = new Content();
        Lesson lessonNoId = new Lesson();
        contentLessonNoId.setLesson(lessonNoId);
        assertNull(contentMapper.toResponse(contentLessonNoId).getLessonId());

        Role roleWithNullPrivileges = new Role();
        roleWithNullPrivileges.setRoleCode("R");
        roleWithNullPrivileges.setRoleName("Role");
        roleWithNullPrivileges.setPrivileges(null);
        assertNull(roleMapper.toResponse(roleWithNullPrivileges).getPrivileges());

        Role roleWithEmptyPrivileges = new Role();
        roleWithEmptyPrivileges.setRoleCode("R2");
        roleWithEmptyPrivileges.setRoleName("Role2");
        roleWithEmptyPrivileges.setPrivileges(Set.of());
        assertTrue(roleMapper.toResponse(roleWithEmptyPrivileges).getPrivileges().isEmpty());

        Role roleWithOnePrivilege = new Role();
        roleWithOnePrivilege.setRoleCode("R3");
        roleWithOnePrivilege.setRoleName("Role3");
        Privilege onePrivilege = new Privilege();
        onePrivilege.setPrivilegeCode("P_ONE");
        onePrivilege.setPrivilegeName("One");
        roleWithOnePrivilege.setPrivileges(Set.of(onePrivilege));
        assertEquals(1, roleMapper.toResponse(roleWithOnePrivilege).getPrivileges().size());

        Users userWithNullRoles = new Users();
        userWithNullRoles.setUserId(UUID.randomUUID());
        userWithNullRoles.setRolesList(null);
        assertNull(userMapper.toResponse(userWithNullRoles).getRoles());

        Feedback feedbackWithNullImages = new Feedback();
        feedbackWithNullImages.setLearner(new Users());
        feedbackWithNullImages.setImages(null);
        assertNull(feedBackMapper.entityToResponse(feedbackWithNullImages).getImageResponses());

        Privilege privilege = new Privilege();
        privilege.setPrivilegeName("old");
        privilegeMapper.updateEntity(null, privilege);
        assertEquals("old", privilege.getPrivilegeName());

        PrivilegeUpdateRequest privilegeUpdate = new PrivilegeUpdateRequest("new-name", null);
        privilegeMapper.updateEntity(privilegeUpdate, privilege);
        assertEquals("new-name", privilege.getPrivilegeName());

        Role role = new Role();
        role.setRoleName("oldRole");
        roleMapper.updateEntity(null, role);
        assertEquals("oldRole", role.getRoleName());

        RoleUpdateRequest roleUpdate = new RoleUpdateRequest("newRole", null, null);
        roleMapper.updateEntity(roleUpdate, role);
        assertEquals("newRole", role.getRoleName());

        Users user = new Users();
        user.setName("old-user");
        user.setAvatarUrl("old-avatar");
        user.setActive(true);
        userMapper.updateEntity(new UserUpdateRequest(null, "new-avatar", null, null), user);
        assertEquals("old-user", user.getName());
        assertEquals("new-avatar", user.getAvatarUrl());
        assertTrue(user.isActive());

        userMapper.updateEntity(new UserUpdateRequest(null, null, false, null), user);
        assertFalse(user.isActive());

        Users untouched = new Users();
        untouched.setName("untouched");
        userMapper.updateEntity(null, untouched);
        assertEquals("untouched", untouched.getName());
    }

    @Test
    void contentMapperPrivateHelperShouldReturnNullWhenContentIsNull() throws Exception {
        Method helper = contentMapper.getClass().getDeclaredMethod("contentLessonLessonId", Content.class);
        helper.setAccessible(true);

        Object result = helper.invoke(contentMapper, new Object[]{null});

        assertNull(result);
    }
}
