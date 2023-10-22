package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.projection.EventShortProjection;

@Mapper(uses = {CategoryMapper.class, UserMapper.class, LocationMapper.class}, componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "category", source = "categoryModel")
    @Mapping(target = "state", source = "eventDto", qualifiedByName = "toState")
    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "createdOn", expression = "java(null)")
    @Mapping(target = "publishedOn", expression = "java(null)")
    Event toModel(NewEventDto eventDto, Category categoryModel, User initiator);


    @Mapping(target = "confirmedRequests", expression = "java(0)")
    @Mapping(target = "views", expression = "java(0L)")
    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "confirmedRequests", expression = "java(0)")
    @Mapping(target = "views", expression = "java(0L)")
    EventShortDto toEventShortDto(EventShortProjection event);

    @Named("toState")
    default EventState toState(NewEventDto newEventDto) {
        return newEventDto.isRequestModeration() ? EventState.PENDING : EventState.PUBLISHED;
    }

}
