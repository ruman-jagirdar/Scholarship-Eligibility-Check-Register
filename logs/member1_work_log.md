# Member 1 – Work Log

**Project:** Scholarship Eligibility Check Register  
**Role:** DBMS and Data Integrity

## Work Log

### Entry 1: MySQL Database Setup
- **Task:** Created the scholarship_register database and required tables.
- **Issue:** Foreign key relationships were not working because some tables used the MyISAM storage engine.
- **Solution:** Converted the tables to InnoDB and restored the foreign key constraints.
- **Status:** Completed.

### Entry 2: Database Integrity
- **Task:** Added primary keys, foreign keys, unique constraints and check constraints.
- **Issue:** An invalid student USN was initially accepted.
- **Solution:** Added the required foreign key constraint to ensure academic records reference existing students.
- **Status:** Completed.

### Entry 3: Duplicate Record Validation
- **Task:** Tested duplicate academic records and registrations.
- **Issue:** Duplicate records needed to be prevented.
- **Solution:** Added and tested unique constraints.
- **Status:** Completed.

### Entry 4: JDBC Connectivity
- **Task:** Connected Java with the MySQL database.
- **Issue:** Needed to configure the JDBC connector and database connection.
- **Solution:** Added the MySQL Connector/J JAR to the lib folder and configured the connection details.
- **Status:** Completed.

### Entry 5: Eligibility Rule Retrieval
- **Task:** Retrieved the five eligibility rule values from the database.
- **Issue:** Needed to verify that Java could read all rule values correctly.
- **Solution:** Used JDBC queries and tested the retrieval of R1 to R5.
- **Status:** Completed.

### Entry 6: Eligibility Result Storage
- **Task:** Saved eligibility results and failure reasons.
- **Issue:** Previous results needed to be updated without creating unnecessary duplicate results.
- **Solution:** Checked for an existing result and updated it when available. Stored failure reasons in result_reason.
- **Status:** Completed.

### Entry 7: Audit Logging
- **Task:** Recorded eligibility checks in the audit_log table.
- **Issue:** Eligibility-check activities needed to be recorded.
- **Solution:** Added audit log insertion to the Java eligibility-check process.
- **Status:** Completed.

### Entry 8: Transaction Management
- **Task:** Maintained database consistency while saving results, reasons and audit logs.
- **Issue:** Needed to prevent partial database updates if an operation failed.
- **Solution:** Used commit() and rollback() for transaction management.
- **Status:** Completed.

### Entry 9: Final Testing
- **Task:** Tested eligibility checking using three sample students.
- **Issue:** TEST003 initially produced an unexpected eligibility result.
- **Solution:** Reviewed the eligibility-checking logic and verified the expected results through testing.
- **Status:** Completed.
### Entry 10: Web Application Integration
- **Date:** 15 November 2026
- **Task:** Integrated the ScholarEase web application with the existing project.
- **Issue:** Required Java files were stored in an incorrect web application folder.
- **Solution:** Moved the required AuthFilter and SessionInfoServlet source files into the Maven Java source directory and removed outdated duplicate source files.
- **Status:** Completed. Maven build successful.

### Entry 11: Scheme Management and Data Integrity Testing
- **Date:** 15 November 2026
- **Task:** Tested adding and removing scholarship schemes through the admin dashboard.
- **Issue:** A scheme must not be deleted when other database records reference it.
- **Solution:** Added reference checks before deletion and tested both successful deletion and deletion prevention for referenced schemes.
- **Status:** Completed.

