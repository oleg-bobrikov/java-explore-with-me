package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;

@Mapper(uses = {CategoryMapper.class, UserMapper.class, LocationMapper.class}, componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "category", source = "categoryModel")
    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "createdOn", expression = "java(null)")
    @Mapping(target = "publishedOn", expression = "java(null)")
    @Mapping(target = "state", expression = "java(Event.State.PENDING)")
    Event toModel(NewEventDto eventDto, Category categoryModel, User initiator);

    EventShortDto toEventShortDto(Event event, long confirmedRequests, long views);

    EventFullDto toEventFullDto(Event event, long confirmedRequests, long views);
}
