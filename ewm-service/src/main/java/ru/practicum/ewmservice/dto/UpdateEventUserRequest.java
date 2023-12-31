package ru.practicum.ewmservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewmservice.enums.StateAction;
import ru.practicum.ewmservice.tools.Const;
import ru.practicum.ewmservice.tools.annotation.FutureWithinTwoHours;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Данные для изменения информации о событии. Если поле в запросе не указано
 * (равно null) - значит изменение этих данных не требуется.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventUserRequest {

    @Size(min = 20, max = 2000)
    private String annotation;          // Аннотация
    private Long category;              // Категория
    @Size(min = 20, max = 7000)
    private String description;         // Описание
    @FutureWithinTwoHours
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Const.DT_PATTERN)
    private LocalDateTime eventDate;    // Дата и время события в yyyy-MM-dd HH:mm:ss
    private Location location;          // Широта и долгота места проведения события
    private Boolean paid;               // Флаг о платности мероприятия
    private Integer participantLimit;   // Лимит пользователей
    private Boolean requestModeration;  // Нужна ли пре-модерация заявок на участие
    private StateAction stateAction;    // Состояние события
    @Size(min = 3, max = 120)
    private String title;               // Заголовок

}
