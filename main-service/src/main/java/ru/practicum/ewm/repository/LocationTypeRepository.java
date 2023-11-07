package ru.practicum.ewm.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.LocationType;

import java.util.List;

public interface LocationTypeRepository extends JpaRepository<LocationType, Long> {
    default LocationType findLocationTypeById(long id) {
        return findById(id).orElseThrow(
                () -> new NotFoundException(
                        String.format("No location type found by ID %s", id)));
    }

    @Query("SELECT type FROM LocationType type " +
            "WHERE :text IS NULL OR LOWER(type.name) LIKE LOWER(CONCAT('%', :text, '%'))")
    List<LocationType> findLocationTypesByText(@Param("text") String text, PageRequest page);
}
