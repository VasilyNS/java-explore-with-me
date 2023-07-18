package ru.practicum.ewmservice.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.dto.*;
import ru.practicum.ewmservice.model.*;

@Service
@RequiredArgsConstructor
public class PlaceLocationMapper {

    /**
     * Для создания новой локации, DTO -> Сущность
     */
    public PlaceLocation toPlaceLocationFromNewDto(NewPlaceLocationDto dto) {
        PlaceLocation pl = new PlaceLocation();
        pl.setId(0L);
        pl.setName(dto.getName());
        pl.setLat(dto.getLat());
        pl.setLon(dto.getLon());
        pl.setRadius(dto.getRadius());
        return pl;
    }

    /**
     * Сущность -> DTO
     */
    public PlaceLocationDto toPlaceLocationDto(PlaceLocation pl) {
        PlaceLocationDto dto = new PlaceLocationDto();
        dto.setId(pl.getId());
        dto.setName(pl.getName());
        dto.setLat(pl.getLat());
        dto.setLon(pl.getLon());
        dto.setRadius(pl.getRadius());
        return dto;
    }

    /**
     * Для апдейта локации
     */
    public PlaceLocation toPlaceLocationFromUpdateDto(UpdatePlaceLocationDto upd, PlaceLocation pl) {
        // Традиционно заменяем поля только если они не null
        if (upd.getName() != null) {
            pl.setName(upd.getName());
        }
        if (upd.getLat() != null) {
            pl.setLat(upd.getLat());
        }
        if (upd.getLon() != null) {
            pl.setLon(upd.getLon());
        }
        if (upd.getRadius() != null) {
            pl.setRadius(upd.getRadius());
        }
        return pl;
    }

}
