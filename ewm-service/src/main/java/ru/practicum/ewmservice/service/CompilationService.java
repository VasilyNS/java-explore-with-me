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
public class CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    /**
     * Добавление новой подборки, подборка может не содержать событий (Admin API)
     */
    @Transactional
    public CompilationDto saveCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationMapper.toCompilationFormNewCompilationDto(newCompilationDto);

        Compilation compilationForReturn = compilationRepository.save(compilation);
        return compilationMapper.toCompilationDtoFormCompilation(compilationForReturn);
    }

    /**
     * Удаление подборки (Admin API)
     */
    @Transactional
    public void delCompilation(Long compId) {
        compilationRepository.deleteById(compId);
    }

    /**
     * Обновление информации о подборке (Admin API)
     */
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = checkExistAndGetCompilation(compId);
        Compilation compilationForReturn = compilationMapper.toCompilationFromUpdateEventUserRequest(
                compilation, updateCompilationRequest);

        return compilationMapper.toCompilationDtoFormCompilation(compilationForReturn);
    }

    /**
     * Получение подборок событий (Public API)
     * В случае, если по заданным фильтрам не найдено ни одной подборки, возвращает пустой список
     */
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Boolean pinned, Long from, Long size) {
        int pageNum = (int) (from / size);
        Pageable pageable = PageRequest.of(pageNum, Math.toIntExact(size));
        List<Compilation> compilations;
        if (pinned == null) {
            compilations = compilationRepository.getCompilations(pageable);
        } else {
            compilations = compilationRepository.getCompilationsByPinned(pinned, pageable);
        }
        return compilations.stream().map(compilationMapper::toCompilationDtoFormCompilation).collect(Collectors.toList());
    }

    /**
     * Получение подборки событий по его id (Public API)
     * В случае, если подборки с заданным id не найдено, возвращает статус код 404
     */
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = checkExistAndGetCompilation(compId);
        return compilationMapper.toCompilationDtoFormCompilation(compilation);
    }

    /**
     * Проверка, что сущность есть в БД, если нет - исключение, если да - возврат объекта с ней
     */
    @Transactional(readOnly = true)
    public Compilation checkExistAndGetCompilation(Long id) {
        return compilationRepository.findById(id).orElseThrow(() -> new NotFoundException("Compilation " + id));
    }

}
