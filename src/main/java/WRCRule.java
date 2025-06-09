import java.util.HashMap;
import java.util.regex.Pattern;

public class WRCRule extends Rule {
    public WRCRule() {
        this.patterns = new HashMap<>();
        this.patterns.put("1111", new double[]{15, 5, -5, -15});
        this.patterns.put("1120", new double[]{15, 5, -10, -10});
        this.patterns.put("1201", new double[]{15, 0, 0, -15});
        this.patterns.put("2011", new double[]{10, 10, -5, -15});
        this.patterns.put("2020", new double[]{10, 10, -10, -10});
        this.patterns.put("3001", new double[]{5, 5, 5, -15});
        this.patterns.put("4000", new double[]{0, 0, 0, 0});

    }
}
