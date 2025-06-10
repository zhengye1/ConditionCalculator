import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MainClass {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String[] player = new String[]{"小V", "靖", "萨菲", "男神"};
        Rule Arule = new JPMLRule();
        Rule WRCrule = new WRCRule();
        Rule MLeague = new MLeague();
        Double[] starting = new Double[]{57.8, 40.7, -15.8, -82.7};
        Integer[] currentScore = new Integer[]{35700, 25300, 20700, 38300};

        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Future<String>> results = new ArrayList<>();
        int kyotaku = 0;
        int honba = 0;

        System.out.println(ConditionCalculation
                .calculateCondition(starting, currentScore, 0,
                        kyotaku, honba, 2, Arule, player, false));
//        for (int i = 0; i < 4; i++) {
//            final int playerId = i;
//            Callable<String> task = () -> ConditionCalculation
//                    .calculateCondition(starting, currentScore, playerId,
//                            kyotaku, honba, 2, MLeague, player, true);
//            results.add(executor.submit(task));
//        }
//        for (int i = 0; i < results.size(); i++) {
//            String condition = results.get(i).get();
//            System.out.println(player[i] + " 晋级条件: \n" + condition);
//            System.out.println("====================");
//        }
//
//        executor.shutdown();

    }
}
