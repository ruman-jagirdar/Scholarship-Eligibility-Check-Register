
import java.util.ArrayList;
import java.util.List;

public class EligibilityResult {

    private final boolean eligible;
    private final List<String> reasons;

    public EligibilityResult(List<String> reasons) {
        this.reasons = new ArrayList<>(reasons);
        this.eligible = reasons.isEmpty();
    }

    public boolean isEligible() {
        return eligible;
    }

    public List<String> getReasons() {
        return new ArrayList<>(reasons);
    }
}
