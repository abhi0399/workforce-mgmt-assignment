package com.railse.workforcemgmt.mapper;

import com.railse.workforcemgmt.dto.TaskDto;
import com.railse.workforcemgmt.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {StaffMapper.class, TaskActivityMapper.class})
public interface TaskMapper {
    
    @Mapping(target = "assignedTo", source = "assignedTo")
    @Mapping(target = "activities", source = "activities")
    TaskDto toDto(Task task);
    
    List<TaskDto> toDtoList(List<Task> tasks);
} 