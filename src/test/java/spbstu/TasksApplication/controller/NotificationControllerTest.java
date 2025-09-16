package spbstu.TasksApplication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import spbstu.TasksApplication.model.Notification;
import spbstu.TasksApplication.service.NotificationService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NotificationService notificationService;

    private Notification testNotification;

    @BeforeEach
    void setUp() {
        testNotification = Notification.builder()
                .notificationId(1L)
                .text("Test notification")
                .date(LocalDateTime.now())
                .taskId(1L)
                .userId(1L)
                .isRead(false)
                .build();
    }

    @Test
    void getAllNotifications_ShouldReturnNotifications() throws Exception {
        List<Notification> notifications = Arrays.asList(testNotification);
        when(notificationService.getAllNotifications(1L)).thenReturn(notifications);

        mockMvc.perform(get("/api/notifications/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].notificationId").value(1))
                .andExpect(jsonPath("$[0].text").value("Test notification"));
    }

    @Test
    void getUnreadNotifications_ShouldReturnUnreadNotifications() throws Exception {
        List<Notification> notifications = Arrays.asList(testNotification);
        when(notificationService.getUnreadNotifications(1L)).thenReturn(notifications);

        mockMvc.perform(get("/api/notifications/user/1/unread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].notificationId").value(1))
                .andExpect(jsonPath("$[0].text").value("Test notification"));
    }

    @Test
    void createNotification_ShouldCreateNewNotification() throws Exception {
        when(notificationService.createNotification(any(Notification.class))).thenReturn(testNotification);

        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testNotification)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notificationId").value(1))
                .andExpect(jsonPath("$.text").value("Test notification"));
    }

    @Test
    void markAsRead_ShouldMarkNotificationAsRead() throws Exception {
        mockMvc.perform(put("/api/notifications/1/read"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteNotification_ShouldDeleteNotification() throws Exception {
        mockMvc.perform(delete("/api/notifications/1"))
                .andExpect(status().isNoContent());
    }
}
