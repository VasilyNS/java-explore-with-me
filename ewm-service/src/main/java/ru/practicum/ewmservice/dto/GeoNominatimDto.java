package ru.practicum.ewmservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

/**
 * Для использования сервиса nominatim.openstreetmap.org
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // Не читаем ненужные поля из JSON
public class GeoNominatimDto {

    private String display_name; // Название места, как возвращает в JSON сторонний сервис

}