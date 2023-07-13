package ru.practicum.ewmservice.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.dto.*;
import ru.practicum.ewmservice.model.*;
import ru.practicum.ewmservice.service.*;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationMapper {

    private final EventService eventService;
    private final EventMapper eventMapper;

    /**
     * Для создания новой подборки, DTO -> Сущность
     */
    public Compilation toCompilationFormNewCompilationDto(NewCompilationDto newCompilationDto) {
        Compilation compilation = new Compilation();
        compilation.setId(0L);
        compilation.setEvents(fillEvents(newCompilationDto.getEvents()));
        compilation.setPinned(newCompilationDto.getPinned());
        compilation.setTitle(newCompilationDto.getTitle());
        return compilation;
    }

    /**
     * Сущность -> DTO
     */
    public CompilationDto toCompilationDtoFormCompilation(Compilation compilation) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compilation.getId());

        List<EventShortDto> eventsDto = new ArrayList<>();
        for (Event event : compilation.getEvents()) {
            eventsDto.add(eventMapper.toEventShortDtoFromEvent(event));
        }
        compilationDto.setEvents(eventsDto);

        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setTitle(compilation.getTitle());

        return compilationDto;
    }

    /**
     * Для апдейта подборки (Admin API)
     */
    public Compilation toCompilationFromUpdateEventUserRequest(Compilation comp, UpdateCompilationRequest upd) {
        // Заменяем поля только если они не null
        comp.setEvents(fillEvents(upd.getEvents()));
        if (upd.getPinned() != null) {
            comp.setPinned(upd.getPinned());
        }
        if (upd.getTitle() != null) {
            comp.setTitle(upd.getTitle());
        }
        return comp;
    }

    /**
     * Заполнение поля events
     */
    private List<Event> fillEvents(List<Long> eventIds) {
        List<Event> result = new ArrayList<>();
        if (eventIds != null) {
            for (Long eventId : eventIds) {
                Event event = eventService.checkExistAndGetEvent(eventId);
                result.add(event);
            }
        }
        return result;
    }

}
