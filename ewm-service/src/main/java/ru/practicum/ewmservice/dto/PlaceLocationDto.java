package ru.practicum.ewmservice.dto;

import lombok.*;

/**
 * DTO локации
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceLocationDto {

    private Long id;        // Идентификатор
    private String name;    // Имя локации
    private Float lat;      // Широта локации
    private Float lon;      // Долгота локации
    private Float radius;   // Радиус локации в километрах

}
