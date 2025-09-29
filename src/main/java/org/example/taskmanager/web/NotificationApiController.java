package org.example.taskmanager.web;

import java.util.List;

import org.example.taskmanager.domain.NotificationEntity;
import org.example.taskmanager.service.NotificationManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationApiController {
    private final NotificationManagementService notificationService;

    public NotificationApiController(NotificationManagementService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/{recipientId}")
    public ResponseEntity<List<NotificationEntity>> retrieveAllNotifications(@PathVariable Long recipientId) {
        return ResponseEntity.ok(notificationService.getAllUserNotifications(recipientId));
    }

    @GetMapping("/{recipientId}/unread")
    public ResponseEntity<List<NotificationEntity>> retrieveUnreadNotifications(@PathVariable Long recipientId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(recipientId));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleValidationError(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericError(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
    }
}
