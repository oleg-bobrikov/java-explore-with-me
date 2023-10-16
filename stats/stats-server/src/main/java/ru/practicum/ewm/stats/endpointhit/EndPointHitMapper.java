package ru.practicum.ewm.stats.endpointhit;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.stats.dto.EndpointHitAnswerDto;
import ru.practicum.ewm.stats.dto.EndpointHitRequestDto;


@Mapper(componentModel = "spring")
public interface EndPointHitMapper {
    EndpointHitAnswerDto toDto(EndpointHit endpointHit);

    @Mapping(target = "id", expression = "java(1L)")
    EndpointHit toEntity(EndpointHitRequestDto endpointHitRequestDto);
}
