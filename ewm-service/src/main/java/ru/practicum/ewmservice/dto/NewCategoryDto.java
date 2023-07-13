package ru.practicum.ewmservice.dto;

import lombok.*;

import javax.validation.constraints.*;

/**
 * Данные для добавления новой категории
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCategoryDto {

    @NotBlank
    @Size(min = 1, max = 50)
    private String name;       // Название категории

}
