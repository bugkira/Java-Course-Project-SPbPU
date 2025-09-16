package spbstu.TasksApplication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import spbstu.TasksApplication.model.Task;
import spbstu.TasksApplication.service.TaskService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    private Task testTask;

    @BeforeEach
    void setUp() {
        testTask = Task.builder()
                .taskId(1L)
                .title("Test Task")
                .description("Test Description")
                .targetDate(LocalDateTime.now().plusDays(1))
                .userId(1L)
                .build();
    }

    @Test
    void getAllTasks_ShouldReturnTasks() throws Exception {
        List<Task> tasks = Arrays.asList(testTask);
        when(taskService.getAllTasks(1L)).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taskId").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Task"));
    }

    @Test
    void getPendingTasks_ShouldReturnPendingTasks() throws Exception {
        List<Task> tasks = Arrays.asList(testTask);
        when(taskService.getPendingTasks(1L)).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks/user/1/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taskId").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Task"));
    }

    @Test
    void createTask_ShouldCreateNewTask() throws Exception {
        when(taskService.createTask(any(Task.class))).thenReturn(testTask);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void updateTask_ShouldUpdateTask() throws Exception {
        when(taskService.updateTask(any(Long.class), any(Task.class))).thenReturn(testTask);

        mockMvc.perform(put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void completeTask_ShouldCompleteTask() throws Exception {
        mockMvc.perform(put("/api/tasks/1/complete"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTask_ShouldDeleteTask() throws Exception {
        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void createTask_ShouldReturnBadRequest_WhenTitleIsEmpty() throws Exception {
        testTask.setTitle("");
        when(taskService.createTask(any(Task.class))).thenThrow(new IllegalArgumentException("Task title cannot be empty"));

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isBadRequest());
    }
}
