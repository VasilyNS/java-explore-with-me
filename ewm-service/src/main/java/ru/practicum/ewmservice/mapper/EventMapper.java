package ru.practicum.ewmservice.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.dto.*;
import ru.practicum.ewmservice.enums.State;
import ru.practicum.ewmservice.model.*;
import ru.practicum.ewmservice.service.CategoryService;
import ru.practicum.ewmservice.service.UserService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventMapper {

    private final UserService userService;
    private final CategoryService categoryService;

    public Event toEventFormNewEventDto(NewEventDto newEventDto, Long userId) { // Только для создания нового события
        Event event = new Event();
        event.setId(0L);
        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(categoryService.checkExistAndGetCategory(newEventDto.getCategory())); // класс Category
        event.setDescription(newEventDto.getDescription());
        event.setConfirmedRequests(0); // Инициализация при создании нового объект
        event.setCreatedOn(LocalDateTime.now()); // Инициализация при создании нового объект
        event.setEventDate(newEventDto.getEventDate());
        event.setInitiator(userService.checkExistAndGetUser(userId)); // класс User
        event.setLon(newEventDto.getLocation().getLon());
        event.setLat(newEventDto.getLocation().getLat());
        event.setPaid(newEventDto.getPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.getRequestModeration());
        event.setState(State.PENDING);
        event.setTitle(newEventDto.getTitle());
        //event.set(newEventDto.get());
        //event.set(newEventDto.get());
        return event;
    }

    public EventFullDto toEventFullDtoDtoFromEvent(Event event) { // При получении события в web API в полном DTO
        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setId(event.getId());
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategory(categoryService.getCategoryById(event.getCategory().getId())); // класс CategoryDto
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setConfirmedRequests(event.getConfirmedRequests());    // TODO: Хранится ли в базе???
        eventFullDto.setCreatedOn(event.getCreatedOn());
        eventFullDto.setEventDate(event.getEventDate());
        eventFullDto.setInitiator(UserMapper.toUserShortDto(  // UserShortDto
                userService.checkExistAndGetUser(             // User
                        event.getInitiator().getId())));      // id
        eventFullDto.setLocation(new Location(event.getLat(), event.getLon()));
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setState(event.getState());
        eventFullDto.setTitle(event.getTitle());
        //eventFullDto.set(event.get());
        //eventFullDto.set(event.get());
        return eventFullDto;
    }


}
