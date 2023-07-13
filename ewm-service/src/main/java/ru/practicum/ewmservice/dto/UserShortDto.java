package ru.practicum.ewmservice.dto;

import lombok.*;

/**
 * Пользователь (краткая информация)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserShortDto {

    private Long id;        // Идентификатор
    private String name;    // Имя

}
