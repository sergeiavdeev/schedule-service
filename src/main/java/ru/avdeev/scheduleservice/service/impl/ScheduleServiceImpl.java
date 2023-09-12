package ru.avdeev.scheduleservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.avdeev.scheduleservice.dto.ScheduleDto;
import ru.avdeev.scheduleservice.dto.TimeIntervalDto;
import ru.avdeev.scheduleservice.mapper.ScheduleMapper;
import ru.avdeev.scheduleservice.repository.ScheduleRepository;
import ru.avdeev.scheduleservice.service.ScheduleService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository repository;
    private final ScheduleMapper mapper;

    @Override
    public Flux<ScheduleDto> getByCalendarId(UUID calendarId) {
        return repository.findAllByCalendarIdOrderByDayOfWeekAscStartTimeAsc(calendarId)
                .map(mapper::toDto);
    }

    @Override
    public Flux<ScheduleDto> saveList(List<ScheduleDto> schedules, UUID calendarId) {

        List<ScheduleDto> schedulesToSave = new ArrayList<>();

        schedules.forEach(schedule -> schedule.getTimeIntervals().forEach(
                timeInterval -> schedulesToSave.add(new ScheduleDto(
                        null,
                        calendarId,
                        schedule.getDayOfWeek(),
                        new TimeIntervalDto(timeInterval.getStartTime(), timeInterval.getEndTime()),
                        null))));
        return repository.saveAll(schedulesToSave.stream().map(mapper::toEntity).toList())
                .map(mapper::toDto);
    }
}
