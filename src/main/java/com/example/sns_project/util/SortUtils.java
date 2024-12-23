package com.example.sns_project.util;

import org.springframework.data.domain.Sort;

import java.util.Arrays;

public class SortUtils {
    public static Sort.Order[] getSortOrder(String[] sort) {
        return Arrays.stream(sort)
                .map(s -> {
                    String[] parts = s.split(",");
                    return new Sort.Order(
                            parts.length > 1 && parts[1].equalsIgnoreCase("desc")
                                    ? Sort.Direction.DESC
                                    : Sort.Direction.ASC,
                            parts[0]
                    );
                })
                .toArray(Sort.Order[]::new);
    }
}