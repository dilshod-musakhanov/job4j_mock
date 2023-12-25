package ru.checkdev.notification.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryDTO {
    private int id;
    private String name;
    private int total;
    private int position;
}
