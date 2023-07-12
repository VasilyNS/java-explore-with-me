package ru.practicum.ewmservice.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.dto.*;
import ru.practicum.ewmservice.enums.*;
import ru.practicum.ewmservice.model.*;
import ru.practicum.ewmservice.service.*;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RequestMapper {

    private final UserService userService;
    private final CategoryService categoryService;
    private final EventService eventService;

    /**
     * Для создания нового запроса от текущего пользователя на участие в событии
     */
    public Request toRequest(Long userId, Long eventId) {
        Request request = new Request();
        request.setId(0L);
        request.setCreated(LocalDateTime.now());
        request.setEvent(eventService.checkExistAndGetEvent(eventId));
        request.setRequester(userService.checkExistAndGetUser(userId));
        request.setStatus(Status.PENDING);
        return request;
    }

    /**
     * Запрос -> DTO
     */
    public ParticipationRequestDto toParticipationRequestDtoFromRequest(Request request) {
        ParticipationRequestDto prDto = new ParticipationRequestDto();
        prDto.setId(request.getId());
        prDto.setCreated(request.getCreated());
        prDto.setEvent(request.getEvent().getId());
        prDto.setRequester(request.getRequester().getId());
        prDto.setStatus(request.getStatus().toString());
        return prDto;
    }

}
