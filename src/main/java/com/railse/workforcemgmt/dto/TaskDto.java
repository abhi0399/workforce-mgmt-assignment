package com.railse.workforcemgmt.dto;

import com.railse.workforcemgmt.model.TaskPriority;
import com.railse.workforcemgmt.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {
    private String id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate startDate;
    private LocalDate dueDate;
    private StaffDto assignedTo;
    private String customerRef;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TaskActivityDto> activities;
} 