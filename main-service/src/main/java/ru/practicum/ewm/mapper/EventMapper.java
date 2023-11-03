package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.model.*;

@Mapper(uses = {CategoryMapper.class,
        UserMapper.class,
        LocationMapper.class}, componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", expression = "java(Event.State.PENDING)")
    Event toModel(EventDto eventDto);

    EventShortDto toEventShortDto(Event event, long confirmedRequests, long views);

    EventFullDto toEventFullDto(Event event, long confirmedRequests, long views);

    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "location", source = "location")
    EventDto toEventDto(NewEventDto newEventDto, User initiator, Category category, Location location);
}
