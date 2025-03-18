package ru.avdeev.scheduleservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class BookingCancelDto {
    UUID id;
}
