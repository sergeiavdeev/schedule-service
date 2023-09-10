package ru.avdeev.scheduleservice.mapper;

import org.mapstruct.Mapper;
import ru.avdeev.scheduleservice.dto.CalendarDto;
import ru.avdeev.scheduleservice.entity.Calendar;

@Mapper(componentModel = "spring")
public interface CalendarMapper {

    Calendar toEntity(CalendarDto calendarDto);
    CalendarDto toDto(Calendar calendar);
}
