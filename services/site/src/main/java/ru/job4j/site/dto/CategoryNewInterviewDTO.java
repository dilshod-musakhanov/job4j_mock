package ru.job4j.site.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryNewInterviewDTO {
    private int id;
    private String name;
    private int total;
    private int topicsSize;
    private int position;
    private int newInterviewCount;

    public CategoryNewInterviewDTO(int id, String name) {
        this(id, name, 0, 0, 0, 0);
    }
}
