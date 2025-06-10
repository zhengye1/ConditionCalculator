import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConditionCalculation {
    // 子家荣和
    private final static int[] childRonArr = {1000, 1300, 1600, 2000, 2300,
            2600, 3200, 3900, 4500, 5200,
            6400, 7700, 8000, 12000, 16000,
            24000, 32000, 64000, 96000, 128000};
    // 亲家自摸（每家支付）
    private final static int[] dealerTsumoArr = {500, 700, 800, 1000, 1200,
            1300, 1600, 2000, 2300, 2600,
            3200, 3900, 4000, 6000, 8000,
            12000, 16000, 32000, 48000, 64000};
    // 亲家荣和
    private final static int[] dealerRonArr = {1500, 2000, 2400, 2900, 3400,
            3900, 4800, 5800, 6800, 7700, 9600,
            11600, 12000, 18000, 24000,
            36000, 48000, 96000, 144000, 192000};
    // 自摸分数表：子家自摸每家和亲家
    private final static int[][] childTsumoArr = {
            {300, 500}, {400, 700}, {400, 800}, {500, 1000}, {600, 1200},
            {700, 1300}, {800, 1600}, {1000, 2000}, {1200, 2300}, {1300, 2600},
            {1600, 3200}, {2000, 3900}, {2000, 4000}, {3000, 6000}, {4000, 8000},
            {6000, 12000}, {8000, 16000}, {16000, 32000}, {24000, 48000}, {32000, 64000}};

    /**
     * Calculate for this player winnning condition
     *
     * @param starting         - the score for each player have right now
     * @param currentScore     - Current hanchan score they have
     * @param playerIndex      - which player condition need to calculate
     * @param kyotaku          - Any kyotaku on the table right now?
     * @param honba            - Any honba?
     * @param winningCondition - Advanced by top? top 2?
     * @param rule             - Which rule we're using
     * @param player           - name of player
     * @param kiriageMangan    - enable kiriage Mangan?
     * @return winningCondition for this player
     */
    public static String calculateCondition(Double[] starting, Integer[] currentScore, int playerIndex, int kyotaku, int honba, int winningCondition, Rule rule, String[] player, boolean kiriageMangan) {
        StringBuilder results = new StringBuilder();
        int playerCount = currentScore.length;
        boolean isDealer = (playerIndex == 3); // 因为是all last条件，只有playerIndex3是亲家
        boolean resultFound = false;
        Integer[] newScore; // 没算一次都要重新copy一次值
        // 1. 自摸所有情况
        if (!isDealer) {
            for (int[] tsumoPair : childTsumoArr) {
                newScore = Arrays.copyOf(currentScore, playerCount);
                if (kiriageMangan && tsumoPair[0] == 2000 && tsumoPair[1] == 3900) continue;
                adjustScoreTsumo(newScore, playerIndex, tsumoPair, kyotaku, honba);
                int[] ranking = RankUtil.getRanking(newScore);
                String pattern = rule.getPattern(newScore);
                double[] uma = rule.getUma(pattern);
                double[] total = calcFinalScore(newScore, uma, starting, ranking);
                if (isQualified(total, playerIndex, winningCondition, starting)) {
                    results.append("自摸\t")
                            .append(getYakuName(tsumoPair[0], tsumoPair[1]))
                            .append("\n");
                    resultFound = true;
                    break;
                }
            }
        } else {
            for (int j : dealerTsumoArr) {
                if (kiriageMangan && j == 3900) continue;
                newScore = Arrays.copyOf(currentScore, playerCount);
                adjustScoreTsumo(newScore, playerIndex, j, kyotaku, honba);
                int[] ranking = RankUtil.getRanking(newScore);
                String pattern = rule.getPattern(newScore);
                double[] uma = rule.getUma(pattern);
                double[] total = calcFinalScore(newScore, uma, starting, ranking);
                if (isQualified(total, playerIndex, winningCondition, starting)) {
                    results.append("自摸\t")
                            .append(getYakuName(j, true, "tsumo"))
                            .append("\n");
                    resultFound = true;
                    break;
                }
            }
        }
        if (!resultFound) results.append("自摸\t无条件\n");


        // 2. 荣和所有情况
        for (int i = 0; i < playerCount; i++) {
            // reset it
            resultFound = false;
            if (i == playerIndex) continue;
            int[] ronArray = (!isDealer) ? childRonArr : dealerRonArr;
            for (int score : ronArray) {
                // 要是切上了就看是不是子家7700或者亲家 11600，是的话不用算
                if (kiriageMangan &&
                        (score == 7700 && !isDealer || (score == 11600 && isDealer))) continue;

                newScore = Arrays.copyOf(currentScore, playerCount);
                adjustScoreRon(newScore, i, playerIndex, score, kyotaku, honba);
                int[] ranking = RankUtil.getRanking(newScore);
                String pattern = rule.getPattern(newScore);
                double[] uma = rule.getUma(pattern);
                double[] total = calcFinalScore(newScore, uma, starting, ranking);
                if (isQualified(total, playerIndex, winningCondition, starting)) {
                    results.append(player[i])
                            .append("荣和")
                            .append("\t")
                            .append(getYakuName(score, isDealer, "ron"))
                            .append("\n");
                    resultFound = true;
                    break;
                }
            }
            if (!resultFound) results.append(player[i])
                    .append("\t荣和 无条件\n");

        }

        // 放铳情况
        for (int i = 0; i < playerCount; i++) {
            if (i == playerIndex) continue;
            // 看是不是点炮给亲家
            boolean dealToDealer = (i == 3);
            int[] ronArr = dealToDealer ? dealerRonArr : childRonArr;
            int maxSafeRon = -1;
            for (int ron : ronArr) {
                newScore = Arrays.copyOf(currentScore, playerCount);
                adjustScoreRon(newScore, playerIndex, i, ron, kyotaku, honba);
                int[] ranking = RankUtil.getRanking(newScore);
                String pattern = rule.getPattern(newScore);
                double[] uma = rule.getUma(pattern);
                double[] total = calcFinalScore(newScore, uma, starting, ranking);
                boolean qualified = isQualified(total, playerIndex, winningCondition, starting);
                if (!qualified) break;
                maxSafeRon = ron;
            }
            if (maxSafeRon > 0) results.append("对")
                    .append(player[i])
                    .append("最多放铳\t")
                    .append(getYakuName(maxSafeRon, dealToDealer, "ron"))
                    .append("\n");
        }


        return results.toString();
    }

    // 子家自摸（分数变动）
    static void adjustScoreTsumo(Integer[] currentScore, int playerId, int[] pay, int kyotaku, int honba) {
        int n = currentScore.length;

        // pay[0]: 子家每家支付, pay[1]: 亲家支付
        for (int i = 0; i < n; i++) {
            if (i == playerId) continue;
            if (i == 3) currentScore[i] -= (pay[1] + 100 * honba); // 假设3号为亲家
            else currentScore[i] -= (pay[0] + 100 * honba);
        }
        currentScore[playerId] += 2 * pay[0] + pay[1] + 300 * honba + kyotaku;
    }

    // 亲家自摸（分数变动）
    private static void adjustScoreTsumo(Integer[] currentScore, int playerId, int pay, int kyotaku, int honba) {
        int n = currentScore.length;

        // 亲家自摸, 每家都付一样
        for (int i = 0; i < n; i++) {
            if (i == playerId) continue;
            currentScore[i] -= (pay + 300 * honba);
        }
        currentScore[playerId] += pay * (n - 1) + 300 * honba + kyotaku;

    }

    // 荣和分数变动
    private static void adjustScoreRon(Integer[] currentScore, int loserId, int winnerId, int score, int kyotaku, int honba) {
        currentScore[loserId] -= (score + 300 * honba);
        currentScore[winnerId] += (score + 300 * honba + kyotaku);
        // 其他人不变
    }


    // 最终分
    private static double[] calcFinalScore(Integer[] score, double[] uma, Double[] starting, int[] ranking) {
        int n = score.length;
        double[] total = new double[n];
        for (int i = 0; i < n; i++) {
            total[i] = Math.round(((score[i] - 30000) / 1000.0 + uma[ranking[i] - 1] + starting[i]) * 10) / 10.0;
        }
        return total;
    }

    private static boolean isQualified(double[] total, int playerIndex, int winningCondition, Double[] starting) {
        int n = total.length;
        PlayerScore[] arr = new PlayerScore[n];
        for (int i = 0; i < n; i++) arr[i] = new PlayerScore(total[i], starting[i], i);

        // 排序：先按total降序，再按starting降序
        Arrays.sort(arr, (a, b) -> {
            int cmp = Double.compare(b.total, a.total);
            if (cmp != 0) return cmp;
            return Double.compare(b.starting, a.starting);
        });

        // 找晋级线
        PlayerScore thresholdPlayer = arr[winningCondition - 1];

        // 统计有多少人完全一样（total和starting全相等）
        int same = 0;
        for (PlayerScore ps : arr) {
            if (Math.abs(ps.total - thresholdPlayer.total) < 1e-6
                    && Math.abs(ps.starting - thresholdPlayer.starting) < 1e-6) {
                same++;
            }
        }

        PlayerScore current = null;
        for (PlayerScore ps : arr) {
            if (ps.index == playerIndex) {
                current = ps;
                break;
            }
        }
        if (current == null) return false;

        if (Math.abs(current.total - thresholdPlayer.total) < 1e-6
                && Math.abs(current.starting - thresholdPlayer.starting) < 1e-6) {
            // 同分同starting，只有独占才晋级
            return same == 1;
        } else if (current.total > thresholdPlayer.total) {
            return true;
        } else if (Math.abs(current.total - thresholdPlayer.total) < 1e-6 && current.starting > thresholdPlayer.starting) {
            // total同分但starting高，排名会被提前，不会出现在threshold上，已经在晋级线内
            return true;
        }
        return false;
    }

    private static String getYakuName(int x, int y) {
        if (x == 8000 && y == 16000) return "役满";
        if (x == 16000 && y == 32000) return "2倍役满";
        if (x == 24000 && y == 48000) return "3倍役满";
        if (x == 32000 && y == 64000) return "4倍役满";
        return x + "・" + y;
    }

    private static String getYakuName(int score, boolean dealer, String type) {
        if (dealer) {
            if ("tsumo".equals(type)) {
                if (score == 16000) return "役满";
                if (score == 32000) return "2倍役满";
                if (score == 48000) return "3倍役满";
                if (score == 64000) return "4倍役满";
                return score + "all";
            } else {
                if (score == 48000) return "役满";
                if (score == 96000) return "2倍役满";
                if (score == 144000) return "3倍役满";
                if (score == 192000) return "4倍役满";
                return score + "";
            }
        } else {
            if (score == 32000) return "役满";
            if (score == 64000) return "2倍役满";
            if (score == 96000) return "3倍役满";
            if (score == 128000) return "4倍役满";
            return score + "";

        }
    }

}
