package ru.practicum.statserver;

import lombok.experimental.UtilityClass;
import ru.practicum.statdto.StatDto;

@UtilityClass
public class StatMapper {

    public static StatDto toStatDto(Stat stat) {
        return new StatDto(
                stat.getApp(),
                stat.getUri(),
                stat.getIp(),
                stat.getTimestamp()
        );
    }

    public static Stat toStat(StatDto statDto) {
        return new Stat(
                0L,
                statDto.getApp(),
                statDto.getUri(),
                statDto.getIp(),
                statDto.getTimestamp()
        );
    }

}
