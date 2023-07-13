package ru.practicum.ewmservice.dto;

import lombok.*;

/**
 * Широта и долгота места проведения события
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {

    private Float lat;      // Широта
    private Float lon;      // Долгота

}
