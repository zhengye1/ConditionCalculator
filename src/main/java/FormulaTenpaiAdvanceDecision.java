import java.util.*;

public class FormulaTenpaiAdvanceDecision {

    public static List<DecisionResult> getAllTenpaiAdvance(Double[] starting, Integer[] current,
                                                           Rule rule, String[] players, int advanceCount,
                                                           int currentDealerIndex, int[] firstHanchanRank) {
        List<DecisionResult> results = new ArrayList<>();
        int n = players.length;
        List<Set<Integer>> patterns = new ArrayList<>();

        // 全员未听牌
        patterns.add(new HashSet<>());
        // 全员听牌
        Set<Integer> all = new HashSet<>();
        for (int i = 0; i < n; i++) all.add(i);
        patterns.add(all);
        // 一人听牌
        for (int i = 0; i < n; i++) {
            Set<Integer> s = new HashSet<>();
            s.add(i);
            patterns.add(s);
        }
        // 一人未听牌
        for (int i = 0; i < n; i++) {
            Set<Integer> s = new HashSet<>();
            for (int j = 0; j < n; j++) if (j != i) s.add(j);
            patterns.add(s);
        }
        // 两人听牌
        for (int i = 0; i < n; i++) for (int j = i + 1; j < n; j++) {
            Set<Integer> s = new LinkedHashSet<>();
            s.add(i);
            s.add(j);
            patterns.add(s);
        }

        for (Set<Integer> tenpaiSet : patterns) {
            Integer[] scoreAfter = Arrays.copyOf(current, n);
            TenpaiAdvanceDecision.applyNotenPenalty(scoreAfter, tenpaiSet, 3000);
            String pattern = rule.getPattern(scoreAfter);
            double[] uma = rule.getUma(pattern);
            double[] total = FormulaConditionCalculation.calcFinalScore(scoreAfter, uma, starting);

            // 第二半庄名次：按 scoreAfter 降序，同分按座位
            Integer[] sortIdx = new Integer[n];
            for (int i = 0; i < n; i++) sortIdx[i] = i;
            Arrays.sort(sortIdx, (a, b) -> {
                int cmp = Integer.compare(scoreAfter[b], scoreAfter[a]);
                if (cmp != 0) return cmp;
                return Integer.compare(a, b);
            });
            int[] secondHanchanRank = new int[n];
            for (int i = 0; i < n; i++) secondHanchanRank[sortIdx[i]] = i + 1;

            // 排序: total → 第一半庄名次 → 第二半庄名次 → 座位
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

            List<String> advance = new ArrayList<>();
            for (int i = 0; i < advanceCount; i++) advance.add(players[playerIndices[i]]);

            String tenpaiDesc;
            if (tenpaiSet.size() == n) {
                tenpaiDesc = "全员听牌";
            } else if (tenpaiSet.isEmpty()) {
                tenpaiDesc = "全员未听牌";
            } else if (tenpaiSet.size() == 1) {
                int idx = tenpaiSet.iterator().next();
                tenpaiDesc = players[idx] + "一人听牌";
            } else if (tenpaiSet.size() == n - 1) {
                tenpaiDesc = "";
                for (int i = 0; i < n; i++) {
                    if (!tenpaiSet.contains(i)) {
                        tenpaiDesc = players[i] + "一人未听牌";
                        break;
                    }
                }
            } else {
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

    public static class DecisionResult {
        public final String tenpaiDesc;
        public final List<String> winners;
        public final String extraNote;

        public DecisionResult(String desc, List<String> win, String note) {
            this.tenpaiDesc = desc;
            this.winners = win;
            this.extraNote = note;
        }

        @Override
        public String toString() {
            return tenpaiDesc + "：" + winners + ((winners.size() == 1) ? "优胜" : "晋级") + extraNote;
        }
    }
}
