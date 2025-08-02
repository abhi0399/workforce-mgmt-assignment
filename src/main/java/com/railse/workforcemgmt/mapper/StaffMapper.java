package com.railse.workforcemgmt.mapper;

import com.railse.workforcemgmt.dto.StaffDto;
import com.railse.workforcemgmt.model.Staff;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StaffMapper {
    
    StaffDto toDto(Staff staff);
    
    Staff toEntity(StaffDto staffDto);
    
    List<StaffDto> toDtoList(List<Staff> staffList);
} 