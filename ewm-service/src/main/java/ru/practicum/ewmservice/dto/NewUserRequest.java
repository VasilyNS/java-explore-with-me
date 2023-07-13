package ru.practicum.ewmservice.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

/**
 * Данные нового пользователя
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {

    @NotBlank
    @Length(min = 2, max = 250)
    private String name;            // Имя

    @NotBlank
    @Length(min = 6, max = 254)
    @Email
    private String email;           // Почтовый адрес

}
