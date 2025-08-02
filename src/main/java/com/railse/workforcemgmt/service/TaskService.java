package com.railse.workforcemgmt.service;

import com.railse.workforcemgmt.dto.AddCommentRequest;
import com.railse.workforcemgmt.dto.CreateTaskRequest;
import com.railse.workforcemgmt.dto.TaskDto;
import com.railse.workforcemgmt.dto.UpdatePriorityRequest;
import com.railse.workforcemgmt.mapper.TaskMapper;
import com.railse.workforcemgmt.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private StaffService staffService;

    // In-memory storage
    private final Map<String, Task> tasks = new HashMap<>();
    private final Map<String, List<Task>> tasksByCustomerRef = new HashMap<>();

    public TaskDto createTask(CreateTaskRequest request) {
        Staff staff = staffService.getStaffById(request.getStaffId());
        if (staff == null) {
            throw new RuntimeException("Staff not found with id: " + request.getStaffId());
        }

        String taskId = UUID.randomUUID().toString();
        Task task = new Task(
                taskId,
                request.getTitle(),
                request.getDescription(),
                TaskStatus.ACTIVE,
                request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM,
                request.getStartDate(),
                request.getDueDate(),
                staff,
                request.getCustomerRef()
        );

        // Add creation activity
        TaskActivity creationActivity = new TaskActivity(
                UUID.randomUUID().toString(),
                taskId,
                "system",
                "TASK_CREATED",
                "Task created by system",
                LocalDateTime.now(),
                null
        );
        task.addActivity(creationActivity);
        tasks.put(taskId, task);

        // Store by customer reference for reassignment logic
        if (request.getCustomerRef() != null) {
            tasksByCustomerRef.computeIfAbsent(request.getCustomerRef(), k -> new ArrayList<>()).add(task);
        }

        return taskMapper.toDto(task);
    }

    public List<TaskDto> getTasksByDateRange(LocalDate startDate, LocalDate endDate) {
        return tasks.values().stream()
                .filter(task -> task.getStatus() == TaskStatus.ACTIVE)
                .filter(task -> {
                    // Bug Fix 2: Exclude CANCELLED tasks
//                    if (task.getStatus() == TaskStatus.CANCELLED) {
//                        return false;
//                    }
                    
                    // Feature 1: Smart Daily Task View
                    // Return tasks that started within the range OR
                    // tasks that started before the range but are still active and not completed
                    return (task.getStartDate().isAfter(startDate.minusDays(1)) && 
                            task.getStartDate().isBefore(endDate.plusDays(1))) ||
                           (task.getStartDate().isBefore(startDate));
                })
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    public TaskDto getTaskById(String taskId) {
        Task task = tasks.get(taskId);
        if (task == null) {
            throw new RuntimeException("Task not found with id: " + taskId);
        }
        return taskMapper.toDto(task);
    }

    public TaskDto assignTaskByCustomerRef(String customerRef, String newStaffId) {
        Staff newStaff = staffService.getStaffById(newStaffId);
        if (newStaff == null) {
            throw new RuntimeException("Staff not found with id: " + newStaffId);
        }

        List<Task> existingTasks = tasksByCustomerRef.get(customerRef);
        if (existingTasks == null || existingTasks.isEmpty()) {
            throw new RuntimeException("No tasks found for customer reference: " + customerRef);
        }

        // Bug Fix 1: Cancel existing tasks before creating new one
        for (Task existingTask : existingTasks) {
            if (existingTask.getStatus() == TaskStatus.ACTIVE) {
                existingTask.setStatus(TaskStatus.CANCELLED);
                existingTask.setUpdatedAt(LocalDateTime.now());
                
                // Add cancellation activity
                TaskActivity cancellationActivity = new TaskActivity(
                        UUID.randomUUID().toString(),
                        existingTask.getId(),
                        "system",
                        "TASK_CANCELLED",
                        "Task cancelled due to reassignment to " + newStaff.getName(),
                        LocalDateTime.now(),
                        null
                );
                existingTask.addActivity(cancellationActivity);
            }
        }

        // Create new task with same details as the most recent active task
        Task mostRecentTask = existingTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.ACTIVE)
                .max(Comparator.comparing(Task::getCreatedAt))
                .orElse(existingTasks.get(0));

        String newTaskId = UUID.randomUUID().toString();
        Task newTask = new Task(
                newTaskId,
                mostRecentTask.getTitle(),
                mostRecentTask.getDescription(),
                TaskStatus.ACTIVE,
                mostRecentTask.getPriority(),
                mostRecentTask.getStartDate(),
                mostRecentTask.getDueDate(),
                newStaff,
                customerRef
        );

        // Add reassignment activity
        TaskActivity reassignmentActivity = new TaskActivity(
                UUID.randomUUID().toString(),
                newTaskId,
                "system",
                "TASK_REASSIGNED",
                "Task reassigned from " + mostRecentTask.getAssignedTo().getName() + " to " + newStaff.getName(),
                LocalDateTime.now(),
                null
        );
        newTask.addActivity(reassignmentActivity);

        tasks.put(newTaskId, newTask);
        tasksByCustomerRef.computeIfAbsent(customerRef, k -> new ArrayList<>()).add(newTask);

        return taskMapper.toDto(newTask);
    }

    public TaskDto updateTaskPriority(String taskId, UpdatePriorityRequest request) {
        Task task = tasks.get(taskId);
        if (task == null) {
            throw new RuntimeException("Task not found with id: " + taskId);
        }

        TaskPriority oldPriority = task.getPriority();
        task.setPriority(request.getPriority());
        task.setUpdatedAt(LocalDateTime.now());

        // Add priority change activity
        TaskActivity priorityActivity = new TaskActivity(
                UUID.randomUUID().toString(),
                taskId,
                "system",
                "PRIORITY_CHANGED",
                "Priority changed from " + oldPriority + " to " + request.getPriority(),
                LocalDateTime.now(),
                null
        );
        task.addActivity(priorityActivity);

        return taskMapper.toDto(task);
    }

    public List<TaskDto> getTasksByPriority(TaskPriority priority) {
        return tasks.values().stream()
                .filter(task -> task.getPriority() == priority)
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    public TaskDto addCommentToTask(String taskId, AddCommentRequest request) {
        Task task = tasks.get(taskId);
        if (task == null) {
            throw new RuntimeException("Task not found with id: " + taskId);
        }

        // Add comment activity
        TaskActivity commentActivity = new TaskActivity(
                UUID.randomUUID().toString(),
                taskId,
                request.getUserId(),
                "COMMENT_ADDED",
                "Comment added by user",
                LocalDateTime.now(),
                request.getComment()
        );
        task.addActivity(commentActivity);

        return taskMapper.toDto(task);
    }

    public List<TaskDto> getAllTasks() {
        return taskMapper.toDtoList(new ArrayList<>(tasks.values()));
    }

    public void deleteTask(String taskId) {
        Task task = tasks.remove(taskId);
        if (task != null && task.getCustomerRef() != null) {
            List<Task> customerTasks = tasksByCustomerRef.get(task.getCustomerRef());
            if (customerTasks != null) {
                customerTasks.removeIf(t -> t.getId().equals(taskId));
            }
        }
    }
} 