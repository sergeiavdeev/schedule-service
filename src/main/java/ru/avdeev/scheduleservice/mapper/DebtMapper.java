package ru.avdeev.scheduleservice.mapper;

import org.mapstruct.Mapper;
import ru.avdeev.scheduleservice.dto.DebtDto;
import ru.avdeev.scheduleservice.entity.Debt;

@Mapper(componentModel = "spring")
public interface DebtMapper {
    Debt toEntity(DebtDto debtDto);
    DebtDto toDto(Debt debt);
}
