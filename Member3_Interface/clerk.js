const applications = [
    {
        id: "APP001",
        name: "Ananya Sharma",
        scholarship: "Merit Scholarship",
        eligibility: "Eligible",
        status: "Pending"
    },
    {
        id: "APP002",
        name: "Rahul Kumar",
        scholarship: "Need Based Scholarship",
        eligibility: "Eligible",
        status: "Approved"
    },
    {
        id: "APP003",
        name: "Priya Reddy",
        scholarship: "Women Empowerment Scholarship",
        eligibility: "Not Eligible",
        status: "Rejected"
    },
    {
        id: "APP004",
        name: "Arjun Patel",
        scholarship: "Tech Education Grant",
        eligibility: "Eligible",
        status: "Pending"
    },
    {
        id: "APP005",
        name: "Meera Nair",
        scholarship: "Merit Scholarship",
        eligibility: "Eligible",
        status: "Pending"
    }
];

let selectedApplicationId = null;

function renderApplications() {
    const table = document.getElementById("applicationTable");
    const search = document.getElementById("searchInput").value.toLowerCase();
    const filter = document.getElementById("statusFilter").value;

    const filtered = applications.filter(app => {
        const matchesSearch =
            app.name.toLowerCase().includes(search) ||
            app.scholarship.toLowerCase().includes(search) ||
            app.id.toLowerCase().includes(search);

        const matchesStatus = filter === "All" || app.status === filter;

        return matchesSearch && matchesStatus;
    });

    table.innerHTML = "";

    if (filtered.length === 0) {
        table.innerHTML = `
            <tr>
                <td colspan="6">No matching applications found.</td>
            </tr>`;
    } else {
        filtered.forEach(app => {
            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${app.id}</td>
                <td>${app.name}</td>
                <td>${app.scholarship}</td>
                <td>${app.eligibility}</td>
                <td>
                    <span class="status status-${app.status.toLowerCase()}">
                        ${app.status}
                    </span>
                </td>
                <td>
                    <button class="action-button"
                        onclick="reviewApplication('${app.id}')">
                        Review
                    </button>
                </td>`;

            table.appendChild(row);
        });
    }

    updateCounts();
}

function updateCounts() {
    document.getElementById("totalCount").textContent = applications.length;
    document.getElementById("pendingCount").textContent =
        applications.filter(app => app.status === "Pending").length;
    document.getElementById("approvedCount").textContent =
        applications.filter(app => app.status === "Approved").length;
    document.getElementById("rejectedCount").textContent =
        applications.filter(app => app.status === "Rejected").length;
}

function reviewApplication(id) {
    const app = applications.find(item => item.id === id);

    if (!app) return;

    selectedApplicationId = id;

    document.getElementById("reviewMessage").textContent =
        "Reviewing application " + app.id;

    document.getElementById("reviewDetails").innerHTML = `
        <p><strong>Application ID:</strong> ${app.id}</p>
        <p><strong>Student:</strong> ${app.name}</p>
        <p><strong>Scholarship:</strong> ${app.scholarship}</p>
        <p><strong>Eligibility:</strong> ${app.eligibility}</p>
        <p><strong>Current Status:</strong> ${app.status}</p>
        <p><strong>Note:</strong> These are fictional demonstration records.</p>
        <button class="approve-button"
            onclick="updateStatus('Approved')">
            Approve
        </button>
        <button class="reject-button"
            onclick="updateStatus('Rejected')">
            Reject
        </button>
    `;

    document.getElementById("review").scrollIntoView({
        behavior: "smooth"
    });
}

function updateStatus(newStatus) {
    const app = applications.find(
        item => item.id === selectedApplicationId
    );

    if (!app) return;

    if (app.eligibility !== "Eligible" && newStatus === "Approved") {
        alert("This sample record is marked not eligible. Review the eligibility rules before approving.");
        return;
    }

    const confirmed = confirm(
        `Change ${app.id} status to ${newStatus}?`
    );

    if (!confirmed) return;

    app.status = newStatus;

    renderApplications();
    reviewApplication(app.id);

    alert(
        "Demo status updated. This change exists only in the current page session and is not saved to a database."
    );
}

document.getElementById("searchInput").addEventListener(
    "input",
    renderApplications
);

document.getElementById("statusFilter").addEventListener(
    "change",
    renderApplications
);

renderApplications();