package ru.practicum.ewmservice.dto;

import lombok.*;

import javax.validation.constraints.*;

/**
 * DTO для новой локации
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewPlaceLocationDto {

    @NotBlank
    @Size(min = 2, max = 127)
    private String name;                        // Имя локации

    @NotNull
    private Float lat;                          // Широта локации

    @NotNull
    private Float lon;                          // Долгота локации

    @NotNull
    private Float radius;                       // Радиус локации в километрах

}
