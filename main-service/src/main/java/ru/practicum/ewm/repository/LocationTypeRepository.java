package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.LocationType;

public interface LocationTypeRepository extends JpaRepository<LocationType, Long> {
    default LocationType findLocationTypeById(long id) {
        return findById(id).orElseThrow(
                () -> new NotFoundException(
                        String.format("No location type found by ID %s", id)));
    }
}
