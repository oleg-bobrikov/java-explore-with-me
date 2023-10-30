package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.projection.EventShortProjection;

import java.util.List;

@Mapper(uses = {CategoryMapper.class, UserMapper.class, LocationMapper.class}, componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "category", source = "categoryModel")
    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "createdOn", expression = "java(null)")
    @Mapping(target = "publishedOn", expression = "java(null)")
    Event toModel(NewEventDto eventDto, Category categoryModel, User initiator);

    @Mapping(target = "confirmedRequests", expression = "java(0)")
    @Mapping(target = "views", expression = "java(0L)")
    EventFullDto toEventFullDto(Event event);

    List<EventFullDto> toEventFullDto(List<Event> events);

    @Mapping(target = "confirmedRequests", expression = "java(0)")
    @Mapping(target = "views", expression = "java(0L)")
    EventShortDto toEventShortDto(EventShortProjection event);
    EventShortDto toEventShortDto(Event event, long confirmedRequests, long views);
}
