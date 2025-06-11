import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MLeagueRuleTest {
    static Rule rule;

    @BeforeAll
    public static void setup() {
        rule = new MLeague();
    }

    @Test
    void test3FirstPlaceUma(){
        Integer[] scores = new Integer[]{26000, 26000, 26000, 22000};
        int[] ranking = RankUtil.getRanking(scores);
        assertArrayEquals(new int[] {1, 1, 1, 4}, ranking);
        String pattern = rule.getPattern(scores);
        assertEquals("3001", pattern);
        double[] uma = rule.getUma(pattern);
        assertArrayEquals(new double[]{20, 15, 15, -30}, uma);
        double[] finalScore = ConditionCalculation.calcFinalScore(scores, uma, new Double[]{0.0, 0.0, 0.0, 0.0},ranking);
        assertArrayEquals(new double[]{16.0, 11.0, 11.0, -38.0}, finalScore);
    }
}
