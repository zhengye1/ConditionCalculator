import java.util.*;

public class TenpaiAdvanceDecision {
    /**
     * 计算所有听牌情况下，晋级的选手
     * @param starting 开局前总分
     * @param current 当前分数
     * @param rule 用于获得马点（可不传也行，备用）
     * @param players 玩家名字的array
     * @param advanceCount 前几名晋级
     * @param currentDealerIndex 当前庄家索引
     * @return List<DecisionResult>，每个是一个听牌情况+晋级名单
     */
    public static List<DecisionResult> getAllTenpaiAdvance(Double[] starting, Integer[] current,
                                                           Rule rule, String[] players, int advanceCount,
                                                           int currentDealerIndex) {
            List<DecisionResult> results = new ArrayList<>();
            int n = players.length;

            List<Set<Integer>> patterns = new ArrayList<>();

            // 1. 全员未听牌
            patterns.add(new HashSet<>());

            // 2. 全员听牌
            Set<Integer> all = new HashSet<>();
            for (int i = 0; i < n; i++) all.add(i);
            patterns.add(all);

            // 3. 每个人一人听牌
            for (int i = 0; i < n; i++) {
                Set<Integer> s = new HashSet<>();
                s.add(i);
                patterns.add(s);
            }

            // 4. 每个人一人未听牌
            for (int i = 0; i < n; i++) {
                Set<Integer> s = new HashSet<>();
                for (int j = 0; j < n; j++) if (j != i) s.add(j);
                patterns.add(s);
            }

            // 5. 两个人听牌的所有组合，顺序和输入一致
            for (int i = 0; i < n; i++) for (int j = i + 1; j < n; j++) {
                Set<Integer> s = new LinkedHashSet<>();
                s.add(i); s.add(j);
                patterns.add(s);
            }

            // 遍历每个pattern
            for (Set<Integer> tenpaiSet : patterns) {
                Integer[] scoreAfter = Arrays.copyOf(current, n);
                applyNotenPenalty(scoreAfter, tenpaiSet, 3000);
                int[] ranking = RankUtil.getRanking(scoreAfter);
                double[] uma = rule.getUma(rule.getPattern(scoreAfter));
                double[] total = ConditionCalculation.calcFinalScore(scoreAfter, uma, starting, ranking);

                PlayerScore[] arr = new PlayerScore[n];
                for (int i = 0; i < n; i++) arr[i] = new PlayerScore(total[i], starting[i], i);

                Arrays.sort(arr, (a, b) -> {
                    int cmp = Double.compare(b.total, a.total);
                    if (cmp != 0) return cmp;
                    cmp = Double.compare(b.starting, a.starting);
                    if (cmp != 0) return cmp;
                    return Integer.compare(a.index, b.index);
                });

                List<String> advance = new ArrayList<>();
                for (int i = 0; i < advanceCount; i++) advance.add(players[arr[i].index]);

                // 精简输出
                String tenpaiDesc="";
                if (tenpaiSet.size() == n) {
                    tenpaiDesc = "全员听牌";
                } else if (tenpaiSet.isEmpty()) {
                    tenpaiDesc = "全员未听牌";
                } else if (tenpaiSet.size() == 1) {
                    int idx = tenpaiSet.iterator().next();
                    tenpaiDesc = players[idx] + "一人听牌";
                } else if (tenpaiSet.size() == n - 1) {
                    for (int i = 0; i < n; i++) {
                        if (!tenpaiSet.contains(i)) {
                            tenpaiDesc = players[i] + "一人未听牌";
                            break;
                        }
                    }
                } else {
                    // 两人或三人听牌的描述
                    StringBuilder s = new StringBuilder();
                    List<Integer> idxList = new ArrayList<>(tenpaiSet);
                    idxList.sort(Comparator.naturalOrder());
                    for (int ii = 0; ii < idxList.size(); ii++) {
                        if (ii > 0) s.append("、");
                        s.append(players[idxList.get(ii)]);
                    }
                    tenpaiDesc = s + "听牌";
                }
                boolean isOyaContinue = tenpaiSet.contains(currentDealerIndex);
                String extraNote = "";
                if (isOyaContinue) {
                    extraNote = "，但" + players[currentDealerIndex] + "听牌而继续";
                }
                results.add(new DecisionResult(tenpaiDesc, advance, extraNote));
            }

            return results;
        }


    /**
     * 对听牌情况分配罚符
     */
    public static void applyNotenPenalty(Integer[] scores, Set<Integer> tenpai, int penalty) {
        int n = scores.length;
        int tenpaiNum = tenpai.size();
        if (tenpaiNum == 0 || tenpaiNum == n) return; // 全听全不听无需操作
        int gain = penalty / tenpaiNum;
        int lose = penalty / (n - tenpaiNum);
        for (int i = 0; i < n; i++) {
            if (tenpai.contains(i)) {
                scores[i] += gain * (n - tenpaiNum);
            } else {
                scores[i] -= lose * tenpaiNum;
            }
        }
    }
    /** 用于UI展示的结果结构 */
    public static class DecisionResult {
        public final String tenpaiDesc;
        public final List<String> winners;
        public final String extraNote;
        public DecisionResult(String desc, List<String> win,  String note) {
            this.tenpaiDesc = desc;
            this.winners = win;
            this.extraNote = note;
        }
        @Override
        public String toString() {
            return tenpaiDesc + "：" + winners +((winners.size() == 1)?"优胜":"晋级") + extraNote;
        }
    }

}
