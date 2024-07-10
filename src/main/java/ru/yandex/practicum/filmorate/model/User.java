package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class User {
    private Long id;
    @Email
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private final Set<Long> friends = new HashSet<>();
}