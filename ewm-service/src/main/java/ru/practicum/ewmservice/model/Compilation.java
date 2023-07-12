package ru.practicum.ewmservice.model;

import lombok.*;
import ru.practicum.ewmservice.dto.EventShortDto;

import javax.persistence.*;
import java.util.List;

/**
 * Подборка событий
 */
@Entity
@Table(name = "compilation", schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // Идентификатор

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "compilation_event",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private List<Event> events;         // Список событий входящих в подборку

    @Column(name = "pinned")
    private Boolean pinned;             // Закреплена ли подборка на главной странице сайта

    @Column(name = "title")
    private String title;               // Заголовок подборки

}
