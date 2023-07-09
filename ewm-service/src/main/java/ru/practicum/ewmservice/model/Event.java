package ru.practicum.ewmservice.model;

import lombok.*;
import ru.practicum.ewmservice.enums.State;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Событие
 */
@Entity
@Table(name = "event", schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "annotation")
    private String annotation;              // Краткое описание события

    @ManyToOne
    @JoinColumn(name = "category_id")       // Поле в таблице данной сущности
    @ToString.Exclude
    private Category category;              // Категория к которой относится событие

    @Column(name = "description")
    private String description;             // Полное описание события

    @Column(name = "confirmed_requests")
    private Integer confirmedRequests;      // Количество одобренных заявок на участие в данном событии

    @Column(name = "created_on")
    private LocalDateTime createdOn;        // Дата и время создания события

    @Column(name = "event_date")
    private LocalDateTime eventDate;        // Дата и время планируемого события

    @ManyToOne
    @JoinColumn(name = "initiator_id")      // Поле в таблице данной сущности
    @ToString.Exclude
    private User initiator;                 // Инициатор

    @Column(name = "lat")
    private Float lat;                      // Широта и долгота места проведения события

    @Column(name = "lon")
    private Float lon;                      // Широта и долгота места проведения события

    @Column(name = "paid")
    private Boolean paid;                   // Нужно ли оплачивать участие в событии, default: false

    @Column(name = "participant_limit")
    private Integer participantLimit;       // Ограничение на количество участников. default: 0 - отсутствие ограничения

    @Column(name = "published_on")
    private LocalDateTime publishedOn;      // Дата и время публикации события

    // Нужна ли пре-модерация заявок на участие. default: true, то все заявки будут ожидать
    // подтверждения инициатором события. Если false - то будут подтверждаться автоматически.
    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private State state;            // Список состояний жизненного цикла события Enum: PENDING, PUBLISHED, CANCELED

    @Column(name = "title")
    private String title;           // Заголовок события

}
