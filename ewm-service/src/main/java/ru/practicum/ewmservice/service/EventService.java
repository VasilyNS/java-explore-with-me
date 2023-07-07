package ru.practicum.ewmservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.dto.*;
import ru.practicum.ewmservice.mapper.EventMapper;
import ru.practicum.ewmservice.model.Event;
import ru.practicum.ewmservice.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Transactional
    public EventFullDto saveEvent(NewEventDto newEventDto, Long userId) {
        Event event = eventMapper.toEventFormNewEventDto(newEventDto, userId);

        Event eventForReturn = eventRepository.save(event);
        return eventMapper.toEventFullDtoDtoFromEvent(eventForReturn);
    }

    @Transactional(readOnly = true)
    public EventFullDto getEventByIdForCurrentUser(Long userId, Long eventId) {

        return null;
    }


    /**
     * Проверка, что объект существует,
     * если нет - исключение, если да - возврат его самого
     */
//    public Category checkExistAndGetEvent(Long id) {
//        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category " + id));
//    }

}
