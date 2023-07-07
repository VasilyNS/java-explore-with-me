package ru.practicum.ewmservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * Подборка событий
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {

  private List<Long> events;  // Список идентификаторов событий входящих в подборку

  private Boolean pinned;     // Закреплена ли подборка на главной странице сайта

  @Size(min=1,max=50)
  private String title;       // Заголовок подборки

}
