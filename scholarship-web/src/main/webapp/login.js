// ========================================
// ScholarEase Login Page JavaScript
// The server (LoginServlet) checks the username, password and role.
// This file only helps the user: show/hide password, empty-field check,
// and showing the error message sent back by the server.
// ========================================

const loginForm = document.getElementById("loginForm");
const usernameInput = document.getElementById("username");
const passwordInput = document.getElementById("password");
const roleInput = document.getElementById("role");
const togglePassword = document.getElementById("togglePassword");
const messageBox = document.getElementById("message");

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
// 2. SHOW THE ERROR SENT BY THE SERVER
// ========================================

const errorMessages = {
    missing: "Please fill in all login fields.",
    invalid: "Incorrect username, password or role. Please try again.",
    server: "The server had a problem. Please try again later.",
    login: "Please log in to continue."
};

function showMessage(text) {
    if (!messageBox) return;
    messageBox.textContent = text;
    messageBox.style.color = "#b52d58";
}

const errorCode = new URLSearchParams(window.location.search).get("error");
if (errorCode && errorMessages[errorCode]) {
    showMessage(errorMessages[errorCode]);
}

// ========================================
// 3. CHECK THE FORM BEFORE SENDING IT TO THE SERVER
// ========================================

if (loginForm) {
    loginForm.addEventListener("submit", function (event) {
        const username = usernameInput ? usernameInput.value.trim() : "";
        const password = passwordInput ? passwordInput.value : "";
        const role = roleInput ? roleInput.value : "";

        if (!username || !password || !role) {
            event.preventDefault();          // stop only when something is empty
            showMessage(errorMessages.missing);
        }
        // Otherwise the form is sent to the /login servlet.
        // Nothing is saved in the browser: the server keeps the session.
    });
}
