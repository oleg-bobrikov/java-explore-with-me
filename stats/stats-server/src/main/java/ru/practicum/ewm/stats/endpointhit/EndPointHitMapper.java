package ru.practicum.ewm.stats.endpointhit;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.stats.dto.EndpointHitResponseDto;
import ru.practicum.ewm.stats.dto.EndpointHitRequestDto;

@Mapper(componentModel = "spring")
public interface EndPointHitMapper {
    EndpointHitResponseDto toDto(EndpointHit endpointHit);

    @Mapping(target = "id", expression = "java(null)")
    EndpointHit toEntity(EndpointHitRequestDto endpointHitRequestDto);

}

