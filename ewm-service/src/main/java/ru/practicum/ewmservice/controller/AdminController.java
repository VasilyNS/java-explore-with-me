package ru.practicum.ewmservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.dto.*;
import ru.practicum.ewmservice.service.*;

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

    // API для работы с пользователями --------------------------------------------------------

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
                                  @RequestParam(defaultValue = "0") Long from,
                                  @RequestParam(defaultValue = "10") Long size) {
        log.info("Begin of 'GET /admin/users' Users getting, ids={}", ids);
        return userService.getUsers(ids, from, size);
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void delUser(@PathVariable Long id) {
        log.info("Begin of 'DELETE /admin/users' User deleting, id={}", id);
        userService.deleteUser(id);
    }

    // API для работы с категориями --------------------------------------------------------

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

    // API для работы с ... --------------------------------------------------------


}
