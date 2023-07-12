package ru.practicum.ewmservice.model;

import lombok.*;
import ru.practicum.ewmservice.enums.Status;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Заявка на участие в событии
 */
@Entity
@Table(name = "request", schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Request {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;                  // Идентификатор заявки

  @Column(name = "created")
  private LocalDateTime created;    // Дата и время создания заявки

  @ManyToOne
  @JoinColumn(name = "event_id")
  @ToString.Exclude
  private Event event;               // Идентификатор события

  @ManyToOne
  @JoinColumn(name = "requester_id")
  @ToString.Exclude
  private User requester;           // Идентификатор пользователя, отправившего заявку

  @Column(name = "status")
  private Status status;            // Статус заявки

}
