package org.example.taskmanager.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
