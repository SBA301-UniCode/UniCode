package com.example.unicode.exception;

import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // AUTHENCATION ERRORS(1)
    INVALID_AUTHENCATION(1001, "Invalid credentials", HttpStatus.UNAUTHORIZED),
    INVALID_LOGIN_REQUEST(1002, "username or password wrong", HttpStatus.BAD_REQUEST),

    // TOKEN ERRORS(2)
    REFRESH_TOKEN_NOT_FOUND(2001, "Refresh token not found", HttpStatus.NOT_FOUND),
    REFRESH_TOKEN_EXPIRED(2002, "Refresh token has expired.Please make a new login request", HttpStatus.BAD_REQUEST),

    // USERS ERRORS(3)
    USER_NOT_FOUND(3001, "User not found ", HttpStatus.NOT_FOUND),
    USER_INACTIVE(3002, "User is inactive", HttpStatus.FORBIDDEN),
    USER_ALREADY_EXISTS(3003, "User with this email already exists", HttpStatus.BAD_REQUEST),

    // PRIVILEGE ERRORS(4)
    PRIVILEGE_NOT_FOUND(4001, "Privilege not found", HttpStatus.NOT_FOUND),
    PRIVILEGE_ALREADY_EXISTS(4002, "Privilege already exists", HttpStatus.BAD_REQUEST),

    // ROLE ERRORS(5)
    ROLE_NOT_FOUND(5001, "Role not found", HttpStatus.NOT_FOUND),
    ROLE_ALREADY_EXISTS(5002, "Role already exists", HttpStatus.BAD_REQUEST),

    // CERTIFICATE ERRORS(6)
    CERTIFICATE_NOT_FOUND(6001, "Certificate not found", HttpStatus.NOT_FOUND),
    CERTIFICATE_ALREADY_EXISTS(6002, "Certificate already exists for this user and course", HttpStatus.BAD_REQUEST),

    // COURSE ERRORS(7)
    COURSE_NOT_FOUND(7001, "Course not found", HttpStatus.NOT_FOUND),

    // CHAPTER ERRORS(8)
    CHAPTER_NOT_FOUND(8001, "Chapter not found", HttpStatus.NOT_FOUND),

    // LESSON ERRORS(9)
    LESSON_NOT_FOUND(9001, "Lesson not found", HttpStatus.NOT_FOUND),

    // SYLLABUS ERRORS(10)
    SYLLABUS_NOT_FOUND(10001, "Syllabus not found", HttpStatus.NOT_FOUND),
    //SUBCRIPTION(11)
    PAYMENT_HASH_DATA_FAIL(11001,"Hash data payment fail",HttpStatus.BAD_REQUEST),
    SUBCRIPTION_NOT_FOUND(11002,"Subcription not found",HttpStatus.NOT_FOUND),
    VERIFY_SIGN_FAIL(11003,"Verify signature payment fail",HttpStatus.BAD_REQUEST),
    //CONTENT ERRORS(12)
    CONTENT_NOT_FOUND(12001, "Content  not found", HttpStatus.NOT_FOUND),
    //Enrollment(13)
    NOT_PAYMNET(13001,"You haven't bought this course yet ", HttpStatus.BAD_REQUEST),
    ENROLLMENT_NOT_FOUND(13002,"Enrollment not found ", HttpStatus.NOT_FOUND),
    //Exam(14)
    EXAM_NOT_FOUND(14001,"Exam not found",HttpStatus.NOT_FOUND),
    EXAM_INACTIVE(14002,"Exam is inactive",HttpStatus.BAD_REQUEST),
    EXAM_ATTEMPT_NOT_FOUND(14003,"Exam attempt not found",HttpStatus.NOT_FOUND),
    ANSWER_HISTORY_NOT_FOUND(14004,"Answer history not found",HttpStatus.NOT_FOUND),

    //Question bank(15)
    QUESTION_BANK_NOT_FOUND(15001,"Question bank not found",HttpStatus.NOT_FOUND),
    INSUFFICIENT_QUESTIONS(15002,"Not enough questions in the question bank to create the exam",HttpStatus.BAD_REQUEST),
    QUESTION_NOT_FOUND(15003,"Question not found",HttpStatus.NOT_FOUND),
    ANSWER_NOT_FOUND(15004,"Answer not found",HttpStatus.NOT_FOUND),
    ;
    int code;
    String message;
    HttpStatus status;

    ErrorCode(int code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
