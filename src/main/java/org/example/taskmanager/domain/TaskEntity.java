package org.example.taskmanager.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TaskEntity {
    private Long id;
    
    @NonNull
    private String name;
    
    @NonNull
    private String details;
    
    @NonNull
    @Builder.Default
    private LocalDateTime createTime = LocalDateTime.now();
    
    @NonNull
    private LocalDateTime dueDate;
    
    @NonNull
    private Long ownerId;
    
    @NonNull
    @Builder.Default
    private Boolean finished = false;
    
    @NonNull
    @Builder.Default
    private Boolean removed = false;
}
