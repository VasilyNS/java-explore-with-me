package ru.practicum.ewmservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewmservice.tools.Const;

import java.time.LocalDateTime;

/**
 * Заявка на участие в событии
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequestDto {

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Const.DT_PATTERN)
  private LocalDateTime created;    // Дата и время создания заявки

  private Long event;               // Идентификатор события

  private Long id;                  // Идентификатор заявки

  private Long requester;           // Идентификатор пользователя, отправившего заявку

  private String status;            // Статус заявки

}
