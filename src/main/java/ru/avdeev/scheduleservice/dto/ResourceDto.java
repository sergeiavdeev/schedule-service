package ru.avdeev.scheduleservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ResourceDto {

    private UUID id;
    private String name;
    private String description;
    private UUID storage;
}
