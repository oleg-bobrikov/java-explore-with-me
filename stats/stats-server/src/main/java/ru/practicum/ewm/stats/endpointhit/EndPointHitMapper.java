package ru.practicum.ewm.stats.endpointhit;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.ewm.common.LocalDateTimeConverter;
import ru.practicum.ewm.stats.dto.EndpointHitAnswerDto;
import ru.practicum.ewm.stats.dto.EndpointHitRequestDto;

import java.time.LocalDateTime;

@Mapper(imports = LocalDateTimeConverter.class, componentModel = "spring")
public interface EndPointHitMapper {
    EndpointHitAnswerDto toDto(EndpointHit endpointHit);

    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "timestamp", source = "timestamp", qualifiedByName = "toLocalDateTime")
    EndpointHit toEntity(EndpointHitRequestDto endpointHitRequestDto);

    @Named("toLocalDateTime")
    default LocalDateTime toLocalDateTime(String timestamp) {
        return LocalDateTimeConverter.toLocalDateTime(timestamp);
    }
}

