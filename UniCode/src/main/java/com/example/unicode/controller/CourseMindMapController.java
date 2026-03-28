package com.example.unicode.controller;

import com.example.unicode.base.ApiResponse;
import com.example.unicode.service.impl.CourseMindMapServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/mindmap")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "MindMap", description = "Course Mind-Map APIs")
public class CourseMindMapController {

    private final CourseMindMapServiceImpl mindMapService;

    @GetMapping("/{courseId}")
    @Operation(summary = "Get user's mind-map tree for a course")
    public ResponseEntity<?> getTree(@PathVariable UUID courseId) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            UUID userId = mindMapService.getUserIdByEmail(email);
            String treeData = mindMapService.getUserTree(userId, courseId);
            return ResponseEntity.ok(ApiResponse.builder().data(treeData).build());
        } catch (Exception e) {
            log.error("Failed to get mind-map tree for course {}: {}", courseId, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.builder().data("Error: " + e.getMessage()).build());
        }
    }

    @PutMapping("/{courseId}")
    @Operation(summary = "Save user's customized mind-map tree")
    public ResponseEntity<?> saveTree(@PathVariable UUID courseId, @RequestBody String treeData) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            UUID userId = mindMapService.getUserIdByEmail(email);
            mindMapService.saveUserTree(userId, courseId, treeData);
            return ResponseEntity.ok(ApiResponse.builder().data("Saved").build());
        } catch (Exception e) {
            log.error("Failed to save mind-map for course {}: {}", courseId, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.builder().data("Error: " + e.getMessage()).build());
        }
    }

    @DeleteMapping("/{courseId}")
    @Operation(summary = "Reset mind-map to default course structure")
    public ResponseEntity<?> resetTree(@PathVariable UUID courseId) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            UUID userId = mindMapService.getUserIdByEmail(email);
            mindMapService.resetUserTree(userId, courseId);
            return ResponseEntity.ok(ApiResponse.builder().data("Reset OK").build());
        } catch (Exception e) {
            log.error("Failed to reset mind-map for course {}: {}", courseId, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.builder().data("Error: " + e.getMessage()).build());
        }
    }
}
