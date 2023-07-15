package ru.practicum.ewmservice.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmservice.dto.*;
import ru.practicum.ewmservice.model.*;

@UtilityClass
public class CategoryMapper {

    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }

    public static Category toCategoryFormNewCategoryDto(NewCategoryDto newCategoryDto) {
        return new Category(
                0L,
                newCategoryDto.getName()
        );
    }

    public static Category toCategoryFormCategoryDto(CategoryDto categoryDto) {
        return new Category(
                0L,
                categoryDto.getName()
        );
    }

}
