package ru.practicum.ewm.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Location;

import java.util.Collection;
import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {
    default Location findLocationById(long id) {
        return findById(id).orElseThrow(
                () -> new NotFoundException(
                        String.format("No location found by ID %s", id)));
    }

    @Query("SELECT " +
            "   location " +
            "FROM " +
            "   Location location " +
            "WHERE " +
            "   location.lat BETWEEN :latMin AND :latMax AND " +
            "   location.lon BETWEEN :lonMin AND :lonMax AND " +
            "   (:typeIds IS NULL OR location.type.id IN :typeIds)")
    List<Location> findLocations(@Param("latMin") float latMin,
                                 @Param("latMax") float latMax,
                                 @Param("lonMin") float lonMin,
                                 @Param("lonMax") float lonMax,
                                 @Param("typeIds") Collection<Long> typeIds,
                                 PageRequest page);
}
