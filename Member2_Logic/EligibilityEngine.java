
import java.time.LocalDate;
import java.util.ArrayList;

public class EligibilityEngine {

    public static EligibilityResult checkEligibility(
            double marks,
            double familyIncome,
            double attendance,
            boolean hasOtherActiveScholarship,
            LocalDate applicationDate,
            LocalDate deadline,
            double minimumMarks,
            double maximumIncome,
            double minimumAttendance) {

        ArrayList<String> reasons = new ArrayList<>();

        if (marks < minimumMarks) {
            reasons.add("R1");
        }

        if (familyIncome > maximumIncome) {
            reasons.add("R2");
        }

        if (attendance < minimumAttendance) {
            reasons.add("R3");
        }

        if (hasOtherActiveScholarship) {
            reasons.add("R4");
        }

        if (applicationDate.isAfter(deadline)) {
            reasons.add("R5");
        }

        return new EligibilityResult(reasons);
    }
}
