package ru.practicum.ewmservice.dto;

import lombok.*;

/**
 * Для использования любого картографического сервиса в нашем API будет поле name
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeoDto {

    private String name; // Название и/или адрес места на карте

}