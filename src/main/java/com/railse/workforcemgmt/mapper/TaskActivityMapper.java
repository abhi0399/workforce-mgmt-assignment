package com.railse.workforcemgmt.mapper;

import com.railse.workforcemgmt.dto.TaskActivityDto;
import com.railse.workforcemgmt.model.TaskActivity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskActivityMapper {
    
    TaskActivityDto toDto(TaskActivity activity);
    
    List<TaskActivityDto> toDtoList(List<TaskActivity> activities);
} 