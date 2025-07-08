import javax.swing.*;
import java.awt.*;

public class DangerDialog extends JDialog {

    public DangerDialog(JFrame owner, Double[] starting, Integer[] score, int kyotaku, int honba,
                        int winningCondition, Rule rule, String[] playerNames,
                        boolean kiriageMangan, int dealerIndex) {
        super(owner, "放铳危险区间（所有玩家）", true);  // true=模态
        setSize(1100, 500);
        setLocationRelativeTo(owner);

        JPanel panel = new JPanel(new GridLayout(2, 2, 12, 12));
        Font resultFont = new Font(Font.MONOSPACED, Font.PLAIN, 13);

        JTextArea[] dangerAreas = new JTextArea[4];
        for (int i = 0; i < 4; i++) {
            JPanel playerPanel = new JPanel(new BorderLayout(5, 5));
            dangerAreas[i] = new JTextArea(12, 28);
            dangerAreas[i].setFont(resultFont);
            dangerAreas[i].setLineWrap(true);
            dangerAreas[i].setWrapStyleWord(true);
            dangerAreas[i].setEditable(false);
            JScrollPane scroll = new JScrollPane(dangerAreas[i]);
            playerPanel.add(scroll, BorderLayout.CENTER);
            panel.add(playerPanel);
        }
        getContentPane().add(panel, BorderLayout.CENTER);

        // 填充内容
        for (int i = 0; i < 4; i++) {
            String sb = playerNames[i] + "危险区间:\n" +
                    ConditionCalculation.calcDangerByOthersDealIn(
                            starting, score, i, kyotaku, honba,
                            winningCondition, rule, playerNames,
                            kiriageMangan, dealerIndex
                    );
            dangerAreas[i].setText(sb);
        }
    }
}
