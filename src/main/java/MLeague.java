import java.util.HashMap;

public class MLeague extends Rule {
    public MLeague() {
        this.patterns = new HashMap<>();
        this.patterns.put("1111", new double[]{50, 10, -10, -30});
        this.patterns.put("1201", new double[]{50, 0, 0, -30});
        this.patterns.put("1120", new double[]{50, 10, -20, -20});
        this.patterns.put("2011", new double[]{30, 30, -10, -30});
        this.patterns.put("2020", new double[]{30, 30, -20, -20});
        this.patterns.put("3001", new double[]{20, 15, 15, -30});
        this.patterns.put("4000", new double[]{5, 5, 5, 5});
    }

}
