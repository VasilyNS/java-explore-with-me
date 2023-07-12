package ru.practicum.ewmservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * Подборка событий
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {

    private List<Long> events = new ArrayList<>();  // Список идентификаторов событий входящих в подборку

    private Boolean pinned = false;                 // Закреплена ли подборка на главной странице сайта

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;                           // Заголовок подборки

}
