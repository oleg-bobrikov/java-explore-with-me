package ru.practicum.ewm.stats.endpointhit;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.common.LocalDateTimeConverter;
import ru.practicum.ewm.stats.dto.EndpointHitAnswerDto;
import ru.practicum.ewm.stats.dto.EndpointHitRequestDto;

@Mapper(imports = LocalDateTimeConverter.class, componentModel = "spring")
public interface EndPointHitMapper {
    EndpointHitAnswerDto toDto(EndpointHit endpointHit);

    @Mapping(target = "id", expression = "java(null)")
    EndpointHit toEntity(EndpointHitRequestDto endpointHitRequestDto);

}

