import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MahjongConditionCalculator extends JFrame {
    private static final int PLAYER_COUNT = 4;
    private JTextField[] nameFields = new JTextField[PLAYER_COUNT];
    private JTextField[] startingFields = new JTextField[PLAYER_COUNT];
    private JTextField[] scoreFields = new JTextField[PLAYER_COUNT];
    private JTextField kyotakuField = new JTextField(5);
    private JTextField honbaField = new JTextField(5);
    private JButton calcButton = new JButton("计算");
    private JTextArea[] resultAreas = new JTextArea[PLAYER_COUNT];

    public MahjongConditionCalculator() {
        setTitle("晋级/优胜条件计算");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 550);
        setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // 表头
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.gridx = 1;
        for (int i = 0; i < PLAYER_COUNT; i++) {
            JLabel label = new JLabel("Line " + (i + 1));
            gbc.gridx = i + 1;
            gbc.gridy = 0;
            inputPanel.add(label, gbc);
        }

        // 名字
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("名字"), gbc);
        for (int i = 0; i < PLAYER_COUNT; i++) {
            nameFields[i] = new JTextField(6);
            gbc.gridx = i + 1;
            inputPanel.add(nameFields[i], gbc);
        }

        // Starting
        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Starting"), gbc);
        for (int i = 0; i < PLAYER_COUNT; i++) {
            startingFields[i] = new JTextField(6);
            gbc.gridx = i + 1;
            inputPanel.add(startingFields[i], gbc);
        }

        // 当前分数
        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(new JLabel("当前分数"), gbc);
        for (int i = 0; i < PLAYER_COUNT; i++) {
            scoreFields[i] = new JTextField(6);
            gbc.gridx = i + 1;
            inputPanel.add(scoreFields[i], gbc);
        }

        // 晋级条件 下拉框
        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(new JLabel("晋级条件"), gbc);

        // 可选“1”或“2”
        String[] advanceOptions = {"1", "2"};
        JComboBox<String> advanceBox = new JComboBox<>(advanceOptions);
        advanceBox.setSelectedIndex(0);
        gbc.gridx = 1;
        inputPanel.add(advanceBox, gbc);

        // 供托
        gbc.gridx = 2;
        inputPanel.add(new JLabel("供托"), gbc);

        gbc.gridx = 3;
        inputPanel.add(kyotakuField, gbc);

        // 本场
        gbc.gridx = 4;
        inputPanel.add(new JLabel("本场"), gbc);

        gbc.gridx = 5;
        inputPanel.add(honbaField, gbc);

        // 计算按钮
        gbc.gridx = 6;
        inputPanel.add(calcButton, gbc);


        // 下方四个输出区域
        JPanel outputPanel = new JPanel(new GridLayout(2, 2, 12, 12));
        Font resultFont = new Font(Font.MONOSPACED, Font.PLAIN, 13);

        for (int i = 0; i < PLAYER_COUNT; i++) {
            JPanel playerPanel = new JPanel();
            playerPanel.setLayout(new BorderLayout(5, 5));
            resultAreas[i] = new JTextArea(7, 22);
            resultAreas[i].setFont(resultFont);
            resultAreas[i].setLineWrap(true);
            resultAreas[i].setWrapStyleWord(true);
            resultAreas[i].setEditable(false);
            JScrollPane scroll = new JScrollPane(resultAreas[i]);
            playerPanel.add(scroll, BorderLayout.CENTER);

            outputPanel.add(playerPanel);
        }

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(inputPanel, BorderLayout.NORTH);
        getContentPane().add(outputPanel, BorderLayout.CENTER);

        // 点击按钮，收集输入，调用你的算法
        calcButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] playerNames = new String[PLAYER_COUNT];
                Double[] starting = new Double[PLAYER_COUNT];
                Integer[] score = new Integer[PLAYER_COUNT];
                for (int i = 0; i < PLAYER_COUNT; i++) {
                    playerNames[i] = nameFields[i].getText()
                            .trim();
                    try {
                        starting[i] = Double.parseDouble(startingFields[i].getText()
                                .trim());
                    } catch (Exception ex) {
                        starting[i] = 0.0;
                    }
                    try {
                        score[i] = Integer.parseInt(scoreFields[i].getText()
                                .trim());
                    } catch (Exception ex) {
                        score[i] = 25000;
                    }
                }
                int kyotaku = 0, honba = 0;
                try {
                    kyotaku = Integer.parseInt(kyotakuField.getText()
                            .trim());
                } catch (Exception ex) {
                }
                try {
                    honba = Integer.parseInt(honbaField.getText()
                            .trim());
                } catch (Exception ex) {
                }

                // 你要补充的: 这里如何调用你自己的calculateCondition, 以及其他参数，比如规则类等
                // 这里只是demo，每人都用自己的index调用
                for (int i = 0; i < PLAYER_COUNT; i++) {
                    // TODO: 替换下面调用为你的 ConditionCalculation.calculateCondition 实际逻辑
                    String output = ConditionCalculation.calculateCondition(
                            starting, score, i, kyotaku, honba, advanceBox.getSelectedIndex() + 1, new MLeague(), playerNames, true
                    );
                    resultAreas[i].setText(playerNames[i] + "晋级条件\n" + output);
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MahjongConditionCalculator().setVisible(true));
    }
}
