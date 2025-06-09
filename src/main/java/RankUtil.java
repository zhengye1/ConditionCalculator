import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RankUtil {
    public static int[] getRanking(Integer[] scores){
        int n = scores.length;
        int[] order = new int[n];
        Integer[] temp = Arrays.copyOf(scores, n);
        Arrays.sort(temp, Collections.reverseOrder());
        Map<Integer, Integer> rankMap = new HashMap<>();
        int prevScore = Integer.MIN_VALUE;
        int rank = 1;
        for (int i = 0; i < n; i++) {
            if (!temp[i].equals(prevScore)) {
                rank = i + 1;
                prevScore = temp[i];
            }
            rankMap.put(temp[i], rank);
        }
        for (int i = 0; i < n; i++) {
            order[i] = rankMap.get(scores[i]);
        }
        return order;
    }

    public static int[] getRaking(Double[] scores){
        int n = scores.length;
        int[] order = new int[n];
        Double[] temp = Arrays.copyOf(scores, n);
        Arrays.sort(temp, Collections.reverseOrder());
        Map<Double, Integer> rankMap = new HashMap<>();
        int rank = 1;
        for (Double s : temp) {
            if (!rankMap.containsKey(s)) rankMap.put(s, rank++);
        }
        for (int i = 0; i < n; i++) {
            order[i] = rankMap.get(scores[i]);
        }
        return order;
    }
}
