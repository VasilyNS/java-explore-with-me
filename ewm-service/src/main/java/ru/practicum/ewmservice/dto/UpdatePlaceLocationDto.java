package ru.practicum.ewmservice.dto;

import lombok.*;

import javax.validation.constraints.*;

/**
 * DTO для обновления локации
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePlaceLocationDto {

    @Size(min = 2, max = 127)
    private String name;                        // Имя локации

    private Float lat;                          // Широта локации

    private Float lon;                          // Долгота локации

    private Float radius;                       // Радиус локации в километрах

}
