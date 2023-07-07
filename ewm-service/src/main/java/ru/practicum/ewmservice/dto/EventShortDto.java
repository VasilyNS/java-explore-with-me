package ru.practicum.ewmservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Краткая информация о событии
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {

  @Size(min=20,max=2000)
  private String annotation;          // Краткое описание события
  private CategoryDto category;       // Категория к которой относится событие
  @Size(min=20,max=7000)
  private Integer confirmedRequests;  // Количество одобренных заявок на участие в данном событии
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime eventDate;    // Дата и время планируемого события
  private Long id;                    // Идентификатор
  private UserShortDto initiator;     // Инициатор
  private Boolean paid;               // Нужно ли оплачивать участие в событии
                                      // default: false
  @Size(min=3,max=120)
  private String title;               // Заголовок события
  private Long views;                 // Количество просмотрев события

}
