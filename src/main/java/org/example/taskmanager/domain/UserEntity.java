package org.example.taskmanager.domain;

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
public class UserEntity {
    private Long id;
    
    @NonNull
    private String login;
    
    @NonNull
    private String passwordHash;
    
    @NonNull
    private String emailAddress;
}
