const registerForm = document.getElementById("registerForm");
const message = document.getElementById("message");

registerForm.addEventListener("submit", function (event) {
    event.preventDefault();

    const password = document.getElementById("password").value;
    const confirmPassword =
        document.getElementById("confirmPassword").value;

    if (password !== confirmPassword) {
        message.textContent = "Passwords do not match. Please try again.";
        message.style.color = "#c62828";
        document.getElementById("confirmPassword").focus();
        return;
    }

    message.textContent =
        "Form validation successful! Account creation will be enabled after database connection.";
    message.style.color = "#287a43";
});

registerForm.addEventListener("reset", function () {
    message.textContent = "";
});
