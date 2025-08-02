# Workforce Management API

A comprehensive Spring Boot application for managing workforce tasks in logistics operations. This API provides task creation, assignment, priority management, and activity tracking capabilities.

## Features

### Core Functionality
- **Task Management**: Create, view, update, and delete tasks
- **Staff Assignment**: Assign tasks to staff members
- **Date-based Filtering**: Smart daily task view with date range queries
- **Customer Reference Tracking**: Manage tasks by customer references

### Bug Fixes
1. **Task Re-assignment Fix**: Prevents duplicate tasks when reassigning by customer reference
2. **Clean Task Lists**: Excludes cancelled tasks from task listings

### New Features
1. **Smart Daily Task View**: Enhanced date filtering to show all relevant active tasks
2. **Task Priority Management**: Set and update task priorities (HIGH, MEDIUM, LOW)
3. **Activity History & Comments**: Complete audit trail and user comments for tasks

## Technology Stack

- **Java**: 17
- **Spring Boot**: 3.0.4
- **Build Tool**: Gradle
- **Dependencies**:
  - Spring Web (REST API)
  - Lombok (boilerplate reduction)
  - MapStruct (object mapping)
- **Storage**: In-memory Java collections

## Quick Start

### Prerequisites
- Java 17 or higher
- Gradle (or use the included wrapper)

### Running the Application

1. **Clone the repository**
   ```bash
   git clone <your-repository-url>
   cd Assignment_Railse
   ```

2. **Build the project**
   ```bash
   ./gradlew build
   ```

3. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

4. **Access the API**
   - Base URL: `http://localhost:8080`
   - API Documentation: Available at runtime

## API Endpoints

### Task Management

#### Create Task
```http
POST /api/tasks
Content-Type: application/json

{
  "title": "Customer Follow-up",
  "description": "Follow up with customer about delivery status",
  "priority": "HIGH",
  "startDate": "2024-01-15",
  "dueDate": "2024-01-20",
  "staffId": "staff1",
  "customerRef": "CUST123"
}
```

#### Get Tasks by Date Range (Smart Daily View)
```http
GET /api/tasks?startDate=2024-01-01&endDate=2024-01-31
```

#### Get Task Details
```http
GET /api/tasks/{taskId}
```

#### Reassign Task by Customer Reference
```http
POST /api/tasks/assign-by-ref?customerRef=CUST123&newStaffId=staff2
```

### Priority Management

#### Update Task Priority
```http
PUT /api/tasks/{taskId}/priority
Content-Type: application/json

{
  "priority": "HIGH"
}
```

#### Get Tasks by Priority
```http
GET /api/tasks/priority/HIGH
```

### Comments & Activity

#### Add Comment to Task
```http
POST /api/tasks/{taskId}/comments
Content-Type: application/json

{
  "comment": "Customer requested expedited delivery",
  "userId": "manager1"
}
```

### Utility Endpoints

#### Get All Tasks
```http
GET /api/tasks/all
```

#### Delete Task
```http
DELETE /api/tasks/{taskId}
```

## Sample Data

The application comes pre-loaded with sample staff members:
- **staff1**: John Doe (john.doe@company.com)
- **staff2**: Jane Smith (jane.smith@company.com)
- **staff3**: Bob Johnson (bob.johnson@company.com)
- **staff4**: Alice Brown (alice.brown@company.com)

## Testing the Application

### Test Bug Fixes

1. **Test Task Re-assignment Fix**:
   ```bash
   # Create a task
   curl -X POST http://localhost:8080/api/tasks \
     -H "Content-Type: application/json" \
     -d '{"title":"Test Task","description":"Test","priority":"MEDIUM","startDate":"2024-01-15","dueDate":"2024-01-20","staffId":"staff1","customerRef":"TEST123"}'
   
   # Reassign to different staff
   curl -X POST "http://localhost:8080/api/tasks/assign-by-ref?customerRef=TEST123&newStaffId=staff2"
   
   # Verify old task is cancelled and new task is created
   curl http://localhost:8080/api/tasks/all
   ```

2. **Test Clean Task Lists**:
   ```bash
   # Get tasks by date range - cancelled tasks should be excluded
   curl "http://localhost:8080/api/tasks?startDate=2024-01-01&endDate=2024-01-31"
   ```

### Test New Features

1. **Test Priority Management**:
   ```bash
   # Create task with priority
   curl -X POST http://localhost:8080/api/tasks \
     -H "Content-Type: application/json" \
     -d '{"title":"High Priority Task","description":"Urgent","priority":"HIGH","startDate":"2024-01-15","dueDate":"2024-01-16","staffId":"staff1","customerRef":"HIGH123"}'
   
   # Get high priority tasks
   curl http://localhost:8080/api/tasks/priority/HIGH
   ```

2. **Test Comments**:
   ```bash
   # Add comment to task
   curl -X POST http://localhost:8080/api/tasks/{taskId}/comments \
     -H "Content-Type: application/json" \
     -d '{"comment":"Important update","userId":"manager1"}'
   ```

## Project Structure

```
src/main/java/com/railse/workforcemgmt/
├── WorkforcemgmtApplication.java          # Main application class
├── controller/
│   └── TaskController.java               # REST API endpoints
├── service/
│   ├── TaskService.java                  # Business logic
│   └── StaffService.java                 # Staff management
├── model/
│   ├── Task.java                         # Task entity
│   ├── Staff.java                        # Staff entity
│   ├── TaskStatus.java                   # Task status enum
│   ├── TaskPriority.java                 # Task priority enum
│   └── TaskActivity.java                 # Activity tracking
├── dto/
│   ├── TaskDto.java                      # Task response DTO
│   ├── StaffDto.java                     # Staff response DTO
│   ├── TaskActivityDto.java              # Activity response DTO
│   ├── CreateTaskRequest.java            # Task creation request
│   ├── UpdatePriorityRequest.java        # Priority update request
│   └── AddCommentRequest.java            # Comment addition request
└── mapper/
    ├── TaskMapper.java                   # Task object mapper
    ├── StaffMapper.java                  # Staff object mapper
    └── TaskActivityMapper.java           # Activity object mapper
```

## Key Features Explained

### Smart Daily Task View
The enhanced date-based filtering returns:
- Tasks that started within the specified date range
- Tasks that started before the range but are still active and not completed

This provides operations employees with a true "today's work" view.

### Activity History
Every task operation is automatically logged with:
- Timestamp
- User/System identifier
- Action type
- Description
- Optional comments

### Priority Management
Tasks can have three priority levels:
- **HIGH**: Urgent tasks requiring immediate attention
- **MEDIUM**: Standard priority tasks
- **LOW**: Low priority tasks

## Error Handling

The application includes comprehensive error handling:
- Resource not found errors
- Invalid request validation
- Proper HTTP status codes
- Descriptive error messages

## Development

### Building from Source
```bash
./gradlew clean build
```

### Running Tests
```bash
./gradlew test
```

### Code Quality
The project uses:
- Lombok for reducing boilerplate
- MapStruct for efficient object mapping
- Spring Boot best practices
- RESTful API design principles

## License

This project is created for the Railse Backend Engineer Challenge. 