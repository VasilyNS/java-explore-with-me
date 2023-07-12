package ru.practicum.ewmservice.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.dto.*;
import ru.practicum.ewmservice.enums.*;
import ru.practicum.ewmservice.model.*;
import ru.practicum.ewmservice.service.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationMapper {

    private final EventService eventService;
    private final CategoryService categoryService;
    private final EventMapper eventMapper;

    /**
     * Для создания нового ...
     */
    public Compilation toCompilationFormNewCompilationDto(NewCompilationDto newCompilationDto) {
        Compilation compilation = new Compilation();
        compilation.setId(0L);
        if (newCompilationDto.getEvents() == null) {
            compilation.setEvents(new ArrayList<Event>());
        } else {
            List<Event> events = new ArrayList<>();
            for (Long eventId : newCompilationDto.getEvents()) {
                Event event = eventService.checkExistAndGetEvent(eventId);
                events.add(event);
            }
            compilation.setEvents(events);
        }
        compilation.setPinned(newCompilationDto.getPinned());
        compilation.setTitle(newCompilationDto.getTitle());
        return compilation;
    }

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


}
