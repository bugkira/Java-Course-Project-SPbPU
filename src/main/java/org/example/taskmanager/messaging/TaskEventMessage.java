package org.example.taskmanager.messaging;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskEventMessage {
    
    private Long taskId;
    private String taskName;
    private String taskDetails;
    private Long ownerId;
    private String ownerLogin;
    private String ownerEmail;
    private LocalDateTime createTime;
    private LocalDateTime dueDate;
    private String eventType; // CREATED, UPDATED, DELETED
    private LocalDateTime eventTime;
    
    public static TaskEventMessage fromTaskCreated(org.example.taskmanager.domain.TaskEntity task, 
                                                  String ownerLogin, String ownerEmail) {
        return TaskEventMessage.builder()
                .taskId(task.getId())
                .taskName(task.getName())
                .taskDetails(task.getDetails())
                .ownerId(task.getOwnerId())
                .ownerLogin(ownerLogin)
                .ownerEmail(ownerEmail)
                .createTime(task.getCreateTime())
                .dueDate(task.getDueDate())
                .eventType("CREATED")
                .eventTime(LocalDateTime.now())
                .build();
    }
    
    public static TaskEventMessage fromTaskUpdated(org.example.taskmanager.domain.TaskEntity task, 
                                                  String ownerLogin, String ownerEmail) {
        return TaskEventMessage.builder()
                .taskId(task.getId())
                .taskName(task.getName())
                .taskDetails(task.getDetails())
                .ownerId(task.getOwnerId())
                .ownerLogin(ownerLogin)
                .ownerEmail(ownerEmail)
                .createTime(task.getCreateTime())
                .dueDate(task.getDueDate())
                .eventType("UPDATED")
                .eventTime(LocalDateTime.now())
                .build();
    }
    
    public static TaskEventMessage fromTaskDeleted(Long taskId, String taskName, Long ownerId, 
                                                  String ownerLogin, String ownerEmail) {
        return TaskEventMessage.builder()
                .taskId(taskId)
                .taskName(taskName)
                .ownerId(ownerId)
                .ownerLogin(ownerLogin)
                .ownerEmail(ownerEmail)
                .eventType("DELETED")
                .eventTime(LocalDateTime.now())
                .build();
    }
}
