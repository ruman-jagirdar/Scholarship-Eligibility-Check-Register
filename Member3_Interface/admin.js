const users = [
    { id: "USR001", name: "Ananya Sharma", role: "Student", status: "Active" },
    { id: "USR002", name: "Rahul Kumar", role: "Student", status: "Active" },
    { id: "USR003", name: "Priya Reddy", role: "Student", status: "Active" },
    { id: "USR004", name: "Arjun Patel", role: "Student", status: "Active" },
    { id: "USR005", name: "Meera Nair", role: "Student", status: "Active" },
    { id: "USR006", name: "Clerk One", role: "Clerk", status: "Active" },
    { id: "USR007", name: "Clerk Two", role: "Clerk", status: "Active" },
    { id: "USR008", name: "Administrator", role: "Admin", status: "Active" }
];

let schemes = [
    { id: "SCH001", name: "Merit Scholarship", deadline: "2026-12-31" },
    { id: "SCH002", name: "Need Based Scholarship", deadline: "2026-11-30" },
    { id: "SCH003", name: "Tech Education Grant", deadline: "2027-01-31" }
];

function renderUsers() {
    const search = document.getElementById("userSearch").value
        .trim().toLowerCase();

    const role = document.getElementById("roleFilter").value;

    const filteredUsers = users.filter(user => {
        const matchesSearch =
            user.name.toLowerCase().includes(search) ||
            user.id.toLowerCase().includes(search);

        const matchesRole = role === "All" || user.role === role;

        return matchesSearch && matchesRole;
    });

    const table = document.getElementById("userTable");
    table.replaceChildren();

    if (filteredUsers.length === 0) {
        const row = document.createElement("tr");
        const cell = document.createElement("td");

        cell.colSpan = 4;
        cell.textContent = "No matching users found.";

        row.appendChild(cell);
        table.appendChild(row);
        return;
    }

    filteredUsers.forEach(user => {
        const row = document.createElement("tr");

        [user.id, user.name, user.role, user.status].forEach(value => {
            const cell = document.createElement("td");
            cell.textContent = value;
            row.appendChild(cell);
        });

        table.appendChild(row);
    });
}

function renderSchemes() {
    const table = document.getElementById("schemeTable");
    table.replaceChildren();

    schemes.forEach(scheme => {
        const row = document.createElement("tr");

        [scheme.id, scheme.name, scheme.deadline].forEach(value => {
            const cell = document.createElement("td");
            cell.textContent = value;
            row.appendChild(cell);
        });

        const actionCell = document.createElement("td");
        const deleteButton = document.createElement("button");

        deleteButton.textContent = "Remove";
        deleteButton.className = "delete-button";

        deleteButton.addEventListener("click", () => {
            const confirmed = confirm(
                "Remove the sample scheme " + scheme.name + "?"
            );

            if (!confirmed) return;

            schemes = schemes.filter(item => item.id !== scheme.id);
            renderSchemes();
            updateSchemeCount();
        });

        actionCell.appendChild(deleteButton);
        row.appendChild(actionCell);
        table.appendChild(row);
    });

    if (schemes.length === 0) {
        const row = document.createElement("tr");
        const cell = document.createElement("td");

        cell.colSpan = 4;
        cell.textContent = "No scholarship schemes available.";

        row.appendChild(cell);
        table.appendChild(row);
    }
}

function updateSchemeCount() {
    document.getElementById("schemeCount").textContent = schemes.length;
}

document.getElementById("userSearch").addEventListener(
    "input",
    renderUsers
);

document.getElementById("roleFilter").addEventListener(
    "change",
    renderUsers
);

document.getElementById("schemeForm").addEventListener(
    "submit",
    function (event) {
        event.preventDefault();

        const nameInput = document.getElementById("schemeName");
        const deadlineInput = document.getElementById("schemeDeadline");

        const name = nameInput.value.trim();
        const deadline = deadlineInput.value;

        if (!name || !deadline) {
            alert("Please enter the scholarship name and deadline.");
            return;
        }

        if (schemes.some(
            scheme => scheme.name.toLowerCase() === name.toLowerCase()
        )) {
            alert("A scheme with this name already exists in the demo.");
            return;
        }

        const id = "SCH" + String(Date.now()).slice(-6);

        schemes.push({ id, name, deadline });

        renderSchemes();
        updateSchemeCount();

        nameInput.value = "";
        deadlineInput.value = "";

        alert(
            "Sample scheme added. It will not be saved to a database."
        );
    }
);

renderUsers();
renderSchemes();
updateSchemeCount();