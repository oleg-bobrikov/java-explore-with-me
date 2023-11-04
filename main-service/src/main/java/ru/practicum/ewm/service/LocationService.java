package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.*;

import java.util.List;

public interface LocationService {
    LocationTypeDto adminAddLocationType(NewLocationTypeDto newLocationTypeDto);

    LocationTypeDto adminUpdateLocationType(long id, NewLocationTypeDto typeDto);

    void adminRemoveLocationType(long id);

    LocationDto adminUpdateLocation(long id, UpdateLocationDto patch);

    List<LocationDto> findLocations(LocationFilterDto filter);

    LocationDto getLocation(long id);

    LocationTypeDto adminGetLocationType(long id);

    List<LocationTypeDto> adminFindLocationTypes(String text, int from, int size);
}
