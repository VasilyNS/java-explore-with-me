package ru.practicum.ewmservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.dto.CategoryDto;
import ru.practicum.ewmservice.service.CategoryService;
import ru.practicum.statclient.StatClient;

import javax.servlet.http.HttpServletRequest;
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

    // Public: Категории - Публичный API для работы с категориями ------------------------------------

    @GetMapping("/categories")
    public List<CategoryDto> getAllCategories(@RequestParam(defaultValue = "0") Long from,
                                              @RequestParam(defaultValue = "10") Long size) {
        log.info("Begin of 'GET /categories' All categories getting");

        return categoryService.getAllCategories(from, size);
    }

    @GetMapping("/categories/{id}")
    public CategoryDto getCategoryById(@PathVariable Long id) {
        log.info("Begin of 'GET /categories/id' category getting by id={}", id);

        return categoryService.getCategoryById(id);
    }

    // Public: ... ------------------------------------




}
