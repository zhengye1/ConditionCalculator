import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public abstract class Rule {
    public Map<String, double[]> patterns;

    public double[] getUma(String pattern) {
        return this.patterns.getOrDefault(pattern, new double[]{0, 0, 0, 0});
    }

    public String getPattern(Integer[] score) {
        int[] getRanking = RankUtil.getRanking(score);
        int[] cnt = new int[score.length];
        for (int rank : getRanking) {
            cnt[rank - 1]++;
        }
        StringBuilder sb = new StringBuilder();
        for (int c : cnt) {
            sb.append(c);
        }
        return sb.toString();
    }
}
