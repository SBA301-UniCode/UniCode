package com.example.unicode.service.impl;

import com.example.unicode.entity.*;
import com.example.unicode.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CourseMindMapServiceImpl {

    private final CourseMindMapRepository mindMapRepository;
    private final CourseRepository courseRepository;
    private final ChapterRepository chapterRepository;
    private final LessonRepository lessonRepository;
    private final UsersRepository usersRepository;

    /**
     * Build the default tree JSON from course structure.
     */
    public String buildDefaultTree(UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found: " + courseId));

        List<Chapter> chapters = chapterRepository.findByCourse_CourseIdAndDeletedFalseOrderByOrderIndexAsc(courseId);

        StringBuilder nodes = new StringBuilder();
        StringBuilder links = new StringBuilder();

        // Course node
        String courseNodeId = "course-" + course.getCourseId();
        appendNode(nodes, courseNodeId, course.getTitle(), "COURSE", 0);

        for (Chapter ch : chapters) {
            String chNodeId = "ch-" + ch.getChapterId();
            appendNode(nodes, chNodeId, ch.getTitle(), "CHAPTER", 1);
            appendLink(links, courseNodeId, chNodeId);

            List<Lesson> lessons = lessonRepository.findByChapter_ChapterIdAndDeletedFalseOrderByOrderIndexAsc(ch.getChapterId());
            for (Lesson ls : lessons) {
                String lsNodeId = "ls-" + ls.getLessonId();
                appendNode(nodes, lsNodeId, ls.getTitle(), "LESSON", 2);
                appendLink(links, chNodeId, lsNodeId);

                // Add content nodes
                if (ls.getContentList() != null) {
                    for (Content ct : ls.getContentList()) {
                        if (ct.getDeleted() != null && ct.getDeleted()) continue;
                        String type = ct.getContentType() != null ? ct.getContentType().name() : "UNKNOWN";
                        String ctNodeId = "ct-" + ct.getContentId();
                        String label = type;
                        if (ct.getVideo() != null) label = "Video";
                        if (ct.getDocument() != null) label = ct.getDocument().getTitle() != null ? ct.getDocument().getTitle() : "Document";
                        if (ct.getExam() != null) label = ct.getExam().getName() != null ? ct.getExam().getName() : "Quiz";
                        appendNode(nodes, ctNodeId, label, type, 3);
                        appendLink(links, lsNodeId, ctNodeId);
                    }
                }
            }
        }

        // Remove trailing comma
        String nodesStr = nodes.length() > 0 ? nodes.substring(0, nodes.length() - 1) : "";
        String linksStr = links.length() > 0 ? links.substring(0, links.length() - 1) : "";

        return "{\"nodes\":[" + nodesStr + "],\"links\":[" + linksStr + "],\"notes\":{},\"nodePositions\":{}}";
    }

    private void appendNode(StringBuilder sb, String id, String label, String type, int group) {
        String escaped = label != null ? label.replace("\"", "\\\"") : "";
        sb.append("{\"id\":\"").append(id)
                .append("\",\"label\":\"").append(escaped)
                .append("\",\"type\":\"").append(type)
                .append("\",\"group\":").append(group)
                .append("},");
    }

    private void appendLink(StringBuilder sb, String source, String target) {
        sb.append("{\"source\":\"").append(source)
                .append("\",\"target\":\"").append(target)
                .append("\"},");
    }

    /**
     * Get user's custom tree or build default.
     */
    public String getUserTree(UUID userId, UUID courseId) {
        Optional<CourseMindMap> existing = mindMapRepository.findByUser_UserIdAndCourse_CourseId(userId, courseId);
        if (existing.isPresent() && existing.get().getTreeData() != null) {
            return existing.get().getTreeData();
        }
        return buildDefaultTree(courseId);
    }

    /**
     * Save user's custom tree.
     */
    public void saveUserTree(UUID userId, UUID courseId, String treeData) {
        Optional<CourseMindMap> existing = mindMapRepository.findByUser_UserIdAndCourse_CourseId(userId, courseId);
        CourseMindMap mindMap;
        if (existing.isPresent()) {
            mindMap = existing.get();
        } else {
            Users user = usersRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));
            mindMap = CourseMindMap.builder().user(user).course(course).build();
        }
        mindMap.setTreeData(treeData);
        mindMapRepository.save(mindMap);
    }

    /**
     * Reset to default (delete custom tree).
     */
    public void resetUserTree(UUID userId, UUID courseId) {
        mindMapRepository.deleteByUser_UserIdAndCourse_CourseId(userId, courseId);
    }

    /**
     * Resolve user email to userId.
     */
    public UUID getUserIdByEmail(String email) {
        Users user = usersRepository.findByEmail(email);
        if (user == null) throw new RuntimeException("User not found: " + email);
        return user.getUserId();
    }
}
