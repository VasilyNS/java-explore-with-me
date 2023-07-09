package ru.practicum.ewmservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.dto.*;
import ru.practicum.ewmservice.mapper.*;
import ru.practicum.ewmservice.model.*;
import ru.practicum.ewmservice.repository.*;
import ru.practicum.ewmservice.tools.exception.*;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryDto saveCategory(NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.toCategoryFormNewCategoryDto(newCategoryDto);

        Category categoryForSave;
        categoryForSave = categoryRepository.save(category);
        return CategoryMapper.toCategoryDto(categoryForSave);
    }

    @Transactional
    public void delCategory(Long id) {
        checkExistAndGetCategory(id);
        categoryRepository.deleteById(id);
    }

    /**
     * Метод обновления простой только из-за того, что сущность имеет всего 2 поля
     * id и name. При update сущностей с большим количеством полей нужна проверка на null
     * каждого из полей dto-класса (см. EventMapper.toEventFromUpdateEventUserRequest).
     */
    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        checkExistAndGetCategory(id);
        Category categoryForSave = CategoryMapper.toCategoryFormCategoryDto(categoryDto);
        categoryForSave.setId(id);

        Category categoryForReturn = categoryRepository.save(categoryForSave);
        return CategoryMapper.toCategoryDto(categoryForReturn);
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories(Long from, Long size) {
        int pageNum = (int) (from / size);
        Pageable pageable = PageRequest.of(pageNum, Math.toIntExact(size));
        return categoryRepository.getAllCategories(pageable);
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long id) {
        Category category = checkExistAndGetCategory(id);
        return CategoryMapper.toCategoryDto(category);
    }

    /**
     * Проверка, что сущность есть в БД, если нет - исключение, если да - возврат объекта с ней
     */
    public Category checkExistAndGetCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category " + id));
    }

}
