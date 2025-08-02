package com.railse.workforcemgmt.controller;

import com.railse.workforcemgmt.dto.AddCommentRequest;
import com.railse.workforcemgmt.dto.CreateTaskRequest;
import com.railse.workforcemgmt.dto.TaskDto;
import com.railse.workforcemgmt.dto.UpdatePriorityRequest;
import com.railse.workforcemgmt.model.TaskPriority;
import com.railse.workforcemgmt.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@RequestBody CreateTaskRequest request) {
        TaskDto task = taskService.createTask(request);
        return ResponseEntity.ok(task);
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getTasksByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<TaskDto> tasks = taskService.getTasksByDateRange(startDate, endDate);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable String taskId) {
        TaskDto task = taskService.getTaskById(taskId);
        return ResponseEntity.ok(task);
    }

    @PostMapping("/assign-by-ref")
    public ResponseEntity<TaskDto> assignTaskByCustomerRef(
            @RequestParam String customerRef,
            @RequestParam String newStaffId) {
        TaskDto task = taskService.assignTaskByCustomerRef(customerRef, newStaffId);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{taskId}/priority")
    public ResponseEntity<TaskDto> updateTaskPriority(
            @PathVariable String taskId,
            @RequestBody UpdatePriorityRequest request) {
        TaskDto task = taskService.updateTaskPriority(taskId, request);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<TaskDto>> getTasksByPriority(@PathVariable TaskPriority priority) {
        List<TaskDto> tasks = taskService.getTasksByPriority(priority);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/{taskId}/comments")
    public ResponseEntity<TaskDto> addCommentToTask(
            @PathVariable String taskId,
            @RequestBody AddCommentRequest request) {
        TaskDto task = taskService.addCommentToTask(taskId, request);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/all")
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        List<TaskDto> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable String taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
} 