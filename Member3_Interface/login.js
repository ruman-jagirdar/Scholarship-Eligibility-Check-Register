// ========================================
// ScholarEase Login Page JavaScript
// ========================================

// Get the login form elements
const loginForm = document.getElementById("loginForm");
const usernameInput = document.getElementById("username");
const passwordInput = document.getElementById("password");
const roleInput = document.getElementById("role");
const togglePassword = document.getElementById("togglePassword");

// ========================================
// 1. SHOW / HIDE PASSWORD
// ========================================

if (togglePassword && passwordInput) {
    togglePassword.addEventListener("click", function () {

        if (passwordInput.type === "password") {
            passwordInput.type = "text";
            togglePassword.textContent = "Hide";
        } else {
            passwordInput.type = "password";
            togglePassword.textContent = "Show";
        }

    });
}

// ========================================
// 2. LOGIN FORM VALIDATION
// ========================================

if (loginForm) {
    loginForm.addEventListener("submit", function (event) {

        // Prevent the form from submitting normally
        event.preventDefault();

        // Get the values entered by the user
        const username = usernameInput
            ? usernameInput.value.trim()
            : "";

        const password = passwordInput
            ? passwordInput.value
            : "";

        const role = roleInput
            ? roleInput.value
            : "";

        // Check whether all required fields are filled
        if (!username || !password || !role) {
            alert("Please fill in all login fields.");
            return;
        }

        // ========================================
        // 3. TEMPORARY DEMO LOGIN
        // ========================================

        // This stores demo information in the browser.
        // It does NOT verify the username or password.

        sessionStorage.setItem("demoUsername", username);
        sessionStorage.setItem("demoRole", role);

        // ========================================
        // 4. REDIRECT TO STUDENT DASHBOARD
        // ========================================

        window.location.href = "dashboard.html";

    });
}