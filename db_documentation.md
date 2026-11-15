# Database Documentation
## Scholarship Eligibility Check Register

**Engine:** Engine 1 – Data and Integrity  
**Database:** MySQL  
**Database Name:** scholarship_register

## 1. Introduction

The database stores and manages all information required by the Scholarship Eligibility Check Register project.

It stores student details, scholarship schemes, academic records, eligibility rules, eligibility results, registrations and audit logs.

## 2. Database Tables

The project contains 10 tables.

| No. | Table Name | Purpose |
|---|---|---|
| 1 | student | Stores student information. |
| 2 | scheme | Stores scholarship scheme details. |
| 3 | academic_record | Stores marks, attendance and family income. |
| 4 | rule_value | Stores scholarship eligibility rule values. |
| 5 | rule_reason | Stores descriptions of eligibility failure reasons. |
| 6 | eligibility_result | Stores eligibility checking results. |
| 7 | result_reason | Stores reasons for students being ineligible. |
| 8 | registration | Stores scholarship registration details. |
| 9 | app_user | Stores application user details and roles. |
| 10 | audit_log | Records important activities performed in the system. |

## 3. Database Constraints

The following constraints are used to maintain data integrity:

- **Primary Key:** Uniquely identifies each record.
- **Foreign Key:** Connects related tables and prevents invalid references.
- **Unique Constraint:** Prevents duplicate values.
- **Check Constraint:** Ensures values satisfy specified conditions.
- **NOT NULL:** Prevents required fields from being left empty.

## 4. Storage Engine

All project tables use the InnoDB storage engine.

InnoDB supports foreign keys and transactions, helping maintain database consistency.

## 5. Java and MySQL Connectivity

Java communicates with MySQL using JDBC (Java Database Connectivity).

The MySQL Connector/J library is used as the JDBC driver.

## 6. Transaction Management

Transactions are used when saving eligibility results, failure reasons and audit records.

- **commit():** Saves the database changes.
- **rollback():** Reverses uncommitted changes if an error occurs.

## 7. Conclusion

The database provides structured storage for the project and maintains data integrity through constraints, relationships and transaction management.

### Table 1: student

**Purpose:** Stores the basic information of students.

| Column | Data Type | Constraint | Description |
|---|---|---|---|
| usn | VARCHAR(20) | Primary Key, NOT NULL | Unique student ID |
| name | VARCHAR(100) | NOT NULL | Student name |
| course | VARCHAR(50) | NOT NULL | Course name |
| semester | TINYINT | NULL allowed | Current semester |
| category | VARCHAR(30) | NULL allowed | Student category |
| contact | VARCHAR(15) | NULL allowed | Contact number |

**Primary Key:** usn

**Importance:** Each student is uniquely identified using their USN. Other tables use this USN to connect academic records and registrations to the correct student.

### Table 2: academic_record

**Purpose:** Stores students' academic performance and financial information for scholarship eligibility checking.

| Column | Data Type | Constraint | Description |
|---|---|---|---|
| record_id | INT | Primary Key, AUTO_INCREMENT | Unique record ID |
| usn | VARCHAR(20) | Foreign Key, NOT NULL | Student identification number |
| academic_year | VARCHAR(9) | NOT NULL | Academic year |
| marks_pct | DECIMAL(5,2) | NOT NULL | Marks percentage |
| attendance_pct | DECIMAL(5,2) | NOT NULL | Attendance percentage |
| family_income | DECIMAL(12,2) | NOT NULL | Annual family income |
| active_scholarship_id | INT | Foreign Key, NULL allowed | Existing active scholarship ID |

**Primary Key:** record_id

**Foreign Keys:**
- usn references student(usn).
- active_scholarship_id references scheme(scheme_id).

**Unique Constraint:** The combination of usn and academic_year must be unique.

**Importance:** This table provides the academic and financial details used by the eligibility-checking engine.

### Table 3: scheme

**Purpose:** Stores scholarship scheme information and application deadlines.

| Column | Data Type | Constraint | Description |
|---|---|---|---|
| scheme_id | INT | Primary Key, AUTO_INCREMENT | Unique scheme ID |
| name | VARCHAR(100) | NOT NULL | Scholarship name |
| academic_year | VARCHAR(9) | NOT NULL | Academic year |
| deadline | DATE | NOT NULL | Last application date |

**Primary Key:** scheme_id

**Unique Constraints:**
- name and academic_year
- scheme_id and academic_year

**Importance:** This table stores scholarship details and deadlines used during eligibility checking.

### Table 4: rule_value

**Purpose:** Stores the eligibility rule codes and their corresponding threshold values for each scholarship scheme.

| Column | Data Type | Constraint | Description |
|---|---|---|---|
| rule_id | INT | Primary Key, AUTO_INCREMENT | Unique rule ID |
| scheme_id | INT | Foreign Key, NOT NULL | Scholarship scheme ID |
| rule_code | ENUM('R1','R2','R3','R4','R5') | NOT NULL | Eligibility rule identifier |
| limit_value | DECIMAL(12,2) | NULL allowed | Rule threshold value |
| effective_from | DATE | NULL allowed | Rule effective date |

**Primary Key:** rule_id

**Foreign Key:** scheme_id references scheme(scheme_id).

**Unique Constraint:** scheme_id and rule_code together must be unique.

**Importance:** This table allows the application to retrieve eligibility thresholds from the database instead of hardcoding them in Java.

### Table 5: rule_reason

**Purpose:** Stores the messages explaining why a student fails a particular eligibility rule.

| Column | Data Type | Constraint | Description |
|---|---|---|---|
| rule_code | VARCHAR(2) | Primary Key, NOT NULL | Unique rule identifier |
| message | VARCHAR(255) | NOT NULL | Explanation of the rule failure |

**Primary Key:** rule_code

**Importance:** This table stores predefined failure messages that can be displayed when a student does not satisfy an eligibility condition.

**Examples:**
- R1 – Marks below the minimum required.
- R2 – Family income above the limit.
- R3 – Attendance below the minimum required.
- R4 – Another scholarship is already active.
- R5 – Application made after the deadline.

### Table 6: eligibility_result

**Purpose:** Stores the final eligibility status of students for scholarship schemes.

| Column | Data Type | Constraint | Description |
|---|---|---|---|
| result_id | INT | Primary Key, AUTO_INCREMENT | Unique result ID |
| usn | VARCHAR(20) | Foreign Key, NOT NULL | Student identification number |
| scheme_id | INT | Foreign Key, NOT NULL | Scholarship scheme ID |
| checked_on | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Date and time of eligibility check |
| status | ENUM('ELIGIBLE','NOT_ELIGIBLE') | NOT NULL | Final eligibility status |

**Primary Key:** result_id

**Foreign Keys:**
- usn references student(usn).
- scheme_id references scheme(scheme_id).

**Importance:** This table stores the final result generated by the eligibility-checking process.

### Table 7: result_reason

**Purpose:** Stores the reasons associated with each student's eligibility result.

| Column | Data Type | Constraint | Description |
|---|---|---|---|
| result_id | INT | Foreign Key, Composite Primary Key | Eligibility result ID |
| rule_code | VARCHAR(2) | Foreign Key, Composite Primary Key | Failed eligibility rule code |

**Primary Key:** Composite primary key consisting of result_id and rule_code.

**Foreign Keys:**
- result_id references eligibility_result(result_id).
- rule_code references rule_reason(rule_code).

**Importance:** This table connects eligibility results with their corresponding failure reasons. For example, a student who fails marks and attendance requirements can have R1 and R3 recorded against the same result.

### Table 8: registration

**Purpose:** Stores scholarship registration details for students.

| Column | Data Type | Constraint | Description |
|---|---|---|---|
| registration_id | INT | Primary Key, AUTO_INCREMENT | Unique registration ID |
| usn | VARCHAR(20) | Foreign Key, NOT NULL | Student identification number |
| scheme_id | INT | Foreign Key, NOT NULL | Scholarship scheme ID |
| academic_year | VARCHAR(9) | Foreign Key, NOT NULL | Academic year |
| result_id | INT | Foreign Key, NOT NULL | Linked eligibility result ID |
| registered_on | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Registration date and time |

**Primary Key:** registration_id

**Foreign Keys:**
- usn references student(usn).
- (scheme_id, academic_year) references scheme(scheme_id, academic_year).
- result_id references eligibility_result(result_id).

**Unique Constraint:** usn, scheme_id and academic_year together must be unique.

**Importance:** This table maintains a record of student scholarship registrations and prevents duplicate registrations for the same scheme and academic year.

### Table 9: app_user

**Purpose:** Stores application user account details and their access roles.

| Column | Data Type | Constraint | Description |
|---|---|---|---|
| user_id | INT | Primary Key, AUTO_INCREMENT | Unique user ID |
| username | VARCHAR(50) | UNIQUE, NOT NULL | Login username |
| password_hash | VARCHAR(255) | NOT NULL | Hashed password |
| role | ENUM('STUDENT','CLERK','ADMIN') | NOT NULL | User role |

**Primary Key:** user_id

**Unique Constraint:** username must be unique.

**Importance:** This table provides the database structure for managing application users and their roles.

### Table 10: audit_log

**Purpose:** Maintains a record of important activities performed in the application.

| Column | Data Type | Constraint | Description |
|---|---|---|---|
| audit_id | INT | Primary Key, AUTO_INCREMENT | Unique audit record ID |
| user_id | INT | Foreign Key, NULL allowed | User who performed the action |
| action | VARCHAR(100) | NOT NULL | Action performed |
| details | TEXT | NULL allowed | Additional action details |
| at_time | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Date and time of action |

**Primary Key:** audit_id

**Foreign Key:** user_id references app_user(user_id).

**Importance:** This table maintains an activity history. For example, it records eligibility checks and scholarship registrations.

## 11. Database Relationships (Foreign Keys)

Foreign keys connect related tables and maintain referential integrity.

| Child Table | Foreign Key | Parent Table | Referenced Column |
|---|---|---|---|
| academic_record | active_scholarship_id | scheme | scheme_id |
| academic_record | usn | student | usn |
| audit_log | user_id | app_user | user_id |
| eligibility_result | scheme_id | scheme | scheme_id |
| eligibility_result | usn | student | usn |
| registration | result_id | eligibility_result | result_id |
| registration | scheme_id | scheme | scheme_id |
| registration | academic_year | scheme | academic_year |
| registration | usn | student | usn |
| result_reason | result_id | eligibility_result | result_id |
| result_reason | rule_code | rule_reason | rule_code |
| rule_value | scheme_id | scheme | scheme_id |

**Composite Foreign Key:** In the registration table, scheme_id and academic_year together reference scheme(scheme_id, academic_year).

**Importance:** These relationships prevent invalid references, such as creating an academic record for a student who does not exist in the student table.
## 12. Scheme Deletion Integrity

The application checks whether a scholarship scheme is referenced by other database records before allowing its deletion.

The following tables are checked:
- academic_record
- eligibility_result
- registration
- rule_value

If any related records exist, deletion is prevented to maintain referential integrity. A scheme with no references can be deleted successfully.

**Importance:** This prevents accidental deletion of scholarship schemes that are still being used by other records.

