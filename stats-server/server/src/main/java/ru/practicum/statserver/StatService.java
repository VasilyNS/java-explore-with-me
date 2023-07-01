package ru.practicum.statserver;

import ru.practicum.statdto.StatDto;
import ru.practicum.statdto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {

    StatDto saveStat(StatDto statDto);

    List<ViewStatsDto> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

}
