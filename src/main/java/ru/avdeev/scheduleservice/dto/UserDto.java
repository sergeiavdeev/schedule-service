package ru.avdeev.scheduleservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserDto {

    private String firstName;
    private String lastName;
    private String email;
}
