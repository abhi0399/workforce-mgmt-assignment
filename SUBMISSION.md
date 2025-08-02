# Railse Backend Assignment Submission

## Personal Information
- **Name**: [Your Name]
- **Email**: [Your Email]
- **GitHub Repository**: [Your GitHub Repository URL]
- **Video Demo**: [Your Video Demo URL]

## Project Overview
This is a Workforce Management API built with Spring Boot 3.0.4 and Java 17. The application provides comprehensive task management functionality for logistics operations, including task creation, assignment, priority management, and activity tracking.

## Technical Stack
- **Language**: Java 17
- **Framework**: Spring Boot 3.0.4
- **Build Tool**: Gradle
- **Dependencies**: 
  - Spring Web (for REST API)
  - Lombok (for reducing boilerplate)
  - MapStruct (for object mapping)
- **Database**: In-memory storage using Java collections

## Project Structure
```
src/main/java/com/railse/workforcemgmt/
├── WorkforcemgmtApplication.java
├── controller/
│   └── TaskController.java
├── service/
│   ├── TaskService.java
│   └── StaffService.java
├── model/
│   ├── Task.java
│   ├── Staff.java
│   ├── TaskStatus.java
│   ├── TaskPriority.java
│   └── TaskActivity.java
├── dto/
│   ├── TaskDto.java
│   ├── StaffDto.java
│   ├── TaskActivityDto.java
│   ├── CreateTaskRequest.java
│   ├── UpdatePriorityRequest.java
│   └── AddCommentRequest.java
└── mapper/
    ├── TaskMapper.java
    ├── StaffMapper.java
    └── TaskActivityMapper.java
```

## Bug Fixes Implemented

### Bug 1: Task Re-assignment Creates Duplicates
**Problem**: When reassigning tasks using the 'assign-by-ref' feature, old tasks weren't being removed, creating duplicates.

**Solution**: Modified the `assignTaskByCustomerRef` method in `TaskService` to:
- Cancel all existing active tasks for the customer reference before creating a new one
- Mark old tasks as `CANCELLED` status
- Add cancellation activity to track the change
- Create a new task with the same details but assigned to the new staff member

**Code Location**: `TaskService.assignTaskByCustomerRef()` method

### Bug 2: Cancelled Tasks Clutter the View
**Problem**: Cancelled tasks were appearing in task lists, cluttering the view for operations employees.

**Solution**: Modified the `getTasksByDateRange` method to filter out tasks with `CANCELLED` status.

**Code Location**: `TaskService.getTasksByDateRange()` method

## New Features Implemented

### Feature 1: Smart Daily Task View
**Requirement**: Enhanced date-based task fetching to show all active tasks that started within the range PLUS all active tasks that started before the range but are still open.

**Implementation**: 
- Modified `getTasksByDateRange` method to include tasks that:
  - Started within the specified date range, OR
  - Started before the range but are still active and not completed
- This provides a true "today's work" view for operations employees

**Endpoint**: `GET /api/tasks?startDate=2024-01-01&endDate=2024-01-31`

### Feature 2: Task Priority Management
**Requirement**: Add priority field and endpoints to manage task priorities.

**Implementation**:
- Added `TaskPriority` enum with HIGH, MEDIUM, LOW values
- Added priority field to Task model
- Created endpoint to update task priority: `PUT /api/tasks/{taskId}/priority`
- Created endpoint to fetch tasks by priority: `GET /api/tasks/priority/{priority}`
- Automatic activity logging when priority is changed

**Endpoints**:
- `PUT /api/tasks/{taskId}/priority` - Update task priority
- `GET /api/tasks/priority/{priority}` - Get tasks by priority

### Feature 3: Task Comments & Activity History
**Requirement**: Implement activity history and user comments for tasks.

**Implementation**:
- Created `TaskActivity` model to track all task events
- Added activity tracking for: task creation, reassignment, priority changes, comments
- Created endpoint to add comments: `POST /api/tasks/{taskId}/comments`
- Enhanced task detail endpoint to include complete activity history
- Activities are sorted chronologically and include user comments

**Endpoints**:
- `POST /api/tasks/{taskId}/comments` - Add comment to task
- `GET /api/tasks/{taskId}` - Get task with full activity history

## API Endpoints

### Core Task Management
- `POST /api/tasks` - Create a new task
- `GET /api/tasks?startDate=2024-01-01&endDate=2024-01-31` - Get tasks by date range (Smart Daily View)
- `GET /api/tasks/{taskId}` - Get task details with activity history
- `DELETE /api/tasks/{taskId}` - Delete a task

### Task Assignment
- `POST /api/tasks/assign-by-ref?customerRef=REF123&newStaffId=staff2` - Reassign task by customer reference

### Priority Management
- `PUT /api/tasks/{taskId}/priority` - Update task priority
- `GET /api/tasks/priority/{priority}` - Get tasks by priority

### Comments & Activity
- `POST /api/tasks/{taskId}/comments` - Add comment to task

### Utility
- `GET /api/tasks/all` - Get all tasks

## Key Design Decisions

1. **In-Memory Storage**: Used Java collections for data storage as specified in requirements
2. **Activity Tracking**: Implemented comprehensive activity logging for all task operations
3. **DTO Pattern**: Used DTOs for API requests/responses to separate internal models from external API
4. **MapStruct**: Used for efficient object mapping between models and DTOs
5. **Lombok**: Reduced boilerplate code for models and DTOs
6. **RESTful Design**: Followed REST conventions for API endpoints

## Testing Instructions

1. **Start the application**: Run `./gradlew bootRun`
2. **Test Bug Fixes**:
   - Create a task with customer reference
   - Reassign it to different staff - verify old task is cancelled
   - Fetch tasks by date range - verify cancelled tasks are excluded
3. **Test New Features**:
   - Create tasks with different priorities
   - Update task priorities
   - Add comments to tasks
   - View task details with activity history
   - Test smart daily view with tasks spanning multiple dates

## Video Demo Content
The video demo will showcase:
1. Bug Fix 1: Task reassignment without duplicates
2. Bug Fix 2: Clean task lists without cancelled tasks
3. Feature 1: Smart daily task view
4. Feature 2: Priority management
5. Feature 3: Comments and activity history

## Additional Notes
- All endpoints include proper error handling
- Activity history is automatically maintained for all operations
- The application uses proper HTTP status codes
- CORS is enabled for cross-origin requests
- Comprehensive logging is implemented for debugging 