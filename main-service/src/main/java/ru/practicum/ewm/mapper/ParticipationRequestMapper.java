package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.model.ParticipationRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ParticipationRequestMapper {
    @Mapping(target = "event", source = "entity.event.id")
    @Mapping(target = "requester", source = "entity.requester.id")
    ParticipationRequestDto toDto(ParticipationRequest entity);

    List<ParticipationRequestDto> toDto(List<ParticipationRequest> entities);
}
