package com.example.sns_project.batch.dto;

import com.example.sns_project.batch.entity.InputType;
import lombok.Data;


@Data
public class OutputType {


    private Long id;
    private String processedData;

    public OutputType(InputType input) {
        this.id = input.getId();
        this.processedData = "Processed: " + input.getData();
    }
}
