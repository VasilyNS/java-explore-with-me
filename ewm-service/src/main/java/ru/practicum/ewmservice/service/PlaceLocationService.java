package ru.practicum.ewmservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.dto.*;
import ru.practicum.ewmservice.mapper.*;
import ru.practicum.ewmservice.model.*;
import ru.practicum.ewmservice.repository.*;
import ru.practicum.ewmservice.tools.exception.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaceLocationService {

    private final PlaceLocationRepository placeLocationRepository;
    private final PlaceLocationMapper placeLocationMapper;

    /**
     * Добавление новой локации (Admin API)
     */
    @Transactional
    public PlaceLocationDto saveLocation(NewPlaceLocationDto newPlaceLocationDto) {
        PlaceLocation pl = placeLocationMapper.toPlaceLocationFromNewDto(newPlaceLocationDto);
        PlaceLocation plForReturn = placeLocationRepository.save(pl);
        return placeLocationMapper.toPlaceLocationDto(plForReturn);
    }

    /**
     * Обновление информации о локации (Admin API)
     */
    @Transactional
    public PlaceLocationDto updateLocation(Long locId, UpdatePlaceLocationDto updatePlaceLocationDto) {
        PlaceLocation pl = checkExistAndGetPlaceLocation(locId);
        pl = placeLocationMapper.toPlaceLocationFromUpdateDto(updatePlaceLocationDto, pl);

        PlaceLocation plForReturn = placeLocationRepository.save(pl);
        return placeLocationMapper.toPlaceLocationDto(plForReturn);
    }

    /**
     * Удаление локации (Admin API)
     */
    @Transactional
    public void delLocation(Long locId) {
        placeLocationRepository.deleteById(locId);
    }

    /**
     * Получение локации по id (Public API)
     */
    @Transactional(readOnly = true)
    public PlaceLocationDto getLocationById(Long locId) {
        return placeLocationMapper.toPlaceLocationDto(checkExistAndGetPlaceLocation(locId));
    }

    /**
     * Получение списка всех локаций с пагинацией (Public API)
     */
    @Transactional(readOnly = true)
    public List<PlaceLocationDto> getAllLocations(Long from, Long size) {
        int pageNum = (int) (from / size);
        Pageable pageable = PageRequest.of(pageNum, Math.toIntExact(size));
        List<PlaceLocation> pl = placeLocationRepository.getAllLocations(pageable);
        return pl.stream().map(placeLocationMapper::toPlaceLocationDto)
                .collect(Collectors.toList());
    }

    /**
     * Проверка, что сущность есть в БД, если нет - исключение, если да - возврат объекта с ней
     */
    @Transactional(readOnly = true)
    public PlaceLocation checkExistAndGetPlaceLocation(Long id) {
        return placeLocationRepository.findById(id).orElseThrow(() -> new NotFoundException("PlaceLocation " + id));
    }

}
