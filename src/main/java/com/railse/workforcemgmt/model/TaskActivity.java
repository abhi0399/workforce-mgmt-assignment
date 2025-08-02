package com.railse.workforcemgmt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskActivity {
    private String id;
    private String taskId;
    private String userId;
    private String action;
    private String description;
    private LocalDateTime timestamp;
    private String comment;
} 