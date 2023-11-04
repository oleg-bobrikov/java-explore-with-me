package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.LocationTypeDto;
import ru.practicum.ewm.dto.NewLocationTypeDto;
import ru.practicum.ewm.model.LocationType;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LocationTypeMapper {
    LocationTypeDto toDto(LocationType locationType);

    List<LocationTypeDto> toDto(List<LocationType> category);

    @Mapping(target = "id", ignore = true)
    LocationType toModel(NewLocationTypeDto locationTypeDto);
}
