package com.railse.workforcemgmt.dto;

import com.railse.workforcemgmt.model.TaskPriority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequest {
    private String title;
    private String description;
    private TaskPriority priority;
    private LocalDate startDate;
    private LocalDate dueDate;
    private String staffId;
    private String customerRef;
} 