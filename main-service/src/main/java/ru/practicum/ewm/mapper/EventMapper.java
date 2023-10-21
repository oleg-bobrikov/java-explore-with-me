package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.LocationDto;
import ru.practicum.ewm.dto.NewEventDto;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.model.User;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "lat", source = "eventDto", qualifiedByName = "toLat")
    @Mapping(target = "lon", source = "eventDto", qualifiedByName = "toLon")
    @Mapping(target = "category", source = "newCategory")
    @Mapping(target = "state", source = "eventDto", qualifiedByName = "toState")
    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "createdOn", expression = "java(null)")
    @Mapping(target = "publishedOn", expression = "java(null)")
    Event toModel(NewEventDto eventDto, Category newCategory, User initiator);

    @Mapping(target = "location", source = "event", qualifiedByName = "toLocationDto")
    @Mapping(target = "confirmedRequests", expression = "java(0)")
    @Mapping(target = "views", expression = "java(0L)")
    EventFullDto toEventFullDto(Event event);

    @Named("toState")
    default EventState toState(NewEventDto newEventDto) {
        return newEventDto.isRequestModeration() ? EventState.PENDING : EventState.PUBLISHED;
    }

    @Named("toLat")
    default float toLat(NewEventDto newEventDto) {
        return newEventDto.getLocation().getLat();
    }

    @Named("toLon")
    default float toLon(NewEventDto newEventDto) {
        return newEventDto.getLocation().getLon();
    }

    @Named("toLocationDto")
    default LocationDto toLocationDto(Event event) {
        return LocationDto.builder().lat(event.getLat()).lon(event.getLon()).build();
    }
}
