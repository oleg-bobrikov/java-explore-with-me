package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.mapper.LocationMapper;
import ru.practicum.ewm.mapper.LocationTypeMapper;
import ru.practicum.ewm.model.Location;
import ru.practicum.ewm.model.LocationType;
import ru.practicum.ewm.repository.LocationRepository;
import ru.practicum.ewm.repository.LocationTypeRepository;
import ru.practicum.ewm.service.LocationService;
import ru.practicum.ewm.util.PageRequestHelper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationTypeRepository locationTypeRepository;
    private final LocationTypeMapper locationTypeMapper;
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    @Override
    public LocationTypeDto adminAddLocationType(NewLocationTypeDto newLocationTypeDto) {
        return locationTypeMapper.toDto(locationTypeRepository.save(locationTypeMapper.toModel(newLocationTypeDto)));
    }

    @Override
    public LocationTypeDto adminUpdateLocationType(long id, NewLocationTypeDto typeDto) {
        LocationType locationType = locationTypeRepository.findLocationTypeById(id);
        if (locationType.getName().equals(typeDto.getName())) {
            return locationTypeMapper.toDto(locationType);
        }

        LocationType updatedLocationType = locationType.toBuilder().name(typeDto.getName()).build();
        return locationTypeMapper.toDto(locationTypeRepository.save(updatedLocationType));
    }

    @Override
    public void adminRemoveLocationType(long id) {
        locationTypeRepository.deleteById(id);
    }

    @Override
    public LocationDto adminUpdateLocation(long id, UpdateLocationDto patch) {
        Location originalLocation = locationRepository.findLocationById(id);
        Location updatedLocation = applyPatch(originalLocation, patch);
        Location savedLocation = locationRepository.save(updatedLocation);

        return locationMapper.toDto(savedLocation);
    }

    @Override
    public List<LocationDto> findLocations(LocationFilterDto filter) {
        PageRequest page = PageRequestHelper.of(filter.getFrom(), filter.getSize());
        List<Location> locations = locationRepository.findLocations(
                filter.getLatMin(),
                filter.getLatMax(),
                filter.getLonMin(),
                filter.getLonMax(),
                filter.getTypes(),
                page);
        return locationMapper.toDto(locations);
    }

    @Override
    public LocationDto getLocation(long id) {
        return locationMapper.toDto(locationRepository.findLocationById(id));
    }

    @Override
    public LocationTypeDto adminGetLocationType(long id) {
        return locationTypeMapper.toDto(locationTypeRepository.findLocationTypeById(id));
    }

    @Override
    public List<LocationTypeDto> adminFindLocationTypes(String text, int from, int size) {
        PageRequest page = PageRequestHelper.of(from, size);
        return locationTypeMapper.toDto(locationTypeRepository.findLocationTypesByText(text, page));
    }

    private Location applyPatch(Location location, UpdateLocationDto patch) {
        Location.LocationBuilder locationBuilder = location.toBuilder();

        if (patch.getLat() != null) {
            locationBuilder.lat(patch.getLat());
        }

        if (patch.getLon() != null) {
            locationBuilder.lon(patch.getLon());
        }

        if (patch.getType() == null) {
            locationBuilder.type(null);
        }

        if (patch.getType() != null) {
            locationBuilder.type(locationTypeRepository.findLocationTypeById(patch.getType()));
        }

        if (patch.getRadiusInMeters() != null) {
            locationBuilder.radiusInMeters(patch.getRadiusInMeters());
        }
        return locationBuilder.build();
    }
}
