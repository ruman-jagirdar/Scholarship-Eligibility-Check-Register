Member 3 – Work Log
Project: Scholarship Eligibility Check Register
Role: Interface and Access (Engine 3)
Name: Sahana Gudi
Branch: engine-3-interface | Folder: Member3_Interface/

Work Log

Entry 1: Git Branch Setup (14-11-2026)

* Task: Cloned the team repository and created the branch engine-3-interface from main.
* Issue: git clone failed with "You must specify a repository to clone".
* Solution: Ran git clone again with the full GitHub repository URL, then created and pushed the branch.
* Status: Completed.

Entry 2: Working in the Correct Folder (14-11-2026)

* Task: Checked and committed files using Git.
* Issue: git status showed "fatal: not a git repository".
* Solution: The terminal was in the outer folder, not in the cloned repository folder. Moved into Scholarship-Eligibility-Check-Register with cd, and opened VS Code on the repository folder.
* Status: Completed.

Entry 3: Interface Files Pushed (14-11-2026)

* Task: Added the 17 front-end files (HTML, CSS and JavaScript for the home, login, register, student, clerk and admin pages) to the Member3_Interface folder and pushed them.
* Issue: New files created for the Java code were not shown by git status. VS Code also gave "not a valid file or folder name" when creating a file.
* Solution: Created the files with PowerShell New-Item. They were created in the outer folder by mistake, so they were moved into the repository's Member3_Interface folder with Move-Item. git status then showed them.
* Status: Completed.

Entry 4: Login and Role-Based Access (14-11-2026)

* Task: Wrote LoginServlet, LogoutServlet, AuthFilter, UserDao and PasswordUtil.
* Issue: The first login.js only saved the role in the browser (sessionStorage) and did not check the password. Anyone could change it.
* Solution: Connected login.html to the /login Servlet. The server checks the password (stored hashed), creates a session and sends each role to its own page. AuthFilter blocks pages the role is not allowed to open.
* Status: Code written. Testing on Tomcat pending.

Entry 5: Logout Links and Session Information (14-11-2026)

* Task: Made Logout end the real session and showed the logged-in user on the student dashboard.
* Issue: Replace in Files changed the logout link only in admin.html.
* Solution: Changed the link on line 15 of clerk.html and dashboard.html by hand and checked with Select-String. Added SessionInfoServlet so the dashboard reads the user from the server session.
* Status: Completed.

Entry 6: Matching Member 1's Database (15-11-2026)

* Task: Connected the login code to the database.
* Issue: I had planned a separate users table, but Member 1's database already has app_user, which audit_log points to.
* Solution: Changed UserDao to use app_user. Wrote app_user_changes.sql to add a usn column so a student login is tied to one student record.
* Status: Completed. app_user_changes.sql still has to be run in MySQL.

Entry 7: Eligibility Check and Registration (15-11-2026)

* Task: Wrote EligibilityService, EligibilityServlet and RegisterServlet, and added the Check my eligibility and Register buttons to the student dashboard.
* Issue: Member 2's classes have no package line, so the Servlets cannot import them. Member 1's EligibilityCheck.java and Member 2's EligibilityEngine.java also both contain the five rules.
* Solution: The Servlets call Member 2's EligibilityEngine for the decision and read the limits from rule_value. Registration repeats the full check on the server and ignores anything sent by the browser. Asked Member 2 to add a package line. The duplicate rules will be removed during integration.
* Status: Code written. Testing with the sample students pending.