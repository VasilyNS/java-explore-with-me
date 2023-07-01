package ru.practicum.statserver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statdto.StatDto;
import ru.practicum.statdto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatController {

    private final StatService statService;

    @PostMapping(path = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public StatDto saveStat(@RequestBody StatDto statDto) {
        log.info("Begin of Stat save (/hit): {}", statDto.toString());
        return statService.saveStat(statDto);
    }

    /**
     * Вид запроса:
     * /stats?start=2020-05-05%2000:00:00&end=2035-05-05%2000:00:00&uris=/events/1&uris=/events/2&unique=true
     */
    @GetMapping("/stats")
    public List<ViewStatsDto> getStat(@RequestParam(required = true)
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                      @RequestParam(required = true)
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                      @RequestParam(required = false) List<String> uris,
                                      @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Begin of Stat getting (/stats) for start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return statService.getStat(start, end, uris, unique);
    }

}
