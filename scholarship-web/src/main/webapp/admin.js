
let users = [];
let schemes = [];

async function loadUsers() {
    const table = document.getElementById("userTable");

    try {
        const response = await fetch("admin/users");

        if (!response.ok) {
            throw new Error("Unable to load users.");
        }

        users = await response.json();

        document.getElementById("userCount").textContent = users.length;

        renderUsers();

    } catch (error) {
        table.replaceChildren();

        const row = document.createElement("tr");
        const cell = document.createElement("td");

        cell.colSpan = 4;
        cell.textContent = "Unable to load user records.";

        row.appendChild(cell);
        table.appendChild(row);
    }
}

function renderUsers() {
    const search = document.getElementById("userSearch").value
        .trim().toLowerCase();

    const role = document.getElementById("roleFilter").value;

    const filteredUsers = users.filter(user => {
        const matchesSearch =
            user.username.toLowerCase().includes(search) ||
            String(user.id).includes(search);

        const displayRole =
            user.role.charAt(0) + user.role.slice(1).toLowerCase();

        const matchesRole = role === "All" || displayRole === role;

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

        const displayRole =
            user.role.charAt(0) + user.role.slice(1).toLowerCase();

        [user.id, user.username, displayRole, "Registered"].forEach(value => {
            const cell = document.createElement("td");
            cell.textContent = value;
            row.appendChild(cell);
        });

        table.appendChild(row);
    });
}

async function loadSchemes() {
    const table = document.getElementById("schemeTable");

    try {
        const response = await fetch("admin/schemes");

        if (!response.ok) {
            throw new Error("Unable to load schemes.");
        }

        schemes = await response.json();

        renderSchemes();
        updateSchemeCount();

    } catch (error) {
        table.replaceChildren();

        const row = document.createElement("tr");
        const cell = document.createElement("td");

        cell.colSpan = 4;
        cell.textContent = "Unable to load scholarship schemes.";

        row.appendChild(cell);
        table.appendChild(row);

        document.getElementById("schemeCount").textContent = "0";
    }
}

function renderSchemes() {
    const table = document.getElementById("schemeTable");
    table.replaceChildren();

    schemes.forEach(scheme => {
        const row = document.createElement("tr");

        [
            scheme.id,
            scheme.name,
            scheme.deadline
        ].forEach(value => {
            const cell = document.createElement("td");
            cell.textContent = value;
            row.appendChild(cell);
        });

        const actionCell = document.createElement("td");
        const deleteButton = document.createElement("button");

        deleteButton.textContent = "Remove";
        deleteButton.className = "delete-button";

        deleteButton.addEventListener("click", async () => {

            if (!confirm("Are you sure you want to remove this scholarship scheme?")) {
                return;
            }

            try {
                const response = await fetch(
                    "admin/schemes?id=" + encodeURIComponent(scheme.id),
                    { method: "DELETE" }
                );

                const result = await response.json();

                if (!response.ok) {
                    alert(result.message || "Unable to remove scheme.");
                    return;
                }

                alert(result.message);
                await loadSchemes();

            } catch (error) {
                alert("Unable to connect to the server.");
            }
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
    async function (event) {
        event.preventDefault();

        const name = document.getElementById("schemeName").value.trim();
        const academicYear = document.getElementById("academicYear").value.trim();
        const deadline = document.getElementById("schemeDeadline").value;

        if (!name || !academicYear || !deadline) {
            alert("Please fill in all fields.");
            return;
        }

        try {
            const response = await fetch("admin/schemes", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: new URLSearchParams({
                    name: name,
                    academicYear: academicYear,
                    deadline: deadline
                })
            });

            const result = await response.json();

            if (!response.ok) {
                alert(result.message || "Unable to add scholarship scheme.");
                return;
            }

            alert("Scholarship scheme added successfully!");

            document.getElementById("schemeForm").reset();

            await loadSchemes();

        } catch (error) {
            alert("Unable to connect to the server.");
        }
    }
);

loadUsers();
loadSchemes();
