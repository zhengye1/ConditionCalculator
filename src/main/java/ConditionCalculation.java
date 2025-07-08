import java.util.*;

public class ConditionCalculation {
    // 子家荣和
    private final static int[] childRonArr = {1000, 1300, 1600, 2000, 2300, 2600, 2900, 3200, 3900, 4500, 5200, 5800, 6400, 7100, 7700, 8000, 12000, 16000, 24000, 32000, 64000, 96000, 128000};
    // 亲家自摸（每家支付）
    private final static int[] dealerTsumoArr = {500, 700, 800, 1000, 1200, 1300, 1500, 1600, 2000, 2300, 2600, 2900, 3200, 3600, 3900, 4000, 6000, 8000, 12000, 16000, 32000, 48000, 64000};
    // 亲家荣和
    private final static int[] dealerRonArr = {1500, 2000, 2400, 2900, 3400, 3900, 4400, 4800, 5800, 6800, 7700, 8700, 9600, 10600, 11600, 12000, 18000, 24000, 36000, 48000, 96000, 144000, 192000};
    // 自摸分数表：子家自摸每家和亲家
    private final static int[][] childTsumoArr = {{300, 500}, {400, 700}, {400, 800}, {500, 1000}, {600, 1200}, {700, 1300}, {800, 1500}, {800, 1600}, {1000, 2000}, {1200, 2300}, {1300, 2600}, {1500, 2900}, {1600, 3200}, {1800, 3600}, {2000, 3900}, {2000, 4000}, {3000, 6000}, {4000, 8000}, {6000, 12000}, {8000, 16000}, {16000, 32000}, {24000, 48000}, {32000, 64000}};

    /**
     * Calculate for this player winning condition
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
    public static String calculateCondition(Double[] starting, Integer[] currentScore, int playerIndex, int kyotaku, int honba, int winningCondition, Rule rule, String[] player, boolean kiriageMangan, int dealerIndex) {
        StringBuilder results = new StringBuilder();
        int playerCount = currentScore.length;
        boolean isDealer = (playerIndex == dealerIndex);
        Integer[] newScore; // 每算一次都要重新copy一次值
        List<Boolean> resultList = new ArrayList<>();
        List<int[]> tsumoPairs = new ArrayList<>(); // 子家
        List<Integer> tsumoDealer = new ArrayList<>(); // 亲家
        // 1. 自摸所有情况
        if (!isDealer) {
            for (int[] tsumoPair : childTsumoArr) {
                newScore = Arrays.copyOf(currentScore, playerCount);
                if (kiriageMangan && tsumoPair[0] == 2000 && tsumoPair[1] == 3900) continue;
                tsumoPairs.add(tsumoPair);
                adjustScoreTsumo(newScore, playerIndex, tsumoPair, kyotaku, honba, dealerIndex);
                int[] ranking = RankUtil.getRanking(newScore);
                String pattern = rule.getPattern(newScore);
                double[] uma = rule.getUma(pattern);
                double[] total = calcFinalScore(newScore, uma, starting, ranking);
                resultList.add(isQualified(total, playerIndex, winningCondition, starting));
            }
        } else {
            for (int j : dealerTsumoArr) {
                if (kiriageMangan && j == 3900) continue;
                tsumoDealer.add(j);
                newScore = Arrays.copyOf(currentScore, playerCount);
                adjustScoreTsumo(newScore, playerIndex, j, kyotaku, honba);
                int[] ranking = RankUtil.getRanking(newScore);
                String pattern = rule.getPattern(newScore);
                double[] uma = rule.getUma(pattern);
                double[] total = calcFinalScore(newScore, uma, starting, ranking);
                resultList.add(isQualified(total, playerIndex, winningCondition, starting));
            }
        }
        // 如果整个list都是false的话，那就简单输出自摸无条件
        if (resultList.stream()
                .noneMatch(x -> x)) {
            results.append("自摸无条件\n");
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

        // 2. 荣和所有情况
        for (int i = 0; i < playerCount; i++) {
            resultList.clear();
            List<Integer> ronList = new ArrayList<>();
            if (i == playerIndex) continue;
            int[] ronArray = (!isDealer) ? childRonArr : dealerRonArr;
            for (int score : ronArray) {
                // 要是切上了就看是不是子家7700或者亲家 11600，是的话不用算
                if (kiriageMangan && (score == 7700 && !isDealer || (score == 11600 && isDealer))) continue;
                ronList.add(score);
                newScore = Arrays.copyOf(currentScore, playerCount);
                adjustScoreRon(newScore, i, playerIndex, score, kyotaku, honba);
                int[] ranking = RankUtil.getRanking(newScore);
                String pattern = rule.getPattern(newScore);
                double[] uma = rule.getUma(pattern);
                double[] total = calcFinalScore(newScore, uma, starting, ranking);
                resultList.add(isQualified(total, playerIndex, winningCondition, starting));
            }
            if (resultList.stream()
                    .noneMatch(x -> x)) {
                results.append(player[i])
                        .append("\t荣和\t无条件");
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

        // 3. 放铳情况
        for (int i = 0; i < playerCount; i++) {
            resultList.clear();
            List<Integer> ronList = new ArrayList<>();
            if (i == playerIndex) continue;
            // 看是不是点炮给亲家
            boolean dealToDealer = (i == dealerIndex);
            int[] ronArr = dealToDealer ? dealerRonArr : childRonArr;
            for (int ron : ronArr) {
                // 切上了就看是不是子家7700或者亲家 11600，是的话不用算
                if (kiriageMangan && (ron == 7700 && !dealToDealer || (ron == 11600 && dealToDealer))) continue;
                newScore = Arrays.copyOf(currentScore, playerCount);
                ronList.add(ron);
                adjustScoreRon(newScore, playerIndex, i, ron, kyotaku, honba);
                int[] ranking = RankUtil.getRanking(newScore);
                String pattern = rule.getPattern(newScore);
                double[] uma = rule.getUma(pattern);
                double[] total = calcFinalScore(newScore, uma, starting, ranking);
                resultList.add(isQualified(total, playerIndex, winningCondition, starting));
            }
            if (resultList.stream()
                    .anyMatch(x -> x)) {
                // 点了还能晋级就看区间
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

        // 4. 被自摸的情况
        for (int other = 0; other < playerCount; other++) {
            if (other == playerIndex) continue;
            boolean otherIsDealer = (other == dealerIndex);

            List<Boolean> beTsumoResultList = new ArrayList<>();
            List<int[]> beTsumoChildPairs = new ArrayList<>();  // 存子家自摸每个pair
            List<Integer> beTsumoDealerArr = new ArrayList<>(); // 存亲家自摸

            if (!otherIsDealer) {
                // 对方是子家自摸（模拟被自摸）
                for (int[] tsumoPair : childTsumoArr) {
                    newScore = Arrays.copyOf(currentScore, playerCount);
                    if (kiriageMangan && tsumoPair[0] == 2000 && tsumoPair[1] == 3900) continue;
                    beTsumoChildPairs.add(tsumoPair);

                    // 模拟“other”自摸，把自己(playerIndex)视作失分方
                    for (int i = 0; i < playerCount; i++) {
                        if (i == other) continue;
                        if (i == dealerIndex) newScore[i] -= (tsumoPair[1] + 100 * honba);
                        else newScore[i] -= (tsumoPair[0] + 100 * honba);
                    }
                    newScore[other] += 2 * tsumoPair[0] + tsumoPair[1] + 300 * honba + kyotaku;

                    int[] ranking = RankUtil.getRanking(newScore);
                    String pattern = rule.getPattern(newScore);
                    double[] uma = rule.getUma(pattern);
                    double[] total = calcFinalScore(newScore, uma, starting, ranking);
                    beTsumoResultList.add(isQualified(total, playerIndex, winningCondition, starting));
                }
                // 输出
                if (beTsumoResultList.stream()
                        .anyMatch(x -> x)) {
                    boolean last = false;
                    int start = -1;
                    for (int i = 0; i < beTsumoResultList.size(); i++) {
                        boolean curr = beTsumoResultList.get(i);
                        if (!last && curr) start = i;
                        if (last && !curr) {
                            // 区间输出
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
                // 对方为亲家自摸
                for (int score : dealerTsumoArr) {
                    if (kiriageMangan && score == 3900) continue;
                    newScore = Arrays.copyOf(currentScore, playerCount);
                    beTsumoDealerArr.add(score);
                    for (int i = 0; i < playerCount; i++) {
                        if (i == other) continue;
                        newScore[i] -= (score + 300 * honba);
                    }
                    newScore[other] += score * (playerCount - 1) + 300 * honba + kyotaku;

                    int[] ranking = RankUtil.getRanking(newScore);
                    String pattern = rule.getPattern(newScore);
                    double[] uma = rule.getUma(pattern);
                    double[] total = calcFinalScore(newScore, uma, starting, ranking);
                    beTsumoResultList.add(isQualified(total, playerIndex, winningCondition, starting));
                }
                // 输出
                if (beTsumoResultList.stream()
                        .anyMatch(x -> x)) {
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

    // 子家自摸（分数变动）
    private static void adjustScoreTsumo(Integer[] currentScore, int playerId, int[] pay, int kyotaku, int honba, int dealerIndex) {
        int n = currentScore.length;

        // pay[0]: 子家每家支付, pay[1]: 亲家支付
        for (int i = 0; i < n; i++) {
            if (i == playerId) continue;
            if (i == dealerIndex) currentScore[i] -= (pay[1] + 100 * honba); // 假设3号为亲家
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


    static double[] calcFinalScore(Integer[] score, double[] uma, Double[] starting, int[] ranking) {
        int n = score.length;
        double[] total = new double[n];

        // 1. 统计每个顺位有哪些人
        // 顺位1开始
        Map<Integer, List<Integer>> rankToIndices = new HashMap<>();
        for (int i = 0; i < n; i++) {
            rankToIndices.computeIfAbsent(ranking[i], x -> new ArrayList<>())
                    .add(i);
        }

        // 2. 为每组顺位分配uma（高uma优先分给index小的）
        double[] appliedUma = new double[n];
        for (Map.Entry<Integer, List<Integer>> entry : rankToIndices.entrySet()) {
            int rank = entry.getKey();
            List<Integer> idxList = entry.getValue();
            idxList.sort(Integer::compareTo); // index小的靠前
            // 从uma数组里选出连续的k个
            List<Double> umaPool = new ArrayList<>();
            for (int j = 0; j < idxList.size(); j++) {
                umaPool.add(uma[rank - 1 + j]);
            }
            // 高到低分配
            umaPool.sort(Collections.reverseOrder());
            for (int j = 0; j < idxList.size(); j++) {
                appliedUma[idxList.get(j)] = umaPool.get(j);
            }
        }

        // 3. 最终分
        for (int i = 0; i < n; i++) {
            total[i] = Math.round(((score[i] - 30000) / 1000.0 + appliedUma[i] + starting[i]) * 10) / 10.0;
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
            if (Math.abs(ps.total - thresholdPlayer.total) < 1e-6 && Math.abs(ps.starting - thresholdPlayer.starting) < 1e-6) {
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

        if (Math.abs(current.total - thresholdPlayer.total) < 1e-6 && Math.abs(current.starting - thresholdPlayer.starting) < 1e-6) {
            // 同分同starting，只有独占才晋级
            return same == 1;
        } else if (current.total > thresholdPlayer.total) {
            // total同分但starting高，排名会被提前，不会出现在threshold上，已经在晋级线内
            return true;
        } else
            return Math.abs(current.total - thresholdPlayer.total) < 1e-6 && current.starting > thresholdPlayer.starting;
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

    private static void printTsumoRange(StringBuilder results, List<?> tsumoList, int start, int end, boolean isDealer) {
        if (start == end) {
            // 只有一个
            if (isDealer) {
                results.append("自摸\t")
                        .append(getYakuName((int) tsumoList.get(start), true, "tsumo"))
                        .append("\n");
            } else {
                int[] arr = (int[]) tsumoList.get(start);
                results.append("自摸\t")
                        .append(getYakuName(arr[0], arr[1]))
                        .append("\n");
            }
        } else if (end == tsumoList.size() - 1) {
            // 一直true到最后
            if (isDealer) {
                results.append("自摸\t")
                        .append(getYakuName((int) tsumoList.get(start), true, "tsumo"))
                        .append("以上\n");
            } else {
                int[] arr = (int[]) tsumoList.get(start);
                results.append("自摸\t")
                        .append(getYakuName(arr[0], arr[1]))
                        .append("以上\n");
            }
        } else {
            // 中间一段
            if (isDealer) {
                results.append("自摸\t")
                        .append(getYakuName((int) tsumoList.get(start), true, "tsumo"))
                        .append("以上")
                        .append(getYakuName((int) tsumoList.get(end), true, "tsumo"))
                        .append("以下\n");
            } else {
                int[] arrS = (int[]) tsumoList.get(start);
                int[] arrE = (int[]) tsumoList.get(end);
                results.append("自摸\t")
                        .append(getYakuName(arrS[0], arrS[1]))
                        .append("以上")
                        .append(getYakuName(arrE[0], arrE[1]))
                        .append("以下\n");
            }
        }
    }

    private static void printRonRange(StringBuilder results, List<Integer> ronList, int start, int end, boolean isDealer, String dealinPlayerName) {
        if (start == end) {
            // 只有一个
            if (isDealer) {
                results.append(dealinPlayerName)
                        .append("荣和\t")
                        .append(getYakuName(ronList.get(start), true, "ron"))
                        .append("\n");
            } else {
                results.append(dealinPlayerName)
                        .append("荣和\t")
                        .append(getYakuName(ronList.get(start), false, "ron"))
                        .append("\n");
            }
        } else if (end == ronList.size() - 1) {
            // 一直true到最后
            if (isDealer) {
                results.append(dealinPlayerName)
                        .append("荣和\t")
                        .append(getYakuName(ronList.get(start), true, "ron"))
                        .append("以上\n");
            } else {
                results.append(dealinPlayerName)
                        .append("荣和\t")
                        .append(getYakuName(ronList.get(start), false, "ron"))
                        .append("以上\n");
            }
        } else {
            // 中间一段
            if (isDealer) {
                results.append(dealinPlayerName)
                        .append("荣和\t")
                        .append(getYakuName(ronList.get(start), true, "ron"))
                        .append("以上")
                        .append(getYakuName(ronList.get(end), true, "ron"))
                        .append("以下\n");
            } else {
                results.append(dealinPlayerName)
                        .append("荣和\t")
                        .append(getYakuName(ronList.get(start), false, "ron"))
                        .append("以上")
                        .append(getYakuName(ronList.get(end), false, "ron"))
                        .append("以下\n");
            }
        }
    }

    private static void printDealinRange(StringBuilder results, List<Integer> ronList, int start, int end, boolean isDealer, String dealToPlayerName) {
        if (start == end) {
            // 只有一个
            if (isDealer) {
                results.append("对")
                        .append(dealToPlayerName)
                        .append("放铳\t")
                        .append(getYakuName(ronList.get(start), true, "ron"))
                        .append("以下\n");
            } else {
                results.append("对")
                        .append(dealToPlayerName)
                        .append("放铳\t")
                        .append(getYakuName(ronList.get(start), false, "ron"))
                        .append("以下\n");
            }
        } else if (end == ronList.size() - 1) {
            // 一直true到最后
            if (isDealer) {
                results.append("对")
                        .append(dealToPlayerName)
                        .append("放铳\t")
                        .append(getYakuName(ronList.get(start), true, "ron"))
                        .append("以上\n");
            } else {
                results.append("对")
                        .append(dealToPlayerName)
                        .append("放铳\t")
                        .append(getYakuName(ronList.get(start), false, "ron"))
                        .append("以上\n");
            }
        } else {
            // 中间一段
            if (isDealer) {
                results.append("对")
                        .append(dealToPlayerName)
                        .append("放铳\t");
                if (start != 0) {


                    results.append(getYakuName(ronList.get(start), true, "ron"))
                            .append("以上");
                }
                results.append(getYakuName(ronList.get(end), true, "ron"))
                        .append("以下\n");
            } else {
                results.append("对")
                        .append(dealToPlayerName)
                        .append("放铳\t");
                if (start != 0) {
                    results.append(getYakuName(ronList.get(start), false, "ron"))
                            .append("以上");
                }
                results.append(getYakuName(ronList.get(end), false, "ron"))
                        .append("以下\n");
            }
        }
    }

    private static void printBeTsumoRange(StringBuilder results, List<?> tsumoList, int start, int end, boolean isDealer, String fromPlayerName) {
        if (start == end) {
            if (isDealer) {
                results.append("被")
                        .append(fromPlayerName)
                        .append("亲家自摸\t")
                        .append(getYakuName((int) tsumoList.get(start), true, "tsumo"))
                        .append("以下\n");
            } else {
                int[] arr = (int[]) tsumoList.get(start);
                results.append("被")
                        .append(fromPlayerName)
                        .append("自摸\t")
                        .append(getYakuName(arr[0], arr[1]))
                        .append("以下\n");
            }
        } else if (end == tsumoList.size() - 1) {
            if (isDealer) {
                results.append("被")
                        .append(fromPlayerName)
                        .append("亲家自摸\t")
                        .append(getYakuName((int) tsumoList.get(start), true, "tsumo"))
                        .append("以上\n");
            } else {
                int[] arr = (int[]) tsumoList.get(start);
                results.append("被")
                        .append(fromPlayerName)
                        .append("自摸\t")
                        .append(getYakuName(arr[0], arr[1]))
                        .append("以上\n");
            }
        } else {
            if (isDealer) {
                results.append("被")
                        .append(fromPlayerName)
                        .append("亲家自摸\t");
                if (start != 0) {
                    results.append(getYakuName((int) tsumoList.get(start), true, "tsumo"))
                            .append("以上");
                }
                results.append(getYakuName((int) tsumoList.get(end), true, "tsumo"))
                        .append("以下\n");
            } else {
                int[] arrS = (int[]) tsumoList.get(start);
                int[] arrE = (int[]) tsumoList.get(end);
                results.append("被")
                        .append(fromPlayerName)
                        .append("自摸\t");
                if (start != 0) {
                    results.append(getYakuName(arrS[0], arrS[1]))
                            .append("以上");
                }
                results.append(getYakuName(arrE[0], arrE[1]))
                        .append("以下\n");
            }
        }
    }

}
