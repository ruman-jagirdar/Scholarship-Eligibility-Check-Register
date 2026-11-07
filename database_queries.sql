
-- ============================================================
-- SCHOLARSHIP ELIGIBILITY CHECK REGISTER
-- MEMBER 1: DATABASE & DATA INTEGRITY
-- File: database_queries.sql
-- Database: scholarship_register
-- ============================================================


-- ============================================================
-- 1. DATABASE CREATION
-- ============================================================

CREATE DATABASE IF NOT EXISTS scholarship_register;

USE scholarship_register;

SELECT DATABASE();

SHOW TABLES;


-- ============================================================
-- 2. CREATE TABLE QUERIES
-- ============================================================

-- 2.1 STUDENT TABLE

CREATE TABLE IF NOT EXISTS student (
    usn VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL,
    course VARCHAR(50) NOT NULL,
    semester TINYINT DEFAULT NULL,
    category VARCHAR(30) DEFAULT NULL,
    contact VARCHAR(15) DEFAULT NULL,
    PRIMARY KEY (usn)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- 2.2 SCHEME TABLE

CREATE TABLE IF NOT EXISTS scheme (
    scheme_id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    academic_year VARCHAR(9) NOT NULL,
    deadline DATE NOT NULL,
    PRIMARY KEY (scheme_id),
    UNIQUE KEY name (name, academic_year),
    UNIQUE KEY scheme_id (scheme_id, academic_year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- 2.3 ACADEMIC_RECORD TABLE

CREATE TABLE IF NOT EXISTS academic_record (
    record_id INT NOT NULL AUTO_INCREMENT,
    usn VARCHAR(20) NOT NULL,
    academic_year VARCHAR(9) NOT NULL,
    marks_pct DECIMAL(5,2) NOT NULL,
    attendance_pct DECIMAL(5,2) NOT NULL,
    family_income DECIMAL(12,2) NOT NULL,
    active_scholarship_id INT DEFAULT NULL,
    PRIMARY KEY (record_id),
    UNIQUE KEY usn (usn, academic_year),
    KEY fk_academic_scheme (active_scholarship_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- 2.4 RULE_REASON TABLE

CREATE TABLE IF NOT EXISTS rule_reason (
    rule_code VARCHAR(2) NOT NULL,
    message VARCHAR(255) NOT NULL,
    PRIMARY KEY (rule_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- 2.5 RULE_VALUE TABLE

CREATE TABLE IF NOT EXISTS rule_value (
    rule_id INT NOT NULL AUTO_INCREMENT,
    scheme_id INT NOT NULL,
    rule_code ENUM('R1','R2','R3','R4','R5') NOT NULL,
    limit_value DECIMAL(12,2) DEFAULT NULL,
    effective_from DATE DEFAULT NULL,
    PRIMARY KEY (rule_id),
    UNIQUE KEY scheme_id (scheme_id, rule_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- 2.6 APP_USER TABLE

CREATE TABLE IF NOT EXISTS app_user (
    user_id INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('STUDENT','CLERK','ADMIN') NOT NULL,
    PRIMARY KEY (user_id),
    UNIQUE KEY username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- 2.7 ELIGIBILITY_RESULT TABLE

CREATE TABLE IF NOT EXISTS eligibility_result (
    result_id INT NOT NULL AUTO_INCREMENT,
    usn VARCHAR(20) NOT NULL,
    scheme_id INT NOT NULL,
    checked_on DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status ENUM('ELIGIBLE','NOT_ELIGIBLE') NOT NULL,
    PRIMARY KEY (result_id),
    KEY fk_result_student (usn),
    KEY fk_result_scheme (scheme_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- 2.8 RESULT_REASON TABLE

CREATE TABLE IF NOT EXISTS result_reason (
    result_id INT NOT NULL,
    rule_code VARCHAR(2) NOT NULL,
    PRIMARY KEY (result_id, rule_code),
    KEY fk_reason_rule (rule_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- 2.9 REGISTRATION TABLE

CREATE TABLE IF NOT EXISTS registration (
    registration_id INT NOT NULL AUTO_INCREMENT,
    usn VARCHAR(20) NOT NULL,
    scheme_id INT NOT NULL,
    academic_year VARCHAR(9) NOT NULL,
    result_id INT NOT NULL,
    registered_on DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (registration_id),
    UNIQUE KEY usn (usn, scheme_id, academic_year),
    KEY fk_registration_scheme (scheme_id, academic_year),
    KEY fk_registration_result (result_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- 2.10 AUDIT_LOG TABLE

CREATE TABLE IF NOT EXISTS audit_log (
    audit_id INT NOT NULL AUTO_INCREMENT,
    user_id INT DEFAULT NULL,
    action VARCHAR(100) NOT NULL,
    details TEXT,
    at_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (audit_id),
    KEY fk_audit_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 3. INSERT SAMPLE DATA
-- ============================================================

-- 3.1 STUDENT DETAILS

INSERT INTO student
(usn, name, course, semester, category, contact)
VALUES
('TEST001', 'Student A', 'BCA', 5, 'General', '9000000001'),
('TEST002', 'Student B', 'BCA', 5, 'General', '9000000002'),
('TEST003', 'Student C', 'BCA', 5, 'General', '9000000003');


-- 3.2 SCHOLARSHIP SCHEME

INSERT INTO scheme
(scheme_id, name, academic_year, deadline)
VALUES
(1, 'Merit Scholarship', '2026-2027', '2026-12-31');


-- 3.3 ACADEMIC RECORDS

INSERT INTO academic_record
(record_id, usn, academic_year, marks_pct,
 attendance_pct, family_income, active_scholarship_id)
VALUES
(1, 'TEST001', '2026-2027', 72.00, 82.00, 180000.00, NULL),
(2, 'TEST002', '2026-2027', 59.90, 80.00, 240000.00, NULL),
(3, 'TEST003', '2026-2027', 65.00, 70.00, 260000.00, NULL);


-- 3.4 ELIGIBILITY RULE REASONS

INSERT INTO rule_reason (rule_code, message)
VALUES
('R1', 'Marks below the minimum required'),
('R2', 'Family income above the limit'),
('R3', 'Attendance below the minimum required'),
('R4', 'Another scholarship is already active'),
('R5', 'Application made after the deadline');


-- 3.5 ELIGIBILITY RULE VALUES

INSERT INTO rule_value
(rule_id, scheme_id, rule_code, limit_value, effective_from)
VALUES
(1, 1, 'R1', 60.00, '2026-01-01'),
(2, 1, 'R2', 250000.00, '2026-01-01'),
(3, 1, 'R3', 75.00, '2026-01-01'),
(4, 1, 'R4', 0.00, '2026-01-01'),
(5, 1, 'R5', 0.00, '2026-01-01');


-- 3.6 ELIGIBILITY RESULTS

INSERT INTO eligibility_result
(result_id, usn, scheme_id, checked_on, status)
VALUES
(1, 'TEST001', 1, '2026-10-26 13:23:02', 'ELIGIBLE'),
(2, 'TEST002', 1, '2026-10-26 13:23:02', 'NOT_ELIGIBLE'),
(3, 'TEST003', 1, '2026-10-26 13:23:02', 'NOT_ELIGIBLE');


-- 3.7 REASONS FOR INELIGIBILITY

INSERT INTO result_reason (result_id, rule_code)
VALUES
(2, 'R1'),
(3, 'R2'),
(3, 'R3');


-- 3.8 REGISTRATION

INSERT INTO registration
(registration_id, usn, scheme_id, academic_year,
 result_id, registered_on)
VALUES
(1, 'TEST001', 1, '2026-2027', 1, '2026-10-24 10:49:45');


-- 3.9 AUDIT LOG

INSERT INTO audit_log
(audit_id, user_id, action, details, at_time)
VALUES
(1, NULL, 'REGISTRATION_CREATED',
 'TEST001 registered for Merit Scholarship, academic year 2026-2027',
 '2026-10-24 10:54:07'),

(2, NULL, 'ELIGIBILITY_CHECKED',
 'TEST001 eligibility checked. Status: ELIGIBLE',
 '2026-10-26 13:23:02'),

(3, NULL, 'ELIGIBILITY_CHECKED',
 'TEST002 eligibility checked. Status: NOT_ELIGIBLE',
 '2026-10-26 13:23:02'),

(4, NULL, 'ELIGIBILITY_CHECKED',
 'TEST003 eligibility checked. Status: NOT_ELIGIBLE',
 '2026-10-26 13:23:02');


-- ============================================================
-- 4. ALTER TABLE QUERIES - FOREIGN KEY CONSTRAINTS
-- ============================================================

ALTER TABLE academic_record
ADD CONSTRAINT fk_academic_scheme
FOREIGN KEY (active_scholarship_id)
REFERENCES scheme (scheme_id);

ALTER TABLE academic_record
ADD CONSTRAINT fk_academic_student
FOREIGN KEY (usn)
REFERENCES student (usn)
ON DELETE RESTRICT ON UPDATE CASCADE;


ALTER TABLE audit_log
ADD CONSTRAINT fk_audit_user
FOREIGN KEY (user_id)
REFERENCES app_user (user_id);


ALTER TABLE eligibility_result
ADD CONSTRAINT fk_result_scheme
FOREIGN KEY (scheme_id)
REFERENCES scheme (scheme_id);

ALTER TABLE eligibility_result
ADD CONSTRAINT fk_result_student
FOREIGN KEY (usn)
REFERENCES student (usn);


ALTER TABLE registration
ADD CONSTRAINT fk_registration_result
FOREIGN KEY (result_id)
REFERENCES eligibility_result (result_id);

ALTER TABLE registration
ADD CONSTRAINT fk_registration_scheme
FOREIGN KEY (scheme_id, academic_year)
REFERENCES scheme (scheme_id, academic_year);

ALTER TABLE registration
ADD CONSTRAINT fk_registration_student
FOREIGN KEY (usn)
REFERENCES student (usn);


ALTER TABLE result_reason
ADD CONSTRAINT fk_reason_result
FOREIGN KEY (result_id)
REFERENCES eligibility_result (result_id);

ALTER TABLE result_reason
ADD CONSTRAINT fk_reason_rule
FOREIGN KEY (rule_code)
REFERENCES rule_reason (rule_code);


ALTER TABLE rule_value
ADD CONSTRAINT fk_rule_scheme
FOREIGN KEY (scheme_id)
REFERENCES scheme (scheme_id);


-- ============================================================
-- 5. SELECT QUERIES - VIEW DATABASE INFORMATION
-- ============================================================

-- Display all tables

SHOW TABLES;


-- Display student records

SELECT * FROM student;


-- Display scholarship schemes

SELECT * FROM scheme;


-- Display academic records

SELECT * FROM academic_record;


-- Display eligibility rules

SELECT * FROM rule_value;


-- Display rule failure messages

SELECT * FROM rule_reason;


-- Display eligibility results

SELECT * FROM eligibility_result;


-- Display reasons for ineligibility

SELECT * FROM result_reason;


-- Display registrations

SELECT * FROM registration;


-- Display application users

SELECT * FROM app_user;


-- Display audit logs

SELECT * FROM audit_log;


-- ============================================================
-- 6. JOIN QUERIES
-- ============================================================

-- Display student details with academic records

SELECT s.usn, s.name, a.academic_year,
       a.marks_pct, a.attendance_pct, a.family_income
FROM student s
JOIN academic_record a ON s.usn = a.usn;


-- Display eligibility results with student names

SELECT s.usn, s.name, e.scheme_id,
       e.status, e.checked_on
FROM student s
JOIN eligibility_result e ON s.usn = e.usn;


-- Display registration details

SELECT r.registration_id, s.name,
       sc.name AS scholarship_name,
       r.academic_year, r.registered_on
FROM registration r
JOIN student s ON r.usn = s.usn
JOIN scheme sc ON r.scheme_id = sc.scheme_id;


-- Display reasons for students who are not eligible

SELECT e.usn, rr.rule_code, rr.message
FROM eligibility_result e
JOIN result_reason r ON e.result_id = r.result_id
JOIN rule_reason rr ON r.rule_code = rr.rule_code;


-- ============================================================
-- 7. DATABASE STRUCTURE VERIFICATION
-- ============================================================

-- Check table structures

DESCRIBE student;
DESCRIBE scheme;
DESCRIBE academic_record;
DESCRIBE rule_reason;
DESCRIBE rule_value;
DESCRIBE app_user;
DESCRIBE eligibility_result;
DESCRIBE result_reason;
DESCRIBE registration;
DESCRIBE audit_log;


-- Check storage engines

SELECT TABLE_NAME, ENGINE
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'scholarship_register';


-- Check foreign key relationships

SELECT
    TABLE_NAME,
    COLUMN_NAME,
    CONSTRAINT_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'scholarship_register'
AND REFERENCED_TABLE_NAME IS NOT NULL;


-- Check indexes

SHOW INDEX FROM registration;
SHOW INDEX FROM rule_value;
SHOW INDEX FROM academic_record;


-- ============================================================
-- 8. DATA INTEGRITY VERIFICATION QUERIES
-- ============================================================

-- Check for duplicate academic records

SELECT usn, academic_year, COUNT(*)
FROM academic_record
GROUP BY usn, academic_year
HAVING COUNT(*) > 1;


-- Check for duplicate registrations

SELECT usn, scheme_id, academic_year, COUNT(*)
FROM registration
GROUP BY usn, scheme_id, academic_year
HAVING COUNT(*) > 1;


-- Check for registrations with invalid student references

SELECT r.*
FROM registration r
LEFT JOIN student s ON r.usn = s.usn
WHERE s.usn IS NULL;


-- Check for registrations with invalid result references

SELECT r.*
FROM registration r
LEFT JOIN eligibility_result e ON r.result_id = e.result_id
WHERE e.result_id IS NULL;


-- ============================================================
-- 9. UPDATE AND DELETE - REFERENCE QUERIES
-- These are examples for future maintenance.
-- They are not claimed as previously executed queries.
-- ============================================================

-- Example UPDATE: Change a student's contact number
-- UPDATE student
-- SET contact = '9000000000'
-- WHERE usn = 'TEST001';


-- Example UPDATE: Change a scholarship deadline
-- UPDATE scheme
-- SET deadline = '2027-01-15'
-- WHERE scheme_id = 1;


-- Example DELETE: Delete an academic record
-- DELETE FROM academic_record
-- WHERE usn = 'TEST003'
-- AND academic_year = '2026-2027';


-- Example DELETE: Delete a student
-- This may be blocked by foreign key constraints.
-- DELETE FROM student
-- WHERE usn = 'TEST003';


-- ============================================================
-- 10. DAILY QUERY LOG
-- Add new SQL queries below this section whenever
-- database changes or maintenance are performed.
-- Mention the date and purpose of each new query.
-- ============================================================

-- Date:
-- Purpose:
-- Query:


-- ============================================================
-- END OF DATABASE QUERY FILE
-- ============================================================
