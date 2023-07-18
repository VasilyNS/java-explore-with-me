package ru.practicum.ewmservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.dto.*;
import ru.practicum.ewmservice.service.*;
import ru.practicum.ewmservice.tools.ParamsForSearch;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * Контроллер административной части API
 * Административная часть начинается с /admin
 */
@Slf4j
@RestController
@Validated
@RequestMapping(path = "/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final CategoryService categoryService;
    private final EventService eventService;
    private final CompilationService compilationService;
    private final PlaceLocationService placeLocationService;

    // Admin: Пользователи. API для работы с пользователями ------------------------------------------------------------

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED) // 201
    public UserDto saveUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.info("Begin of 'POST /admin/users' User creation for: {}", newUserRequest.toString());
        return userService.saveUser(newUserRequest);
    }

    /**
     * Возвращает информацию обо всех пользователях (учитываются параметры ограничения выборки),
     * либо о конкретных (учитываются указанные идентификаторы)
     * В случае, если по заданным фильтрам не найдено ни одного пользователя, возвращает пустой список
     */
    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @RequestParam(defaultValue = "0") int from,
                                  @RequestParam(defaultValue = "10") int size) {
        log.info("Begin of 'GET /admin/users' Users getting, ids={}", ids);
        return userService.getUsers(ids, from, size);
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void delUser(@PathVariable Long id) {
        log.info("Begin of 'DELETE /admin/users' User deleting, id={}", id);
        userService.deleteUser(id);
    }

    // Admin: Категории. API для работы с категориями ------------------------------------------------------------------

    /**
     * Добавление новой категории, имя категории должно быть уникальным
     */
    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED) // 201
    public CategoryDto saveCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Begin of 'POST /admin/categories' Category creation for: {}", newCategoryDto.toString());
        return categoryService.saveCategory(newCategoryDto);
    }

    @DeleteMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void delCategory(@PathVariable Long id) {
        log.info("Begin of 'DELETE /admin/categories' Category, id={}", id);
        categoryService.delCategory(id);
    }

    @PatchMapping("/categories/{id}")
    public CategoryDto updateCategory(@PathVariable Long id,
                                      @Valid @RequestBody CategoryDto categoryDto) {
        log.info("Begin of 'PATCH /admin/categories' Category updating, id={}, {}", id, categoryDto);
        return categoryService.updateCategory(id, categoryDto);
    }

    // Admin: События. API для работы с событиями ----------------------------------------------------------------------

    /**
     * Поиск событий
     * <p>
     * Эндпоинт возвращает полную информацию обо всех событиях подходящих под переданные условия
     * В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список
     */
    @GetMapping("/events")
    public List<EventFullDto> getSelectedEventForAdmin(@RequestParam(required = false) List<Long> users,
                                                       @RequestParam(required = false) List<String> states,
                                                       @RequestParam(required = false) List<Long> categories,
                                                       @RequestParam(required = false) String rangeStart,
                                                       @RequestParam(required = false) String rangeEnd,
                                                       @RequestParam(defaultValue = "0") Long from,
                                                       @RequestParam(defaultValue = "10") Long size,
                                                       HttpServletRequest request) {

        ParamsForSearch params = ParamsForSearch.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();

        log.info("Begin of 'GET /admin/events' (Admin API) all event by params: {}", params);

        return eventService.getSelectedEventForAdmin(params);
    }

    /**
     * Редактирование данных события и его статуса (отклонение/публикация).
     * <p>
     * При редактировании данных любого события администратором валидация данных не требуется.
     * <p>
     * Дата начала изменяемого события должна быть не ранее чем за час от даты публикации. (Ожидается код ошибки 409)
     * Событие можно публиковать, только если оно в состоянии ожидания публикации (Ожидается код ошибки 409)
     * Событие можно отклонить, только если оно еще не опубликовано (Ожидается код ошибки 409)
     */
    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEventByAdmin(@Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest,
                                           @PathVariable Long eventId) {
        log.info("Begin of admin's 'PATCH /admin/events/{eventId}' Event eventId={}, new event={}",
                eventId, updateEventAdminRequest);
        return eventService.updateEventByAdmin(updateEventAdminRequest, eventId);
    }

    // Admin: Подборки событий. API для работы с подборками событий ----------------------------------------------------

    /**
     * Добавление новой подборки, подборка может не содержать событий (Admin API)
     */
    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED) // 201
    public CompilationDto saveCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Begin of 'POST /admin/compilations' Compilation creation for: {}", newCompilationDto.toString());
        return compilationService.saveCompilation(newCompilationDto);
    }

    /**
     * Удаление подборки (Admin API)
     */
    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void delCompilation(@PathVariable Long compId) {
        log.info("Begin of 'DELETE /admin/compilations/{compId}' Compilation deleting, id={}", compId);
        compilationService.delCompilation(compId);
    }

    /**
     * Обновление информации о подборке (Admin API)
     */
    @PatchMapping("/compilations/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        log.info("Begin of 'PATCH /admin/compilations/{compId}' Compilation updating, compId={}, {}",
                compId, updateCompilationRequest);
        return compilationService.updateCompilation(compId, updateCompilationRequest);
    }

    // Admin: Локации. API для работы с локациями ----------------------------------------------------

    /**
     * Добавление новой локации (Admin API)
     */
    @PostMapping("/location")
    @ResponseStatus(HttpStatus.CREATED) // 201
    public PlaceLocationDto saveLocation(@Valid @RequestBody NewPlaceLocationDto newPlaceLocationDto) {
        log.info("Begin of 'POST /admin/location' Location creation for: {}", newPlaceLocationDto.toString());
        return placeLocationService.saveLocation(newPlaceLocationDto);
    }

    /**
     * Обновление информации о локации (Admin API)
     */
    @PatchMapping("/location/{locId}")
    public PlaceLocationDto updateLocation(@PathVariable Long locId,
                                           @Valid @RequestBody UpdatePlaceLocationDto updatePlaceLocationDto) {
        log.info("Begin of 'PATCH /admin/location/{locId}' Location updating, locId={}, {}",
                locId, updatePlaceLocationDto);
        return placeLocationService.updateLocation(locId, updatePlaceLocationDto);
    }

    /**
     * Удаление локации (Admin API)
     */
    @DeleteMapping("/location/{locId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void delLocation(@PathVariable Long locId) {
        log.info("Begin of 'DELETE /admin/location/{locId}' Location deleting, locId={}", locId);
        placeLocationService.delLocation(locId);
    }

}
