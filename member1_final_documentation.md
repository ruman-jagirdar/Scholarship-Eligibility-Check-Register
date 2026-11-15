# Member 1 â€“ Final Contribution Documentation

## Project Name
**Scholarship Eligibility Check Register**

## Engine
**Engine 1 â€“ Data & Integrity (DBMS)**

## 1. Introduction

My contribution to the Scholarship Eligibility Check Register project is focused on database management and data integrity. This engine manages the storage of student details, scholarship information, academic records, eligibility rules and checking results.

## 2. Objectives

- Design and create the MySQL database.
- Store student, scholarship and academic information.
- Establish relationships between database tables.
- Maintain data accuracy using database constraints.
- Connect the Java application to MySQL using JDBC.
- Store eligibility results and reasons.
- Maintain an audit log of eligibility checks.

## 3. Database Implementation

**Database Name:** `scholarship_register`

The database contains the following 10 tables:

| Table | Purpose |
|---|---|
| student | Stores student details. |
| scheme | Stores scholarship scheme information. |
| academic_record | Stores academic and financial details. |
| rule_value | Stores eligibility rule thresholds. |
| rule_reason | Stores explanations for failed rules. |
| eligibility_result | Stores eligibility results. |
| result_reason | Stores reasons for ineligibility. |
| registration | Stores scholarship registration details. |
| app_user | Stores user account information. |
| audit_log | Stores records of important activities. |

## 4. Data Integrity

The following database features were implemented:

- **Primary Keys:** Uniquely identify each record.
- **Foreign Keys:** Maintain relationships between tables.
- **UNIQUE Constraints:** Prevent duplicate records.
- **CHECK Constraints:** Restrict invalid values such as marks above 100.
- **NOT NULL Constraints:** Ensure required fields contain values.
- **InnoDB:** Supports foreign keys and transaction management.

## 5. Java and JDBC Implementation

Java Database Connectivity (JDBC) was used to connect the Java application with MySQL.

The database connection uses:

- Database: `scholarship_register`
- Username: `root`
- Port: `3306`

The Java implementation includes:

1. Testing the database connection.
2. Retrieving eligibility rule values.
3. Reading student academic records.
4. Checking eligibility against the five rules.
5. Saving eligibility results.
6. Storing reasons for failed rules.
7. Recording eligibility checks in the audit log.

## 6. Eligibility Checking

The application checks five eligibility rules:

| Rule | Condition |
|---|---|
| R1 | Minimum marks requirement |
| R2 | Family income limit |
| R3 | Minimum attendance requirement |
| R4 | No other active scholarship |
| R5 | Application before the deadline |

The result is stored as either `ELIGIBLE` or `NOT_ELIGIBLE`.

Reasons are stored separately for students who do not satisfy the requirements.

## 7. Transaction Management

Transaction management is used while saving eligibility-check information.

- `commit()` saves the database changes.
- `rollback()` reverses uncommitted changes if an error occurs.

This helps maintain consistency in the database.

## 8. Testing and Validation

The following tests were performed:

| Test | Expected Result |
|---|---|
| Valid student record | Accepted |
| Marks above 100 | Rejected |
| Duplicate academic record | Rejected |
| Unknown student USN in academic record | Rejected |
| Duplicate registration | Rejected |
| Valid eligibility check | Result stored |
| Failed eligibility check | Result and reasons stored |
| Database foreign-key relationships | Valid references maintained |

Three sample students were used to test eligibility checking:

- **TEST001:** ELIGIBLE
- **TEST002:** NOT_ELIGIBLE â€“ Marks below minimum.
- **TEST003:** NOT_ELIGIBLE â€“ Family income above limit and attendance below minimum.

## 9. Files Developed

The main files associated with my contribution include:

- `DBConnectionTest.java` â€“ Tests Java-MySQL connectivity.
- `RuleTest.java` â€“ Retrieves eligibility rule values.
- `EligibilityCheck.java` â€“ Performs eligibility checking and stores results.
- `database_documentation.md` â€“ Documents database tables and relationships.
- `testing_documentation.md` â€“ Records testing and validation.
- `logs/member1_work_log.md` â€“ Maintains the work and issue-resolution log.

## 10. Challenges and Solutions

| Challenge | Solution |
|---|---|
| Database connectivity | Configured JDBC connection and MySQL Connector/J. |
| Invalid academic records | Added foreign-key and CHECK constraints. |
| Duplicate records | Implemented UNIQUE constraints. |
| Maintaining result consistency | Used transactions and rollback. |
| Storing rejection reasons | Used separate `result_reason` records. |


## 11. Recent Integration Contribution

- Supported integration of the database components with the ScholarEase web application.
- Implemented scheme deletion checks to prevent deletion when related database records exist.
- Tested successful deletion of an unreferenced scheme and prevention of deletion when references exist.
- Updated database documentation and SQL query records.

## 12. Conclusion

I successfully implemented the database and data integrity components of the Scholarship Eligibility Check Register. The database stores the required information, maintains relationships between records and prevents invalid or duplicate data. Java and JDBC connect the application to MySQL, allowing eligibility results and their reasons to be stored systematically.
