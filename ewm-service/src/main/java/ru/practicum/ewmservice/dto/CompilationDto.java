package ru.practicum.ewmservice.dto;

import lombok.*;

import java.util.List;

/**
 * Подборка событий DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {

    private Long id;                    // Идентификатор

    private List<EventShortDto> events; // Список событий входящих в подборку

    private Boolean pinned;             // Закреплена ли подборка на главной странице сайта

    private String title;               // Заголовок подборки

}
