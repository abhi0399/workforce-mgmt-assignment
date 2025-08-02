package com.railse.workforcemgmt.service;

import com.railse.workforcemgmt.model.Staff;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StaffService {

    // In-memory storage for staff
    private final Map<String, Staff> staff = new HashMap<>();

    public StaffService() {
        // Initialize with some sample staff
        staff.put("staff1", new Staff("staff1", "John Doe", "john.doe@company.com"));
        staff.put("staff2", new Staff("staff2", "Jane Smith", "jane.smith@company.com"));
        staff.put("staff3", new Staff("staff3", "Bob Johnson", "bob.johnson@company.com"));
        staff.put("staff4", new Staff("staff4", "Alice Brown", "alice.brown@company.com"));
    }

    public Staff getStaffById(String staffId) {
        return staff.get(staffId);
    }

    public Map<String, Staff> getAllStaff() {
        return new HashMap<>(staff);
    }
} 