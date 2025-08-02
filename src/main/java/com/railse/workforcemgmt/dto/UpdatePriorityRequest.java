package com.railse.workforcemgmt.dto;

import com.railse.workforcemgmt.model.TaskPriority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePriorityRequest {
    private TaskPriority priority;
} 