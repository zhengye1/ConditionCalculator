import javax.swing.*;
import java.awt.*;

public class MahjongConditionCalculator extends JFrame {
    private static final int PLAYER_COUNT = 4;
    private JTextField[] nameFields = new JTextField[PLAYER_COUNT];
    private JTextField[] startingFields = new JTextField[PLAYER_COUNT];
    private JTextField[] scoreFields = new JTextField[PLAYER_COUNT];
    private JTextField kyotakuField = new JTextField(5);
    private JTextField honbaField = new JTextField(5);
    private JButton calcButton = new JButton("计算");
    private JTextArea[] resultAreas = new JTextArea[PLAYER_COUNT];
    private String[] seats = new String[] {"东", "南", "西", "北"};

    public MahjongConditionCalculator() {
        setTitle("晋级/优胜条件计算");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(950, 600); // 推荐更宽
        setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(7, 12, 7, 12);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;

        // 列宽
        Dimension tfDim = new Dimension(68, 25);

        gbc.gridy = 0;
        gbc.gridx = 0;
        inputPanel.add(new JLabel("起家"), gbc);

        // 表头
        gbc.gridy = 0;
        gbc.gridx = 1;
        for (int i = 0; i < PLAYER_COUNT; i++) {
            JLabel label = new JLabel(seats[i]);
            gbc.gridx = i + 1;
            inputPanel.add(label, gbc);
        }
        gbc.gridx = PLAYER_COUNT + 2;
        gbc.weightx = 1;
        inputPanel.add(Box.createHorizontalGlue(), gbc);
        gbc.weightx = 0;

        // 名字
        gbc.gridy = 1;
        gbc.gridx = 0;
        inputPanel.add(new JLabel("名字"), gbc);
        for (int i = 0; i < PLAYER_COUNT; i++) {
            nameFields[i] = new JTextField(6);
            nameFields[i].setPreferredSize(tfDim);
            gbc.gridx = i + 1;
            inputPanel.add(nameFields[i], gbc);
        }
        gbc.gridx = PLAYER_COUNT + 2;
        gbc.weightx = 1;
        inputPanel.add(Box.createHorizontalGlue(), gbc);
        gbc.weightx = 0;

        // Starting
        gbc.gridy = 2;
        gbc.gridx = 0;
        inputPanel.add(new JLabel("Starting"), gbc);
        for (int i = 0; i < PLAYER_COUNT; i++) {
            startingFields[i] = new JTextField(6);
            startingFields[i].setPreferredSize(tfDim);
            gbc.gridx = i + 1;
            inputPanel.add(startingFields[i], gbc);
        }
        gbc.gridx = PLAYER_COUNT + 2;
        gbc.weightx = 1;
        inputPanel.add(Box.createHorizontalGlue(), gbc);
        gbc.weightx = 0;

        // 当前分数
        gbc.gridy = 3;
        gbc.gridx = 0;
        inputPanel.add(new JLabel("当前分数"), gbc);
        for (int i = 0; i < PLAYER_COUNT; i++) {
            scoreFields[i] = new JTextField(6);
            scoreFields[i].setPreferredSize(tfDim);
            gbc.gridx = i + 1;
            inputPanel.add(scoreFields[i], gbc);
        }
        gbc.gridx = PLAYER_COUNT + 2;
        gbc.weightx = 1;
        inputPanel.add(Box.createHorizontalGlue(), gbc);
        gbc.weightx = 0;

        // 下方控件（切上满贯+晋级条件+规则+供托+本场+计算按钮）
        gbc.gridy = 4;
        gbc.gridx = 0;
        JCheckBox kiriageBox = new JCheckBox("切上满贯");
        inputPanel.add(kiriageBox, gbc);

        gbc.gridx = 1;
        inputPanel.add(new JLabel("晋级条件"), gbc);

        String[] advanceOptions = {"1", "2"};
        JComboBox<String> advanceBox = new JComboBox<>(advanceOptions);
        advanceBox.setPreferredSize(new Dimension(45, 25));
        gbc.gridx = 2;
        inputPanel.add(advanceBox, gbc);

        gbc.gridx = 3;
        inputPanel.add(new JLabel("规则"), gbc);

        String[] ruleOptions = {"A规", "WRC", "M League"};
        JComboBox<String> ruleBox = new JComboBox<>(ruleOptions);
        ruleBox.setPreferredSize(new Dimension(85, 25));
        gbc.gridx = 4;
        inputPanel.add(ruleBox, gbc);

        gbc.gridx = 5;
        inputPanel.add(new JLabel("供托"), gbc);

        kyotakuField.setPreferredSize(new Dimension(50, 25));
        gbc.gridx = 6;
        inputPanel.add(kyotakuField, gbc);

        gbc.gridx = 7;
        inputPanel.add(new JLabel("本场"), gbc);

        honbaField.setPreferredSize(new Dimension(50, 25));
        gbc.gridx = 8;
        inputPanel.add(honbaField, gbc);

        gbc.gridx = 9;
        gbc.gridwidth = 1;
        calcButton.setPreferredSize(new Dimension(65, 29));
        inputPanel.add(calcButton, gbc);

        // 弹性空间
        gbc.gridx = 10;
        gbc.weightx = 1;
        inputPanel.add(Box.createHorizontalGlue(), gbc);
        gbc.weightx = 0;

        // 下方输出区域
        JPanel outputPanel = new JPanel(new GridLayout(2, 2, 12, 12));
        Font resultFont = new Font(Font.MONOSPACED, Font.PLAIN, 13);

        for (int i = 0; i < PLAYER_COUNT; i++) {
            JPanel playerPanel = new JPanel(new BorderLayout(5, 5));
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

        calcButton.addActionListener(e -> {
            String[] playerNames = new String[PLAYER_COUNT];
            Double[] starting = new Double[PLAYER_COUNT];
            Integer[] score = new Integer[PLAYER_COUNT];
            for (int i = 0; i < PLAYER_COUNT; i++) {
                playerNames[i] = nameFields[i].getText().trim();
                try { starting[i] = Double.parseDouble(startingFields[i].getText().trim()); }
                catch (Exception ex) { starting[i] = 0.0; }
                try { score[i] = Integer.parseInt(scoreFields[i].getText().trim()); }
                catch (Exception ex) { score[i] = 25000; }
            }
            int kyotaku = 0, honba = 0;
            try { kyotaku = Integer.parseInt(kyotakuField.getText().trim()); }
            catch (Exception ignored) { }
            try { honba = Integer.parseInt(honbaField.getText().trim()); }
            catch (Exception ignored) { }

            boolean kiriageMangan = kiriageBox.isSelected();
            String ruleType = (String) ruleBox.getSelectedItem();
            Rule rule;
            if ("WRC".equals(ruleType)) rule = new WRCRule();
            else if ("M League".equals(ruleType)) rule = new MLeague();
            else rule = new JPMLRule();
            int winningCondition = advanceBox.getSelectedIndex() + 1;

            for (int i = 0; i < PLAYER_COUNT; i++) {
                String output = ConditionCalculation.calculateCondition(
                        starting, score, i, kyotaku, honba, winningCondition, rule, playerNames, kiriageMangan
                );
                resultAreas[i].setText(playerNames[i] + "晋级条件\n" + output);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MahjongConditionCalculator().setVisible(true));
    }
}
