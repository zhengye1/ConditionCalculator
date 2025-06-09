import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class ARuleTest {
    static Rule rule;

    @BeforeAll
    public static void setup() {
        rule = new JPMLRule();
    }

    @Test
    void testRankingAndPatternAndUma() {
        Integer[] scores = new Integer[]{36100, 35600, 29500, 28800};
        int[] ranking = RankUtil.getRanking(scores);
        assertArrayEquals(new int[] {1, 2, 3, 4}, ranking);
        String pattern = rule.getPattern(scores);
        assertEquals("21111", pattern);
        assertArrayEquals(new double[]{8, 4, -4, -8}, rule.getUma(pattern));
    }

    @Test
    void testPattern2() {
        Integer[] scores = new Integer[]{30000, 30000, 30000, 30000};
        int[] ranking = RankUtil.getRanking(scores);
        assertArrayEquals(new int[] {1, 1, 1, 1}, ranking);
        String pattern = rule.getPattern(scores);
        assertEquals("44000", pattern);
        assertArrayEquals(new double[]{0, 0, 0, 0}, rule.getUma(pattern));
    }
}
