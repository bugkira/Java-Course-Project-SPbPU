package spbstu.TasksApplication.messaging;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class TaskCreatedMessage {
    private Long taskId;
    private String title;
    private String description;
    private Long userId;
    private LocalDateTime creationDate;
    private LocalDateTime targetDate;
}
