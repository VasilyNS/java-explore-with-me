package ru.practicum.ewmservice.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.ewmservice.dto.*;
import ru.practicum.ewmservice.enums.*;
import ru.practicum.ewmservice.mapper.CompilationMapper;
import ru.practicum.ewmservice.mapper.EventMapper;
import ru.practicum.ewmservice.model.*;
import ru.practicum.ewmservice.repository.*;
import ru.practicum.ewmservice.tools.*;
import ru.practicum.ewmservice.tools.exception.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CompilationService {

    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
//    @PersistenceContext
//    private EntityManager entityManager; // Для QueryDSL

    /**
     * Добавление новой подборки (подборка может не содержать событий)
     */
    @Transactional
    public CompilationDto saveCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationMapper.toCompilationFormNewCompilationDto(newCompilationDto);

        Compilation compilationForReturn = compilationRepository.save(compilation);
        return compilationMapper.toCompilationDtoFormCompilation(compilationForReturn);
    }

//    public EventFullDto saveEvent(NewEventDto newEventDto, Long userId) {
//        Event event = eventMapper.toEventFormNewEventDto(newEventDto, userId);
//
//        Event eventForReturn = eventRepository.save(event);
//        return eventMapper.toEventFullDtoFromEvent(eventForReturn);
//    }


    /**
     * Проверка, что сущность есть в БД, если нет - исключение, если да - возврат объекта с ней
     */
    @Transactional(readOnly = true)
    public Compilation checkExistAndGetCompilation(Long id) {
        return compilationRepository.findById(id).orElseThrow(() -> new NotFoundException("Compilation " + id));
    }

}
