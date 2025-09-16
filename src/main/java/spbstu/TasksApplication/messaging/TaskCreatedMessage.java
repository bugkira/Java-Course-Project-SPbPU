package spbstu.TasksApplication.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreatedMessage implements Serializable {
    private Long taskId;
    private String title;
    private String description;
    private Long userId;
    private LocalDateTime creationDate;
    private LocalDateTime targetDate;
}
