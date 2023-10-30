package ru.practicum.ewm.stats.endpointhit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.stats.dto.ViewStatsResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {
    @Query(
            "select " +
                    "   new ru.practicum.ewm.stats.dto.ViewStatsResponseDto( " +
                    "       hit.app, " +
                    "       hit.uri,  " +
                    "       case when :unique = true " +
                    "           then count(distinct(hit.ip))" +
                    "           else count(hit.ip)  " +
                    "       end  " +
                    "            )" +
                    "from " +
                    "   EndpointHit hit  " +
                    "where " +
                    "   hit.timestamp between :start_date and :end_date " +
                    "   and (coalesce(:uris, null) is null or hit.uri in :uris)" +
                    "group by " +
                    "   hit.app," +
                    "   hit.uri " +
                    "order by " +
                    "   case when :unique = true " +
                    "       then count(distinct(hit.ip)) " +
                    "       else count(hit.ip) " +
                    "   end desc")
    List<ViewStatsResponseDto> getStats(@Param("start_date") LocalDateTime startDate,
                                        @Param("end_date") LocalDateTime endDate,
                                        @Param("uris") List<String> uris,
                                        @Param("unique") boolean unique);
}

