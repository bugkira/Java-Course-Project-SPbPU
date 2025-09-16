package spbstu.TasksApplication.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;
    
    @NonNull
    @Column(nullable = false)
    private String title;
    
    @NonNull
    @Column(nullable = false)
    private String description;
    
    @NonNull
    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
    
    @NonNull
    @Column(name = "target_date", nullable = false)
    private LocalDateTime targetDate;
    
    @NonNull
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @NonNull
    @Column(name = "is_completed", nullable = false)
    @Builder.Default
    private Boolean isCompleted = false;
    
    @NonNull
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;
}
