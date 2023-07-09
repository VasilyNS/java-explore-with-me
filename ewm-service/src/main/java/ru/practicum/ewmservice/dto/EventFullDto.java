package ru.practicum.ewmservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewmservice.enums.*;
import ru.practicum.ewmservice.tools.Const;

import java.time.LocalDateTime;

/**
 * Событие - Полное DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {

    private Long id;                    // Идентификатор

    private String annotation;          // Краткое описание события

    private CategoryDto category;       // Категория к которой относится событие

    private String description;         // Полное описание события

    private Integer confirmedRequests;  // Количество одобренных заявок на участие в данном событии

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Const.DT_PATTERN)
    private LocalDateTime createdOn;    // Дата и время создания события

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Const.DT_PATTERN)
    private LocalDateTime eventDate;    // Дата и время планируемого события

    private UserShortDto initiator;     // Инициатор

    private Location location;          // Широта и долгота места проведения события

    private Boolean paid;               // Нужно ли оплачивать участие в событии, default: false

    private Integer participantLimit;   // Ограничение на количество участников. default: 0 - отсутствие ограничения

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Const.DT_PATTERN)
    private LocalDateTime publishedOn;  // Дата и время публикации события

    private Boolean requestModeration;  // Нужна ли пре-модерация заявок на участие.
    // default: true, то все заявки будут ожидать подтверждения инициатором события.
    // Если false - то будут подтверждаться автоматически.

    private State state;                // Список состояний жизненного цикла события Enum: PENDING, PUBLISHED, CANCELED

    private String title;               // Заголовок события

    private Long views;                 // Количество просмотрев события

}

/*
Пример:
{
  "annotation": "Эксклюзивность нашего шоу гарантирует привлечение максимальной зрительской аудитории",
  "category": {
    "id": 1,
    "name": "Концерты"
  },
  "confirmedRequests": 5,
  "createdOn": "2022-09-06 11:00:23",
  "description": "Что получится, если соединить кукурузу и полёт? Создатели \"Шоу летающей кукурузы\"
                  испытали эту идею на практике и воплотили в жизнь инновационный проект, предлагающий
                  свежий взгляд на развлечения...",
  "eventDate": "2024-12-31 15:10:05",
  "id": 1,
  "initiator": {
    "id": 3,
    "name": "Фёдоров Матвей"
  },
  "location": {
    "lat": 55.754167,
    "lon": 37.62
  },
  "paid": true,
  "participantLimit": 10,
  "publishedOn": "2022-09-06 15:10:05",
  "requestModeration": true,
  "state": "PUBLISHED",
  "title": "Знаменитое шоу 'Летающая кукуруза'",
  "views": 999
}
 */