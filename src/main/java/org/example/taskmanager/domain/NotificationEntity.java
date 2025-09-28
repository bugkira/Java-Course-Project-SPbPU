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
public class NotificationEntity {
    private Long id;

    @NonNull
    private String message;

    @NonNull
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    @NonNull
    private Long relatedTaskId;

    @NonNull
    private Long recipientId;

    @NonNull
    @Builder.Default
    private Boolean viewed = false;
}
