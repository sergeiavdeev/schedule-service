package ru.avdeev.scheduleservice.mapper;

import org.mapstruct.Mapper;
import ru.avdeev.scheduleservice.dto.BookingDto;
import ru.avdeev.scheduleservice.entity.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    Booking toEntity(BookingDto bookingDto);
    BookingDto toDto(Booking booking);
}
