package com.railse.workforcemgmt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    private String id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate startDate;
    private LocalDate dueDate;
    private Staff assignedTo;
    private String customerRef;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TaskActivity> activities;
    
    public Task(String id, String title, String description, TaskStatus status, 
                TaskPriority priority, LocalDate startDate, LocalDate dueDate, 
                Staff assignedTo, String customerRef) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.assignedTo = assignedTo;
        this.customerRef = customerRef;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.activities = new ArrayList<>();
    }
    
    public void addActivity(TaskActivity activity) {
        if (this.activities == null) {
            this.activities = new ArrayList<>();
        }
        this.activities.add(activity);
        this.updatedAt = LocalDateTime.now();
    }
} 