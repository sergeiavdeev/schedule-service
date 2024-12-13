package ru.avdeev.scheduleservice.mapper;

import org.mapstruct.Mapper;
import ru.avdeev.scheduleservice.dto.OrderDto;
import ru.avdeev.scheduleservice.entity.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    Order toEntity(OrderDto orderDto);
    OrderDto toDto(Order order);
}
