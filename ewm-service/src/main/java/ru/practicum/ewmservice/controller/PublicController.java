package ru.practicum.ewmservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.dto.*;
import ru.practicum.ewmservice.service.*;
import ru.practicum.ewmservice.tools.Const;
import ru.practicum.ewmservice.tools.ParamsForSearch;
import ru.practicum.statclient.StatClient;
import ru.practicum.statdto.StatDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Контроллер публичной части API, доступно без регистрации любому пользователю сети
 */
@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
public class PublicController {

    private final CategoryService categoryService;
    private final EventService eventService;
    private final CompilationService compilationService;
    private final PlaceLocationService placeLocationService;
    private final StatClient statClient;

    // Public: Категории - Публичный API для работы с категориями ------------------------------------------------------

    /**
     * В случае, если по заданным фильтрам не найдено ни одной категории, возвращает пустой список
     */
    @GetMapping("/categories")
    public List<CategoryDto> getAllCategories(@RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size) {
        log.info("Begin of 'GET /categories' All categories getting");

        return categoryService.getAllCategories(from, size);
    }

    /**
     * В случае, если категории с заданным id не найдено, возвращает статус код 404
     */
    @GetMapping("/categories/{id}")
    public CategoryDto getCategoryById(@PathVariable Long id) {
        log.info("Begin of 'GET /categories/id' category getting by id={}", id);

        return categoryService.getCategoryById(id);
    }

    // Public: События. Публичный API для работы с событиями -----------------------------------------------------------

    /**
     * Получение событий с возможностью фильтрации
     * <p>
     * - это публичный эндпоинт, соответственно в выдаче должны быть только опубликованные события
     * - текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв
     * - если в запросе не указан диапазон дат [rangeStart-rangeEnd], то нужно выгружать события,
     * которые произойдут позже текущей даты и времени
     * - информация о каждом событии должна включать в себя количество просмотров и количество уже
     * одобренных заявок на участие
     * - информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить
     * в сервисе статистики
     * В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список
     */
    @GetMapping("/events")
    public List<EventFullDto> getSelectedEventsForPublic(@RequestParam(required = false) String text,
                                                         @RequestParam(required = false) List<Long> categories,
                                                         @RequestParam(required = false) Boolean paid,

                                                         @RequestParam(required = false) Long locid,
                                                         @RequestParam(required = false) Float lat,
                                                         @RequestParam(required = false) Float lon,
                                                         @RequestParam(required = false) Float radius,

                                                         @RequestParam(required = false) String rangeStart,
                                                         @RequestParam(required = false) String rangeEnd,
                                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                         @RequestParam(required = false) String sort,
                                                         @RequestParam(defaultValue = "0") Long from,
                                                         @RequestParam(defaultValue = "10") Long size,
                                                         HttpServletRequest request) {

        ParamsForSearch params = ParamsForSearch.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .locid(locid)
                .lat(lat)
                .lon(lon)
                .radius(radius)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .from(from)
                .size(size)
                .build();

        log.info("Begin of 'GET /events' (Public API) all event by params: {}", params);

        // Для этого эндпоинта необходимо отправить статистику на сервер статистики через клиента
        StatDto statDto = new StatDto(Const.SERVICE_NAME_FOR_STAT, request.getRequestURI(),
                request.getRemoteAddr(), LocalDateTime.now());
        statClient.saveStat(statDto);

        return eventService.getSelectedEventsForPublic(params);
    }

    /**
     * Получение подробной информации об опубликованном событии по его идентификатору
     * <p>
     * - событие должно быть опубликовано
     * - информация о событии должна включать в себя количество просмотров и количество подтвержденных запросов
     * - информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
     * В случае, если события с заданным id не найдено, возвращает статус код 404.
     */
    @GetMapping("/events/{id}")
    public EventFullDto getEventByIdForPublicApi(@PathVariable Long id, HttpServletRequest request) {
        log.info("Begin of 'GET /events/{id}' (Public API) Event, eventId={}", id);

        // Для этого эндпоинта необходимо отправить статистику на сервер статистики через клиента
        StatDto statDto = new StatDto(Const.SERVICE_NAME_FOR_STAT, request.getRequestURI(),
                request.getRemoteAddr(), LocalDateTime.now());
        statClient.saveStat(statDto);

        return eventService.getEventByIdForPublicApi(id);
    }

    // Public: Подборки событий. Публичный API для работы с подборками событий -----------------------------------------

    /**
     * Получение подборок событий (Public API)
     * В случае, если по заданным фильтрам не найдено ни одной подборки, возвращает пустой список
     */
    @GetMapping("/compilations")
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        log.info("Begin of 'GET /compilations' (Public API) all compilations by params, pinned={}", pinned);
        return compilationService.getCompilations(pinned, from, size);
    }

    /**
     * Получение подборки событий по его id (Public API)
     * В случае, если подборки с заданным id не найдено, возвращает статус код 404
     */
    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        log.info("Begin of 'GET /compilations/{compId}' (Public API) Compilations by id, compId={}", compId);
        return compilationService.getCompilationById(compId);
    }

    // Public: Локации. Публичный API для работы с локациями -----------------------------------------------------------

    /**
     * Получение локации по id (Public API)
     */
    @GetMapping("/location/{locId}")
    public PlaceLocationDto getLocationById(@PathVariable Long locId) {
        log.info("Begin of 'GET /location/{locId}' (Public API) Location by id. locId={}", locId);
        return placeLocationService.getLocationById(locId);
    }

    /**
     * Получение списка всех локаций с пагинацией (Public API)
     */
    @GetMapping("/location")
    public List<PlaceLocationDto> getAllLocations(@RequestParam(defaultValue = "0") int from,
                                                  @RequestParam(defaultValue = "10") int size) {
        log.info("Begin of 'GET /location' (Public API) all location");
        return placeLocationService.getAllLocations(from, size);
    }


}
