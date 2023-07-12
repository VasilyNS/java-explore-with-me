package ru.practicum.ewmservice.dto;

import lombok.*;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * Изменение информации о подборке событий. Если поле в запросе не указано
 * (равно null) - значит изменение этих данных не требуется.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationRequest {

    private List<Long> events;  // Список id событий подборки для полной замены текущего списка

    private Boolean pinned;     // Закреплена ли подборка на главной странице сайта

    @Size(min = 1, max = 50)
    private String title;       // Заголовок подборки

}
