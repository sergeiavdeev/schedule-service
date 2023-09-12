package ru.avdeev.scheduleservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.avdeev.scheduleservice.dto.ScheduleDto;
import ru.avdeev.scheduleservice.dto.TimeIntervalDto;
import ru.avdeev.scheduleservice.entity.Schedule;

import java.util.List;

@Mapper(componentModel = "spring", imports = {TimeIntervalDto.class, List.class})
public interface ScheduleMapper {

    @Mappings({
            @Mapping(target="timeInterval", expression="java(new TimeIntervalDto(schedule.startTime(), schedule.endTime()))")
    })
    ScheduleDto toDto(Schedule schedule);

    @Mappings({
            @Mapping(target="startTime", source = "scheduleDto.timeInterval.startTime"),
            @Mapping(target="endTime", source = "scheduleDto.timeInterval.endTime")
    })
    Schedule toEntity(ScheduleDto scheduleDto);
}
