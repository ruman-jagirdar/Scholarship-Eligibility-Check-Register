
import java.time.LocalDate;
import java.util.ArrayList;

public class EligibilityEngine {

    public static void main(String[] args) {

        // Test student
        double marks = 85.0;
        double familyIncome = 180000;
        double attendance = 90.0;
        boolean hasOtherActiveScholarship = false;




        

        LocalDate applicationDate = LocalDate.of(2026, 11, 5);
        LocalDate deadline = LocalDate.of(2026, 10, 30);

        // Call our reusable method
        EligibilityResult result = checkEligibility(
            marks,
            familyIncome,
            attendance,
            hasOtherActiveScholarship,
            applicationDate,
            deadline
        );

        // Display the result
        System.out.println("=== SCHOLARSHIP ELIGIBILITY RESULT ===");

       
if (result.isEligible()) {
    System.out.println("Status: ELIGIBLE");
} else {
    System.out.println("Status: NOT_ELIGIBLE");
    System.out.println("Reasons:");

    for (String reason : result.getReasons()) {
        System.out.println("- " + reason);
    }
}

    }

    // Checks all five eligibility rules
    public static EligibilityResult checkEligibility(
            double marks,
            double familyIncome,
            double attendance,
            boolean hasOtherActiveScholarship,
            LocalDate applicationDate,
            LocalDate deadline) {

        ArrayList<String> reasons = new ArrayList<>();

        if (marks < 60) {
            reasons.add("R1: Marks are below 60%");
        }

        if (familyIncome > 250000) {
            reasons.add("R2: Family income exceeds the limit");
        }

        if (attendance < 75) {
            reasons.add("R3: Attendance is below 75%");
        }

        if (hasOtherActiveScholarship) {
            reasons.add("R4: Another scholarship is active");
        }

        if (applicationDate.isAfter(deadline)) {
            reasons.add("R5: Application deadline has passed");
        }

        return new EligibilityResult(reasons);
    }
}
