package org.example.authexample.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String phoneNumber;
    private String password;
    private String firstName;
    private String lastName;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles; // Роли пользователя
}
