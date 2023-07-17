package ru.practicum.ewmservice.service;

import ru.practicum.ewmservice.dto.GeoDto;

public interface GeoService {

    /**
     * Получение названия и/или адреса места
     */
    GeoDto getPlaceName(Float lat, Float lon);

}
