package ru.practicum.ewmservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.statclient.StatClient;
import ru.practicum.statdto.StatDto;
import ru.practicum.statdto.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Контроллер ДЛЯ ТЕСТОВ ))))
 */
@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
public class TESTController {

    private final StatClient statClient;

    // Public: Категории - Публичный API для работы с категориями ------------------------------------

    @GetMapping("/t")
    public String ttt(@RequestParam(defaultValue = "0") Long from,
                                              @RequestParam(defaultValue = "10") Long size,
                                              HttpServletRequest request) {
        log.info("Begin of 'GET /categories' All categories getting");

        log.info(">>>>> client ip: {}", request.getRemoteAddr());
        log.info(">>>>> endpoint path: {}", request.getRequestURI());

        //        int i = 5 / 0;

        // Отправляем статистику на сервер
        StatDto statDto = new StatDto("ewm-main-service", request.getRequestURI(),
                request.getRemoteAddr(), LocalDateTime.now());
        statClient.saveStat(statDto);

        String s = statDto.toString() + "<br> \n";

        statDto = new StatDto("qq-main-service", request.getRequestURI(),
                request.getRemoteAddr(), LocalDateTime.now());
        statClient.saveStat(statDto);

        s = s + statDto.toString() + "<br> \n<br> \n";

        // Читаем статистику
        LocalDateTime st = LocalDateTime.of(2020, 01, 01, 0, 0);
        LocalDateTime end = LocalDateTime.of(2030, 01, 01, 0, 0);
        // List<ViewStatsDto> viewStatsDtos = statClient.getStat(st, end, List.of("/events/1", "/events/555"), false);
        List<ViewStatsDto> viewStatsDtos = statClient.getStat(st, end, null, false);

        // Ручная проверка
        System.out.println("!!!!!!!!!!!!!!!!!!1");
        if (viewStatsDtos != null) {
            for (ViewStatsDto v : viewStatsDtos) {
                s = s + v.toString() + "<br> \n";
                System.out.println(v);
            }
        }
        System.out.println("!!!!!!!!!!!!!!!!!!1");

        return s;
    }

}
