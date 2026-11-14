-- Login users table (Engine 3). Ask Member 1 to confirm / merge this into the main schema.
USE scholarship_register;

CREATE TABLE IF NOT EXISTS users (
    user_id        INT AUTO_INCREMENT PRIMARY KEY,
    username       VARCHAR(50)  NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,              -- PBKDF2 hash from PasswordUtil, never the plain password
    role           ENUM('STUDENT','CLERK','ADMIN') NOT NULL,
    usn            VARCHAR(20)  NULL,                  -- students only; ties the login to one student record
    active         TINYINT(1)   NOT NULL DEFAULT 1,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_student_usn CHECK (role <> 'STUDENT' OR usn IS NOT NULL)
    -- Optional: FOREIGN KEY (usn) REFERENCES student(usn)  -- use Member 1's real table/column names
);

-- Sample users. Replace each '<HASH_...>' with the output of:
--   java -cp bin com.scholarease.web.PasswordUtil admin123 clerk123 student123
INSERT INTO users (username, password_hash, role, usn) VALUES
    ('admin',    '<HASH_FOR_admin123>',   'ADMIN',   NULL),
    ('clerk1',   '<HASH_FOR_clerk123>',   'CLERK',   NULL),
    ('student1', '<HASH_FOR_student123>', 'STUDENT', '01FE24BCA001');
