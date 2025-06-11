import java.util.HashMap;

public class JPMLRule extends Rule {
    // initialize the pattern in
    public JPMLRule() {
        this.patterns = new HashMap<>();
        this.patterns.put("01111", new double[]{8, 4, -4, -8});
        this.patterns.put("01120", new double[]{8, 4, -6, -6});
        this.patterns.put("02011", new double[]{6, 6, -4, -8});
        this.patterns.put("02020", new double[]{6, 6, -6, -6});
        this.patterns.put("01300", new double[]{8, -8/3.0, -8/3.0, -8/3.0}); // 得问清楚怎么分的....因为除不开....
        this.patterns.put("03001", new double[]{8/3.0, 8/3.0, 8/3.0, -4});
        this.patterns.put("04000", new double[]{0, 0, 0, 0});
        this.patterns.put("11111", new double[]{12, -1, -3, -8});
        this.patterns.put("11201", new double[]{12, -2, -2, -8});
        this.patterns.put("11120", new double[]{12, -1, -5.5, -5.5});
        this.patterns.put("11300", new double[]{12, -4, -4, -4});
        this.patterns.put("21111", new double[]{8, 4, -4, -8});
        this.patterns.put("21120", new double[]{8, 4, -6, -6});
        this.patterns.put("22011", new double[]{6, 6, -4, -8});
        this.patterns.put("22020", new double[]{6, 6, -6, -6});
        this.patterns.put("31111", new double[]{8, 3, 1, -12});
        this.patterns.put("32011", new double[]{5.5, 5.5, 1, -12});
        this.patterns.put("31201", new double[]{8, 2, 2, -12});
        this.patterns.put("33001", new double[]{4, 4, 4, -12});
        this.patterns.put("44000", new double[]{0, 0, 0, 0});
    }

    @Override
    public String getPattern(Integer[] score){
        // determine how many people above 30000
        int above = 0;
        for (int s : score){
            above += (s >= 30000)? 1 :0;
        }

        int[] getRanking = RankUtil.getRanking(score);
        int[] cnt = new int[score.length];
        for (int rank : getRanking){
            cnt[rank - 1]++;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(above);
        for (int c : cnt){
            sb.append(c);
        }
        return sb.toString();

    }
}
