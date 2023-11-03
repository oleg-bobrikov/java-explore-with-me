package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.LocationDto;
import ru.practicum.ewm.dto.NewLocationDto;
import ru.practicum.ewm.model.Location;
import ru.practicum.ewm.model.LocationType;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    LocationDto toDto(Location location);

    List<LocationDto> toDto(List<Location> location);
    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "lat", source = "locationDto.lat")
    @Mapping(target = "lon", source = "locationDto.lon")
    @Mapping(target = "radiusInMeters", source = "locationDto.radiusInMeters")
    @Mapping(target = "type", source = "locationType")
    Location toModel(NewLocationDto locationDto, LocationType locationType);

    @Mapping(target = "type", source = "location.type.id")
    NewLocationDto toNewDto(Location location);
}
