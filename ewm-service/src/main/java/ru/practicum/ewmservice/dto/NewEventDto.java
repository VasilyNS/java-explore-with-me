package ru.practicum.ewmservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewmservice.enums.StateAction;
import ru.practicum.ewmservice.tools.Const;
import ru.practicum.ewmservice.tools.annotation.FutureWithinTwoHours;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Новое событие
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;          // Краткое описание события

    @NotNull
    private Long category;              // id категории к которой относится событие

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;         // Полное описание события

    @NotNull
    @FutureWithinTwoHours
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Const.DT_PATTERN)
    private LocalDateTime eventDate;    // Дата и время планируемого события

    @NotNull
    private Location location;          // Широта и долгота места проведения события

    private Boolean paid;               // Нужно ли оплачивать участие в событии, default: false

    private Integer participantLimit;   // Ограничение на количество участников. def. 0 - отсутствие ограничения

    // Нужна ли пре-модерация заявок на участие. Если true, то все заявки будут ожидать
    // подтверждения инициатором события./ Если false - то будут подтверждаться автоматически.
    private Boolean requestModeration;

    private StateAction stateAction;    // Состояние события. TODO: ОНО ИСПОЛЬЗУЕТСЯ ДЛЯ НОВЫХ????????????????????????????

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;               // Заголовок события

}
