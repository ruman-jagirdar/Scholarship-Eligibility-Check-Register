const eligibilityForm = document.getElementById("eligibilityForm");
const result = document.getElementById("result");

eligibilityForm.addEventListener("submit", function (event) {
    event.preventDefault();

    // Get values from the form
    const studentName = document.getElementById("studentName").value.trim();
    const marks = Number(document.getElementById("marks").value);
    const income = Number(document.getElementById("income").value);
    const attendance = Number(document.getElementById("attendance").value);
    const otherScholarship =
        document.getElementById("otherScholarship").value;

    // Clear any previous result
    result.textContent = "";
    result.style.color = "";

    // Basic validation
    if (
        studentName === "" ||
        document.getElementById("marks").value === "" ||
        document.getElementById("income").value === "" ||
        document.getElementById("attendance").value === "" ||
        otherScholarship === ""
    ) {
        result.textContent = "Please complete all fields.";
        result.style.color = "#c62828";
        return;
    }

    if (
        !Number.isFinite(marks) ||
        marks < 0 || marks > 100 ||
        !Number.isFinite(income) ||
        income < 0 ||
        !Number.isFinite(attendance) ||
        attendance < 0 || attendance > 100
    ) {
        result.textContent = "Please enter valid values in all fields.";
        result.style.color = "#c62828";
        return;
    }

    // Check the project's example eligibility rules
    const reasons = [];

    if (marks < 60) {
        reasons.push("Academic marks must be at least 60%.");
    }

    if (income > 250000) {
        reasons.push("Annual family income must not exceed ₹2,50,000.");
    }

    if (attendance < 75) {
        reasons.push("Attendance must be at least 75%.");
    }

    if (otherScholarship === "yes") {
        reasons.push("You must not receive another active scholarship.");
    }

    // Display the result
    if (reasons.length === 0) {
        result.textContent =
            "You meet all the example criteria, " +
            studentName +
            "! This is not an official approval.";
        result.style.color = "#287a43";
    } else {
        result.textContent =
            "You do not meet the example criteria: " +
            reasons.join(" ");
        result.style.color = "#c62828";
    }
});

// Clear the displayed result when the form is reset
eligibilityForm.addEventListener("reset", function () {
    result.textContent = "";
    result.style.color = "";
});
