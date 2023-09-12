package ru.avdeev.scheduleservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.avdeev.scheduleservice.dto.DeviationDto;
import ru.avdeev.scheduleservice.dto.TimeIntervalDto;
import ru.avdeev.scheduleservice.entity.Deviation;

@Mapper(componentModel = "spring", imports = {TimeIntervalDto.class})
public interface DeviationMapper {

    @Mappings({
            @Mapping(target="timeInterval", expression="java(new TimeIntervalDto(deviation.startTime(), deviation.endTime()))")
    })
    DeviationDto toDto(Deviation deviation);

    @Mappings({
            @Mapping(target="startTime", source = "deviationDto.timeInterval.startTime"),
            @Mapping(target="endTime", source = "deviationDto.timeInterval.endTime")
    })
    Deviation toEntity(DeviationDto deviationDto);
}
