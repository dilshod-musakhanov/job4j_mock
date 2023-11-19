package ru.job4j.site.util;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.job4j.site.dto.CategoryDTO;
import ru.job4j.site.dto.CategoryNewInterviewDTO;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class CategoryInterviewDtoMapper {

    public List<CategoryNewInterviewDTO> toDto(List<CategoryDTO> categoryDTOS) {
        List<CategoryNewInterviewDTO> list = new ArrayList<>();
        for (CategoryDTO categoryDTO : categoryDTOS) {
            CategoryNewInterviewDTO dto = new CategoryNewInterviewDTO();
            dto.setId(categoryDTO.getId());
            dto.setName(categoryDTO.getName());
            dto.setTotal(categoryDTO.getTotal());
            dto.setTopicsSize(categoryDTO.getTopicsSize());
            dto.setPosition(categoryDTO.getPosition());
            list.add(dto);
        }
        return list;
    }
}
