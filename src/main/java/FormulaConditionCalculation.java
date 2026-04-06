import java.util.*;

public class FormulaConditionCalculation {
    // 子家荣和
    private static final int[] childRonArr = {1000, 1300, 1600, 2000, 2300, 2600, 2900, 3200, 3900, 4500, 5200, 5800, 6400, 7100, 7700, 8000, 12000, 16000, 24000, 32000, 64000, 96000, 128000};
    // 亲家自摸（每家支付）
    private static final int[] dealerTsumoArr = {500, 700, 800, 1000, 1200, 1300, 1500, 1600, 2000, 2300, 2600, 2900, 3200, 3600, 3900, 4000, 6000, 8000, 12000, 16000, 32000, 48000, 64000};
    // 亲家荣和
    private static final int[] dealerRonArr = {1500, 2000, 2400, 2900, 3400, 3900, 4400, 4800, 5800, 6800, 7700, 8700, 9600, 10600, 11600, 12000, 18000, 24000, 36000, 48000, 96000, 144000, 192000};
    // 子家自摸
    private static final int[][] childTsumoArr = {{300, 500}, {400, 700}, {400, 800}, {500, 1000}, {600, 1200}, {700, 1300}, {800, 1500}, {800, 1600}, {1000, 2000}, {1200, 2300}, {1300, 2600}, {1500, 2900}, {1600, 3200}, {1800, 3600}, {2000, 3900}, {2000, 4000}, {3000, 6000}, {4000, 8000}, {6000, 12000}, {8000, 16000}, {16000, 32000}, {24000, 48000}, {32000, 64000}};

    private static final int RETURN_POINT = 25000;

    public static String calculateCondition(Double[] starting, Integer[] currentScore, int playerIndex, int kyotaku, int honba, int winningCondition, Rule rule, String[] player, int dealerIndex, int[] firstHanchanRank) {
        StringBuilder results = new StringBuilder();
        int playerCount = currentScore.length;
        boolean isDealer = (playerIndex == dealerIndex);
        Integer[] newScore;
        List<Boolean> resultList = new ArrayList<>();
        List<int[]> tsumoPairs = new ArrayList<>();
        List<Integer> tsumoDealer = new ArrayList<>();

        // 1. 自摸
        if (!isDealer) {
            for (int[] tsumoPair : childTsumoArr) {
                if (tsumoPair[0] == 2000 && tsumoPair[1] == 3900) continue; // 切上满贯
                newScore = Arrays.copyOf(currentScore, playerCount);
                tsumoPairs.add(tsumoPair);
                adjustScoreTsumo(newScore, playerIndex, tsumoPair, kyotaku, honba, dealerIndex);
                double[] total = calcWithUma(newScore, rule, starting);
                resultList.add(isQualified(total, playerIndex, winningCondition, firstHanchanRank, newScore));
            }
        } else {
            for (int pay : dealerTsumoArr) {
                if (pay == 3900) continue; // 切上满贯
                tsumoDealer.add(pay);
                newScore = Arrays.copyOf(currentScore, playerCount);
                adjustScoreTsumo(newScore, playerIndex, pay, kyotaku, honba);
                double[] total = calcWithUma(newScore, rule, starting);
                resultList.add(isQualified(total, playerIndex, winningCondition, firstHanchanRank, newScore));
            }
        }
        if (resultList.stream().noneMatch(x -> x)) {
            results.append("自摸\t无条件\n");
        } else {
            List<?> tsumoList = isDealer ? tsumoDealer : tsumoPairs;
            boolean last = false;
            int start = -1;
            for (int i = 0; i < resultList.size(); i++) {
                boolean curr = resultList.get(i);
                if (!last && curr) start = i;
                if (last && !curr) {
                    printTsumoRange(results, tsumoList, start, i - 1, isDealer);
                    start = -1;
                }
                last = curr;
            }
            if (last) {
                printTsumoRange(results, tsumoList, start, resultList.size() - 1, isDealer);
            }
        }
        results.append("=============================\n");

        // 2. 荣和
        for (int i = 0; i < playerCount; i++) {
            resultList.clear();
            List<Integer> ronList = new ArrayList<>();
            if (i == playerIndex) continue;
            int[] ronArray = (!isDealer) ? childRonArr : dealerRonArr;
            for (int score : ronArray) {
                if (score == 7700 && !isDealer || (score == 11600 && isDealer)) continue; // 切上满贯
                ronList.add(score);
                newScore = Arrays.copyOf(currentScore, playerCount);
                adjustScoreRon(newScore, i, playerIndex, score, kyotaku, honba);
                double[] total = calcWithUma(newScore, rule, starting);
                resultList.add(isQualified(total, playerIndex, winningCondition, firstHanchanRank, newScore));
            }
            if (resultList.stream().noneMatch(x -> x)) {
                results.append(player[i]).append("\t荣和\t无条件\n");
            } else {
                boolean last = false;
                int start = -1;
                for (int j = 0; j < resultList.size(); j++) {
                    boolean curr = resultList.get(j);
                    if (!last && curr) start = j;
                    if (last && !curr) {
                        printRonRange(results, ronList, start, j - 1, isDealer, player[i]);
                        start = -1;
                    }
                    last = curr;
                }
                if (last) {
                    printRonRange(results, ronList, start, resultList.size() - 1, isDealer, player[i]);
                }
            }
        }

        if (winningCondition != 1) {
            results.append("=============================\n");
            // 3. 放铳
            for (int i = 0; i < playerCount; i++) {
                resultList.clear();
                List<Integer> ronList = new ArrayList<>();
                if (i == playerIndex) continue;
                boolean dealToDealer = (i == dealerIndex);
                int[] ronArr = dealToDealer ? dealerRonArr : childRonArr;
                for (int ron : ronArr) {
                    if (ron == 7700 && !dealToDealer || (ron == 11600 && dealToDealer)) continue;
                    newScore = Arrays.copyOf(currentScore, playerCount);
                    ronList.add(ron);
                    adjustScoreRon(newScore, playerIndex, i, ron, kyotaku, honba);
                    double[] total = calcWithUma(newScore, rule, starting);
                    resultList.add(isQualified(total, playerIndex, winningCondition, firstHanchanRank, newScore));
                }
                if (resultList.stream().anyMatch(x -> x)) {
                    boolean last = false;
                    int start = -1;
                    for (int j = 0; j < resultList.size(); j++) {
                        boolean curr = resultList.get(j);
                        if (!last && curr) start = j;
                        if (last && !curr) {
                            printDealinRange(results, ronList, start, j - 1, isDealer, player[i]);
                            start = -1;
                        }
                        last = curr;
                    }
                    if (last) {
                        printDealinRange(results, ronList, start, resultList.size() - 1, isDealer, player[i]);
                    }
                }
            }
        }

        results.append("=============================\n");
        // 4. 被自摸
        for (int other = 0; other < playerCount; other++) {
            if (other == playerIndex) continue;
            boolean otherIsDealer = (other == dealerIndex);
            List<Boolean> beTsumoResultList = new ArrayList<>();
            List<int[]> beTsumoChildPairs = new ArrayList<>();
            List<Integer> beTsumoDealerArr = new ArrayList<>();

            if (!otherIsDealer) {
                for (int[] tsumoPair : childTsumoArr) {
                    if (tsumoPair[0] == 2000 && tsumoPair[1] == 3900) continue;
                    newScore = Arrays.copyOf(currentScore, playerCount);
                    beTsumoChildPairs.add(tsumoPair);
                    adjustScoreTsumo(newScore, other, tsumoPair, kyotaku, honba, dealerIndex);
                    double[] total = calcWithUma(newScore, rule, starting);
                    beTsumoResultList.add(isQualified(total, playerIndex, winningCondition, firstHanchanRank, newScore));
                }
                if (beTsumoResultList.stream().anyMatch(x -> x)) {
                    boolean last = false;
                    int start = -1;
                    for (int i = 0; i < beTsumoResultList.size(); i++) {
                        boolean curr = beTsumoResultList.get(i);
                        if (!last && curr) start = i;
                        if (last && !curr) {
                            printBeTsumoRange(results, beTsumoChildPairs, start, i - 1, false, player[other]);
                            start = -1;
                        }
                        last = curr;
                    }
                    if (last) {
                        printBeTsumoRange(results, beTsumoChildPairs, start, beTsumoResultList.size() - 1, false, player[other]);
                    }
                }
            } else {
                for (int score : dealerTsumoArr) {
                    if (score == 3900) continue;
                    newScore = Arrays.copyOf(currentScore, playerCount);
                    beTsumoDealerArr.add(score);
                    adjustScoreTsumo(newScore, other, score, kyotaku, honba);
                    double[] total = calcWithUma(newScore, rule, starting);
                    beTsumoResultList.add(isQualified(total, playerIndex, winningCondition, firstHanchanRank, newScore));
                }
                if (beTsumoResultList.stream().anyMatch(x -> x)) {
                    boolean last = false;
                    int start = -1;
                    for (int i = 0; i < beTsumoResultList.size(); i++) {
                        boolean curr = beTsumoResultList.get(i);
                        if (!last && curr) start = i;
                        if (last && !curr) {
                            printBeTsumoRange(results, beTsumoDealerArr, start, i - 1, true, player[other]);
                            start = -1;
                        }
                        last = curr;
                    }
                    if (last) {
                        printBeTsumoRange(results, beTsumoDealerArr, start, beTsumoResultList.size() - 1, true, player[other]);
                    }
                }
            }
        }

        return results.toString();
    }

    public static String calcDangerByOthersDealIn(Double[] starting, Integer[] currentScore, int myIdx, int kyotaku, int honba, int winningCondition, Rule rule, String[] playerNames, int dealerIndex, int[] firstHanchanRank) {
        int n = currentScore.length;
        StringBuilder result = new StringBuilder();

        for (int loser = 0; loser < n; loser++) {
            if (loser == myIdx) continue;
            for (int winner = 0; winner < n; winner++) {
                if (winner == myIdx || winner == loser) continue;
                boolean winnerIsDealer = (winner == dealerIndex);
                int[] ronArr = winnerIsDealer ? dealerRonArr : childRonArr;
                List<Boolean> loseDanger = new ArrayList<>();

                for (int ron : ronArr) {
                    if ((!winnerIsDealer && ron == 7700) || (winnerIsDealer && ron == 11600)) continue;
                    Integer[] tempScore = Arrays.copyOf(currentScore, n);
                    adjustScoreRon(tempScore, loser, winner, ron, kyotaku, honba);
                    double[] total = calcWithUma(tempScore, rule, starting);
                    boolean qualified = isQualified(total, myIdx, winningCondition, firstHanchanRank, tempScore);
                    loseDanger.add(!qualified);
                }

                if (loseDanger.stream().anyMatch(x -> x)) {
                    boolean last = false;
                    int start = -1;
                    for (int j = 0; j < loseDanger.size(); j++) {
                        boolean curr = loseDanger.get(j);
                        if (!last && curr) start = j;
                        if (last && !curr) {
                            printDealInDangerRange(result, ronArr, start, j - 1, winnerIsDealer, playerNames[loser], playerNames[winner]);
                            start = -1;
                        }
                        last = curr;
                    }
                    if (last) {
                        printDealInDangerRange(result, ronArr, start, loseDanger.size() - 1, winnerIsDealer, playerNames[loser], playerNames[winner]);
                    }
                }
            }
        }
        return result.toString();
    }

    /**
     * 公式战专用：计算最终分数
     * 同分 uma 分配基于 currentScore 排名，currentScore 也一样则按起家座位（index 小优先）
     */
    static double[] calcFinalScore(Integer[] score, double[] uma, Double[] starting) {
        int n = score.length;
        double[] total = new double[n];

        // 1. 按 currentScore 降序排，同分按 index（起家座位）升序
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) indices[i] = i;
        Arrays.sort(indices, (a, b) -> {
            int cmp = Integer.compare(score[b], score[a]);
            if (cmp != 0) return cmp;
            return Integer.compare(a, b); // 座位小优先
        });

        // 2. 确定顺位并分配 uma
        // 排好序后，相同分数的人共享相同顺位
        int[] ranking = new int[n];
        ranking[indices[0]] = 1;
        for (int i = 1; i < n; i++) {
            if (score[indices[i]].equals(score[indices[i - 1]])) {
                ranking[indices[i]] = ranking[indices[i - 1]];
            } else {
                ranking[indices[i]] = i + 1;
            }
        }

        // 3. 同顺位的人，按 currentScore→座位 排序后依次拿对应 uma
        //    排序后的 indices 已经是正确顺序，直接按顺序分配 uma
        for (int i = 0; i < n; i++) {
            int playerIdx = indices[i];
            total[playerIdx] = Math.round(((score[playerIdx] - RETURN_POINT) / 1000.0 + uma[i] + starting[playerIdx]) * 10) / 10.0;
        }

        return total;
    }

    private static double[] calcWithUma(Integer[] newScore, Rule rule, Double[] starting) {
        String pattern = rule.getPattern(newScore);
        double[] uma = rule.getUma(pattern);
        return calcFinalScore(newScore, uma, starting);
    }

    /**
     * 公式战晋级判定：total → 第一半庄名次 → 第二半庄名次（从finalScore算） → 第二半庄座位
     *
     * @param total             最终总分
     * @param playerIndex       要判定的玩家
     * @param winningCondition  前几名晋级
     * @param firstHanchanRank  第一半庄名次（用户输入，1-4，无并列）
     * @param secondHanchanScore 第二半庄结算后的分数（用于算第二半庄名次）
     */
    static boolean isQualified(double[] total, int playerIndex, int winningCondition, int[] firstHanchanRank, Integer[] secondHanchanScore) {
        int n = total.length;

        // 算第二半庄名次：按分数降序，同分按座位（index小优先）
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) indices[i] = i;
        Arrays.sort(indices, (a, b) -> {
            int cmp = Integer.compare(secondHanchanScore[b], secondHanchanScore[a]);
            if (cmp != 0) return cmp;
            return Integer.compare(a, b);
        });
        int[] secondHanchanRank = new int[n];
        for (int i = 0; i < n; i++) {
            secondHanchanRank[indices[i]] = i + 1;
        }

        // 排序: total → 第一半庄名次(小优先) → 第二半庄名次(小优先) → 座位(小优先)
        Integer[] playerIndices = new Integer[n];
        for (int i = 0; i < n; i++) playerIndices[i] = i;
        Arrays.sort(playerIndices, (a, b) -> {
            int cmp = Double.compare(total[b], total[a]);
            if (cmp != 0) return cmp;
            cmp = Integer.compare(firstHanchanRank[a], firstHanchanRank[b]);
            if (cmp != 0) return cmp;
            cmp = Integer.compare(secondHanchanRank[a], secondHanchanRank[b]);
            if (cmp != 0) return cmp;
            return Integer.compare(a, b);
        });

        for (int i = 0; i < winningCondition; i++) {
            if (playerIndices[i] == playerIndex) return true;
        }
        return false;
    }

    // === 分数调整方法 ===

    private static void adjustScoreTsumo(Integer[] currentScore, int playerId, int[] pay, int kyotaku, int honba, int dealerIndex) {
        int n = currentScore.length;
        for (int i = 0; i < n; i++) {
            if (i == playerId) continue;
            if (i == dealerIndex) currentScore[i] -= (pay[1] + 100 * honba);
            else currentScore[i] -= (pay[0] + 100 * honba);
        }
        currentScore[playerId] += 2 * pay[0] + pay[1] + 300 * honba + kyotaku;
    }

    private static void adjustScoreTsumo(Integer[] currentScore, int playerId, int pay, int kyotaku, int honba) {
        int n = currentScore.length;
        for (int i = 0; i < n; i++) {
            if (i == playerId) continue;
            currentScore[i] -= (pay + 100 * honba);
        }
        currentScore[playerId] += pay * (n - 1) + 300 * honba + kyotaku;
    }

    private static void adjustScoreRon(Integer[] currentScore, int loserId, int winnerId, int score, int kyotaku, int honba) {
        currentScore[loserId] -= (score + 300 * honba);
        currentScore[winnerId] += (score + 300 * honba + kyotaku);
    }

    // === 输出方法 ===

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

    private static void printTsumoRange(StringBuilder results, List<?> tsumoList, int start, int end, boolean isDealer) {
        if (start == end) {
            if (isDealer) {
                results.append("自摸\t").append(getYakuName((int) tsumoList.get(start), true, "tsumo")).append("\n");
            } else {
                int[] arr = (int[]) tsumoList.get(start);
                results.append("自摸\t").append(getYakuName(arr[0], arr[1])).append("\n");
            }
        } else if (end == tsumoList.size() - 1) {
            if (isDealer) {
                results.append("自摸\t").append(getYakuName((int) tsumoList.get(start), true, "tsumo")).append("以上\n");
            } else {
                int[] arr = (int[]) tsumoList.get(start);
                results.append("自摸\t").append(getYakuName(arr[0], arr[1])).append("以上\n");
            }
        } else {
            if (isDealer) {
                results.append("自摸\t").append(getYakuName((int) tsumoList.get(start), true, "tsumo"))
                        .append("以上").append(getYakuName((int) tsumoList.get(end), true, "tsumo")).append("以下\n");
            } else {
                int[] arrS = (int[]) tsumoList.get(start);
                int[] arrE = (int[]) tsumoList.get(end);
                results.append("自摸\t").append(getYakuName(arrS[0], arrS[1]))
                        .append("以上").append(getYakuName(arrE[0], arrE[1])).append("以下\n");
            }
        }
    }

    private static void printRonRange(StringBuilder results, List<Integer> ronList, int start, int end, boolean isDealer, String dealinPlayerName) {
        if (start == end) {
            results.append(dealinPlayerName).append("荣和\t")
                    .append(getYakuName(ronList.get(start), isDealer, "ron")).append("\n");
        } else if (end == ronList.size() - 1) {
            results.append(dealinPlayerName).append("荣和\t")
                    .append(getYakuName(ronList.get(start), isDealer, "ron")).append("以上\n");
        } else {
            results.append(dealinPlayerName).append("荣和\t")
                    .append(getYakuName(ronList.get(start), isDealer, "ron")).append("以上")
                    .append(getYakuName(ronList.get(end), isDealer, "ron")).append("以下\n");
        }
    }

    private static void printDealinRange(StringBuilder results, List<Integer> ronList, int start, int end, boolean isDealer, String dealToPlayerName) {
        if (start == end) {
            results.append("对").append(dealToPlayerName).append("放铳\t")
                    .append(getYakuName(ronList.get(start), isDealer, "ron")).append("\n");
        } else if (end == ronList.size() - 1) {
            results.append("对").append(dealToPlayerName).append("放铳\t")
                    .append(getYakuName(ronList.get(start), isDealer, "ron")).append("以上\n");
        } else {
            results.append("对").append(dealToPlayerName).append("放铳\t");
            if (start != 0) {
                results.append(getYakuName(ronList.get(start), isDealer, "ron")).append("以上");
            }
            results.append(getYakuName(ronList.get(end), isDealer, "ron")).append("以下\n");
        }
    }

    private static void printBeTsumoRange(StringBuilder results, List<?> tsumoList, int start, int end, boolean isDealer, String fromPlayerName) {
        if (start == end) {
            if (isDealer) {
                results.append("被").append(fromPlayerName).append("亲家自摸\t")
                        .append(getYakuName((int) tsumoList.get(start), true, "tsumo")).append("以下\n");
            } else {
                int[] arr = (int[]) tsumoList.get(start);
                results.append("被").append(fromPlayerName).append("自摸\t")
                        .append(getYakuName(arr[0], arr[1])).append("以下\n");
            }
        } else if (end == tsumoList.size() - 1) {
            if (isDealer) {
                results.append("被").append(fromPlayerName).append("亲家自摸\t")
                        .append(getYakuName((int) tsumoList.get(start), true, "tsumo")).append("以上\n");
            } else {
                int[] arr = (int[]) tsumoList.get(start);
                results.append("被").append(fromPlayerName).append("自摸\t")
                        .append(getYakuName(arr[0], arr[1])).append("以上\n");
            }
        } else {
            if (isDealer) {
                results.append("被").append(fromPlayerName).append("亲家自摸\t");
                if (start != 0) {
                    results.append(getYakuName((int) tsumoList.get(start), true, "tsumo")).append("以上");
                }
                results.append(getYakuName((int) tsumoList.get(end), true, "tsumo")).append("以下\n");
            } else {
                int[] arrS = (int[]) tsumoList.get(start);
                int[] arrE = (int[]) tsumoList.get(end);
                results.append("被").append(fromPlayerName).append("自摸\t");
                if (start != 0) {
                    results.append(getYakuName(arrS[0], arrS[1])).append("以上");
                }
                results.append(getYakuName(arrE[0], arrE[1])).append("以下\n");
            }
        }
    }

    private static void printDealInDangerRange(StringBuilder results, int[] ronList, int start, int end, boolean isDealer, String loserName, String winnerName) {
        String base = loserName + "点" + winnerName;
        if (start == end) {
            results.append(base).append("\t")
                    .append(getYakuName(ronList[start], isDealer, "ron")).append("以上会丢晋级资格\n");
        } else if (end == ronList.length - 1) {
            results.append(base).append("\t")
                    .append(getYakuName(ronList[start], isDealer, "ron")).append("以上会丢晋级资格\n");
        } else {
            results.append(base).append("\t");
            if (start != 0) {
                results.append(getYakuName(ronList[start], isDealer, "ron")).append("以上");
            }
            results.append(getYakuName(ronList[end], isDealer, "ron")).append("以下会丢晋级资格\n");
        }
    }
}
