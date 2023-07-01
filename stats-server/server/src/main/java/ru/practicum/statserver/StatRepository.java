package ru.practicum.statserver;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.statdto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<Stat, Long> {

    @Query("SELECT new ru.practicum.statdto.ViewStatsDto(s.app, s.uri, COUNT(s.ip)) " +
            "FROM Stat s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC " )
    List<ViewStatsDto> getStatCountForAllIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.statdto.ViewStatsDto(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Stat s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC " )
    List<ViewStatsDto> getStatCountForUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.statdto.ViewStatsDto(s.app, s.uri, COUNT(s.ip)) " +
            "FROM Stat s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "AND s.uri IN :uris " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC " )
    List<ViewStatsDto> getStatCountForAllIpByUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.statdto.ViewStatsDto(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Stat s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "AND s.uri IN :uris " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC " )
    List<ViewStatsDto> getStatCountForUniqueIpByUris(LocalDateTime start, LocalDateTime end, List<String> uris);

}
