package ru.practicum.ewmservice.dto;

import lombok.*;

import javax.validation.constraints.Size;

/**
 * Данные для добавления новой категории
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

  private Long id;           // Идентификатор категории

  @Size(min=1,max=50)
  private String name;       // Название категории

}
