package ru.practicum.ewmservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewmservice.tools.Const;

import java.time.LocalDateTime;

/**
 * Краткая информация о событии
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {

    private Long id;                    // Идентификатор
    private String annotation;          // Краткое описание события
    private CategoryDto category;       // Категория к которой относится событие
    private Integer confirmedRequests;  // Количество одобренных заявок на участие в данном событии
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Const.DT_PATTERN)
    private LocalDateTime eventDate;    // Дата и время планируемого события
    private UserShortDto initiator;     // Инициатор
    private Boolean paid;               // Нужно ли оплачивать участие в событии, default: false
    private String title;               // Заголовок события
    private Long views;                 // Количество просмотрев события

}
