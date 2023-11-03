package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.LocationDto;
import ru.practicum.ewm.dto.NewLocationDto;
import ru.practicum.ewm.model.Location;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    LocationDto toDto(Location location);

    List<LocationDto> toDto(List<Location> location);

    @Mapping(target = "id", expression = "java(null)")
    Location toModel(NewLocationDto locationDto);
}
