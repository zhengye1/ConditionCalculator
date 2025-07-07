import java.util.HashMap;

public class WRCRRule extends Rule{
    public WRCRRule() {
        this.patterns = new HashMap<>();
        this.patterns.put("1111", new double[]{30, 10, -10, -30});
        this.patterns.put("1120", new double[]{30, 10, -20, -20});
        this.patterns.put("1300", new double[]{30, -10, -10, -10});
        this.patterns.put("1201", new double[]{30, 0, 0, -30});
        this.patterns.put("2011", new double[]{20, 20, -10, -30});
        this.patterns.put("2020", new double[]{10, 10, -10, -10});
        this.patterns.put("3001", new double[]{10, 10, 10, -30});
        this.patterns.put("4000", new double[]{0, 0, 0, 0});
    }
}
