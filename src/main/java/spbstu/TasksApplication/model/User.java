package spbstu.TasksApplication.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    
    @NonNull
    @Column(nullable = false, unique = true)
    private String username;
    
    @NonNull
    @Column(nullable = false)
    private String password;
    
    @NonNull
    @Column(nullable = false, unique = true)
    private String email;
}
