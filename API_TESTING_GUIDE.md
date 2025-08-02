# API Testing Guide - Workforce Management API

## Prerequisites
1. Start the application: `./gradlew bootRun`
2. The API will be available at: `http://localhost:8080`
3. Sample staff IDs available: `staff1`, `staff2`, `staff3`, `staff4`

## Test Data Setup

### Available Staff Members
- **staff1**: John Doe (john.doe@company.com)
- **staff2**: Jane Smith (jane.smith@company.com)
- **staff3**: Bob Johnson (bob.johnson@company.com)
- **staff4**: Alice Brown (alice.brown@company.com)

---

## 1. BUG FIX TESTING

### Bug Fix 1: Task Re-assignment Creates Duplicates

**Problem**: When reassigning tasks, old tasks weren't being removed, creating duplicates.

**Test Steps**:

#### Step 1: Create initial task
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Customer Follow-up",
    "description": "Follow up with customer about delivery status",
    "priority": "MEDIUM",
    "startDate": "2024-01-15",
    "dueDate": "2024-01-20",
    "staffId": "staff1",
    "customerRef": "CUST123"
  }'
```

**Expected Response**: Task created with status ACTIVE

#### Step 2: Check all tasks (should show 1 active task)
```bash
curl http://localhost:8080/api/tasks/all
```

#### Step 3: Reassign task to different staff
```bash
curl -X POST "http://localhost:8080/api/tasks/assign-by-ref?customerRef=CUST123&newStaffId=staff2"
```

**Expected Response**: New task created, old task should be CANCELLED

#### Step 4: Verify bug fix (check all tasks)
```bash
curl http://localhost:8080/api/tasks/all
```

**Expected Result**: 
- Should see 2 tasks for CUST123
- First task: status = "CANCELLED", assignedTo = "John Doe"
- Second task: status = "ACTIVE", assignedTo = "Jane Smith"

#### Step 5: Test date range query (should exclude cancelled tasks)
```bash
curl "http://localhost:8080/api/tasks?startDate=2024-01-01&endDate=2024-01-31"
```

**Expected Result**: Only the active task should appear, cancelled task should be excluded

---

### Bug Fix 2: Cancelled Tasks Clutter the View

**Problem**: Cancelled tasks were appearing in task lists.

**Test Steps**:

#### Step 1: Create multiple tasks with different statuses
```bash
# Create active task
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Active Task",
    "description": "This is an active task",
    "priority": "HIGH",
    "startDate": "2024-01-15",
    "dueDate": "2024-01-20",
    "staffId": "staff1",
    "customerRef": "ACTIVE123"
  }'

# Create another task
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Another Task",
    "description": "This is another task",
    "priority": "LOW",
    "startDate": "2024-01-16",
    "dueDate": "2024-01-25",
    "staffId": "staff2",
    "customerRef": "ANOTHER123"
  }'
```

#### Step 2: Cancel one task by reassigning it
```bash
curl -X POST "http://localhost:8080/api/tasks/assign-by-ref?customerRef=ACTIVE123&newStaffId=staff3"
```

#### Step 3: Test date range query (should exclude cancelled tasks)
```bash
curl "http://localhost:8080/api/tasks?startDate=2024-01-01&endDate=2024-01-31"
```

**Expected Result**: 
- Should only see active tasks
- Cancelled tasks should NOT appear in the list
- This demonstrates the bug fix working correctly

---

## 2. NEW FEATURES TESTING

### Feature 1: Smart Daily Task View

**Requirement**: Show tasks that started within range OR tasks that started before but are still active.

**Test Steps**:

#### Step 1: Create tasks spanning different dates
```bash
# Create task that started yesterday but is still active
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Ongoing Task",
    "description": "This task started yesterday but is still active",
    "priority": "HIGH",
    "startDate": "2024-01-14",
    "dueDate": "2024-01-25",
    "staffId": "staff1",
    "customerRef": "ONGOING123"
  }'

# Create task that starts today
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Today Task",
    "description": "This task starts today",
    "priority": "MEDIUM",
    "startDate": "2024-01-15",
    "dueDate": "2024-01-18",
    "staffId": "staff2",
    "customerRef": "TODAY123"
  }'

# Create task that starts tomorrow
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Tomorrow Task",
    "description": "This task starts tomorrow",
    "priority": "LOW",
    "startDate": "2024-01-16",
    "dueDate": "2024-01-22",
    "staffId": "staff3",
    "customerRef": "TOMORROW123"
  }'
```

#### Step 2: Test smart daily view for today's date range
```bash
curl "http://localhost:8080/api/tasks?startDate=2024-01-15&endDate=2024-01-15"
```

**Expected Result**: 
- Should see "Ongoing Task" (started before but still active)
- Should see "Today Task" (started within range)
- Should see "Tomorrow Task" (started within range)
- This demonstrates the smart daily view feature

---

### Feature 2: Task Priority Management

**Test Steps**:

#### Step 1: Create tasks with different priorities
```bash
# Create HIGH priority task
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Urgent Task",
    "description": "This is an urgent task",
    "priority": "HIGH",
    "startDate": "2024-01-15",
    "dueDate": "2024-01-16",
    "staffId": "staff1",
    "customerRef": "URGENT123"
  }'

# Create MEDIUM priority task
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Normal Task",
    "description": "This is a normal priority task",
    "priority": "MEDIUM",
    "startDate": "2024-01-15",
    "dueDate": "2024-01-20",
    "staffId": "staff2",
    "customerRef": "NORMAL123"
  }'

# Create LOW priority task
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Low Priority Task",
    "description": "This is a low priority task",
    "priority": "LOW",
    "startDate": "2024-01-15",
    "dueDate": "2024-01-30",
    "staffId": "staff3",
    "customerRef": "LOW123"
  }'
```

#### Step 2: Get tasks by priority
```bash
# Get HIGH priority tasks
curl http://localhost:8080/api/tasks/priority/HIGH

# Get MEDIUM priority tasks
curl http://localhost:8080/api/tasks/priority/MEDIUM

# Get LOW priority tasks
curl http://localhost:8080/api/tasks/priority/LOW
```

#### Step 3: Update task priority
```bash
# First, get a task ID from the previous responses
# Then update its priority (replace {taskId} with actual ID)
curl -X PUT http://localhost:8080/api/tasks/{taskId}/priority \
  -H "Content-Type: application/json" \
  -d '{
    "priority": "HIGH"
  }'
```

**Expected Result**: 
- Priority should be updated
- Activity history should show the priority change
- Task should now appear in HIGH priority list

---

### Feature 3: Task Comments & Activity History

**Test Steps**:

#### Step 1: Create a task
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Task with Comments",
    "description": "This task will have comments and activity history",
    "priority": "MEDIUM",
    "startDate": "2024-01-15",
    "dueDate": "2024-01-20",
    "staffId": "staff1",
    "customerRef": "COMMENT123"
  }'
```

#### Step 2: Get task details (should show creation activity)
```bash
# Replace {taskId} with the actual task ID from step 1
curl http://localhost:8080/api/tasks/{taskId}
```

**Expected Result**: Should show task with creation activity

#### Step 3: Add comment to task
```bash
# Replace {taskId} with the actual task ID
curl -X POST http://localhost:8080/api/tasks/{taskId}/comments \
  -H "Content-Type: application/json" \
  -d '{
    "comment": "Customer requested expedited delivery",
    "userId": "manager1"
  }'
```

#### Step 4: Add another comment
```bash
curl -X POST http://localhost:8080/api/tasks/{taskId}/comments \
  -H "Content-Type: application/json" \
  -d '{
    "comment": "Delivery confirmed for tomorrow",
    "userId": "staff1"
  }'
```

#### Step 5: Get task details again (should show all activities)
```bash
curl http://localhost:8080/api/tasks/{taskId}
```

**Expected Result**: 
- Should show task creation activity
- Should show both comments in chronological order
- Each activity should have timestamp, user, and description

---

## 3. COMPREHENSIVE TESTING SCENARIO

### Complete Workflow Test

#### Step 1: Create multiple tasks
```bash
# Task 1
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Customer A Follow-up",
    "description": "Follow up with Customer A about order",
    "priority": "HIGH",
    "startDate": "2024-01-15",
    "dueDate": "2024-01-16",
    "staffId": "staff1",
    "customerRef": "CUST_A"
  }'

# Task 2
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Customer B Delivery",
    "description": "Handle delivery for Customer B",
    "priority": "MEDIUM",
    "startDate": "2024-01-15",
    "dueDate": "2024-01-18",
    "staffId": "staff2",
    "customerRef": "CUST_B"
  }'
```

#### Step 2: Reassign Customer A task (test bug fix)
```bash
curl -X POST "http://localhost:8080/api/tasks/assign-by-ref?customerRef=CUST_A&newStaffId=staff3"
```

#### Step 3: Add comments to Customer B task
```bash
# Get Customer B task ID first, then add comment
curl -X POST http://localhost:8080/api/tasks/{taskId}/comments \
  -H "Content-Type: application/json" \
  -d '{
    "comment": "Customer requested delivery time change",
    "userId": "manager1"
  }'
```

#### Step 4: Update priority of Customer B task
```bash
curl -X PUT http://localhost:8080/api/tasks/{taskId}/priority \
  -H "Content-Type: application/json" \
  -d '{
    "priority": "HIGH"
  }'
```

#### Step 5: Test all endpoints
```bash
# Get all tasks
curl http://localhost:8080/api/tasks/all

# Get tasks by date range (should exclude cancelled)
curl "http://localhost:8080/api/tasks?startDate=2024-01-15&endDate=2024-01-15"

# Get high priority tasks
curl http://localhost:8080/api/tasks/priority/HIGH

# Get task details with full activity history
curl http://localhost:8080/api/tasks/{taskId}
```

---

## 4. ERROR HANDLING TESTS

### Test Invalid Requests
```bash
# Test with invalid staff ID
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Invalid Staff Task",
    "description": "This should fail",
    "priority": "MEDIUM",
    "startDate": "2024-01-15",
    "dueDate": "2024-01-20",
    "staffId": "invalid_staff",
    "customerRef": "INVALID123"
  }'

# Test with invalid task ID
curl http://localhost:8080/api/tasks/invalid-task-id

# Test reassignment with non-existent customer reference
curl -X POST "http://localhost:8080/api/tasks/assign-by-ref?customerRef=NONEXISTENT&newStaffId=staff1"
```

---

## 5. EXPECTED RESULTS SUMMARY

### Bug Fixes Verified:
1. **Task Re-assignment**: Old tasks are cancelled, new tasks created
2. **Clean Task Lists**: Cancelled tasks excluded from date range queries

### New Features Verified:
1. **Smart Daily View**: Shows ongoing tasks + tasks within date range
2. **Priority Management**: Can create, update, and filter by priority
3. **Activity History**: All operations logged with timestamps and details
4. **Comments**: Can add comments and view them in activity history

### API Endpoints Tested:
- ✅ POST /api/tasks (Create task)
- ✅ GET /api/tasks (Date range with smart view)
- ✅ GET /api/tasks/{taskId} (Task details with activities)
- ✅ POST /api/tasks/assign-by-ref (Reassignment with bug fix)
- ✅ PUT /api/tasks/{taskId}/priority (Priority management)
- ✅ GET /api/tasks/priority/{priority} (Filter by priority)
- ✅ POST /api/tasks/{taskId}/comments (Add comments)
- ✅ GET /api/tasks/all (All tasks)
- ✅ DELETE /api/tasks/{taskId} (Delete task)

---

## Notes for Video Demo:
1. Start with bug fix demonstrations
2. Show before/after behavior for each bug
3. Demonstrate all new features with real examples
4. Show activity history and comments in action
5. Test error handling scenarios
6. Use different staff members and customer references
7. Show the smart daily view with tasks spanning multiple dates 