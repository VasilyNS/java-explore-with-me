package ru.practicum.ewmservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Для использования сервиса nominatim.openstreetmap.org
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // Не читаем ненужные поля из JSON
public class GeoNominatimDto {

    @JsonProperty("display_name")
    private String displayName; // Название места, как возвращает в JSON сторонний сервис

}