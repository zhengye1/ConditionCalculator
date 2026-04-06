import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FormulaCalculator extends JFrame {
    private static final int PLAYER_COUNT = 4;
    private final JTextField[] nameFields = new JTextField[PLAYER_COUNT];
    private final JTextField[] startingFields = new JTextField[PLAYER_COUNT];
    private final JTextField[] scoreFields = new JTextField[PLAYER_COUNT];
    @SuppressWarnings("unchecked")
    private final JComboBox<String>[] rankBoxes = new JComboBox[PLAYER_COUNT];
    private final JTextField kyotakuField = new JTextField(5);
    private final JTextField honbaField = new JTextField(5);
    private final JTextArea[] resultAreas = new JTextArea[PLAYER_COUNT];
    private final JRadioButton[] dealerButtons = new JRadioButton[PLAYER_COUNT];

    public FormulaCalculator() {
        setTitle("公式战条件计算器");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 12, 7, 12);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        Dimension tfDim = new Dimension(68, 25);

        // 亲家选择（起家）
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.gridwidth = PLAYER_COUNT;
        JPanel dealerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dealerPanel.add(new JLabel("当前亲家："));
        String[] seats = {"东", "南", "西", "北"};
        ButtonGroup dealerGroup = new ButtonGroup();
        for (int i = 0; i < PLAYER_COUNT; i++) {
            dealerButtons[i] = new JRadioButton(seats[i]);
            dealerGroup.add(dealerButtons[i]);
            dealerPanel.add(dealerButtons[i]);
        }
        dealerButtons[3].setSelected(true);
        inputPanel.add(dealerPanel, gbc);
        gbc.gridwidth = 1;

        // 表头
        gbc.gridy = 1;
        gbc.gridx = 0;
        inputPanel.add(new JLabel("座次"), gbc);
        for (int i = 0; i < PLAYER_COUNT; i++) {
            gbc.gridx = i + 1;
            inputPanel.add(new JLabel(seats[i]), gbc);
        }
        gbc.gridx = PLAYER_COUNT + 2;
        gbc.weightx = 1;
        inputPanel.add(Box.createHorizontalGlue(), gbc);
        gbc.weightx = 0;

        // 名字
        gbc.gridy = 2;
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

        // 第一半庄总分 (starting)
        gbc.gridy = 3;
        gbc.gridx = 0;
        JPanel startLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        startLabelPanel.add(new JLabel("第一半庄总分"));
        JButton helpStartingBtn = createHelpButton(inputPanel,
                "第一半庄总分：指的是第一半庄结束后，各自的最终得分。\n如无特殊规定请填0。",
                "第一半庄总分说明");
        startLabelPanel.add(helpStartingBtn);
        inputPanel.add(startLabelPanel, gbc);
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

        // 第一半庄名次
        gbc.gridy = 4;
        gbc.gridx = 0;
        JPanel rankLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rankLabelPanel.add(new JLabel("第一半庄名次"));
        JButton helpRankBtn = createHelpButton(inputPanel,
                "第一半庄名次：第一半庄结束后各玩家的最终排名（1-4）。\n用于同分时的Tie Breaker。",
                "第一半庄名次说明");
        rankLabelPanel.add(helpRankBtn);
        inputPanel.add(rankLabelPanel, gbc);
        String[] rankOptions = {"1", "2", "3", "4"};
        for (int i = 0; i < PLAYER_COUNT; i++) {
            rankBoxes[i] = new JComboBox<>(rankOptions);
            rankBoxes[i].setSelectedIndex(i);
            rankBoxes[i].setPreferredSize(new Dimension(45, 25));
            gbc.gridx = i + 1;
            inputPanel.add(rankBoxes[i], gbc);
        }
        gbc.gridx = PLAYER_COUNT + 2;
        gbc.weightx = 1;
        inputPanel.add(Box.createHorizontalGlue(), gbc);
        gbc.weightx = 0;

        // 当前分数
        gbc.gridy = 5;
        gbc.gridx = 0;
        JPanel currentScorePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        currentScorePanel.add(new JLabel("当前分数"));
        JButton helpCurrentScoreBtn = createHelpButton(inputPanel,
                "当前分数：指的是第二半庄目前的得点。\n默认为25000。",
                "当前分数说明");
        currentScorePanel.add(helpCurrentScoreBtn);
        inputPanel.add(currentScorePanel, gbc);
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

        // 下方控件
        gbc.gridy = 6;
        gbc.gridx = 0;
        inputPanel.add(new JLabel("规则：15-5 切上满贯"), gbc);

        gbc.gridx = 1;
        inputPanel.add(new JLabel("目标顺位"), gbc);

        String[] advanceOptions = {"1", "2", "3"};
        JComboBox<String> advanceBox = new JComboBox<>(advanceOptions);
        advanceBox.setPreferredSize(new Dimension(45, 25));
        gbc.gridx = 2;
        inputPanel.add(advanceBox, gbc);

        gbc.gridx = 3;
        inputPanel.add(new JLabel("供托"), gbc);

        JPanel kyotakuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        kyotakuPanel.add(kyotakuField);
        kyotakuPanel.add(new JLabel("x1000"));
        kyotakuField.setPreferredSize(new Dimension(50, 25));
        gbc.gridx = 4;
        inputPanel.add(kyotakuPanel, gbc);

        gbc.gridx = 5;
        inputPanel.add(new JLabel("本场"), gbc);

        honbaField.setPreferredSize(new Dimension(50, 25));
        gbc.gridx = 6;
        inputPanel.add(honbaField, gbc);

        gbc.gridx = 7;
        JButton calcButton = new JButton("计算");
        calcButton.setPreferredSize(new Dimension(65, 29));
        inputPanel.add(calcButton, gbc);

        gbc.gridx = 8;
        JButton dangerButton = new JButton("危险区间");
        dangerButton.setPreferredSize(new Dimension(85, 29));
        inputPanel.add(dangerButton, gbc);

        gbc.gridx = 9;
        gbc.weightx = 1;
        inputPanel.add(Box.createHorizontalGlue(), gbc);
        gbc.weightx = 0;

        // 听牌分支输出
        JTextArea tenpaiArea = new JTextArea(17, 42);
        tenpaiArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        tenpaiArea.setLineWrap(true);
        tenpaiArea.setWrapStyleWord(true);
        tenpaiArea.setEditable(false);

        // 结果输出
        JPanel outputPanel = new JPanel(new GridLayout(1, 5, 12, 12));
        for (int i = 0; i < PLAYER_COUNT; i++) {
            JPanel playerPanel = new JPanel(new BorderLayout(5, 5));
            resultAreas[i] = new JTextArea(7, 22);
            resultAreas[i].setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
            resultAreas[i].setLineWrap(true);
            resultAreas[i].setWrapStyleWord(true);
            resultAreas[i].setEditable(false);
            JScrollPane scroll = new JScrollPane(resultAreas[i]);
            playerPanel.add(scroll, BorderLayout.CENTER);
            outputPanel.add(playerPanel);
        }
        JScrollPane tenpaiScroll = new JScrollPane(tenpaiArea);
        outputPanel.add(tenpaiScroll);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(inputPanel, BorderLayout.NORTH);
        getContentPane().add(outputPanel, BorderLayout.CENTER);

        FormulaRule rule = new FormulaRule();

        calcButton.addActionListener(e -> {
            InputData input = parseInput();
            int winningCondition = advanceBox.getSelectedIndex() + 1;

            for (int i = 0; i < PLAYER_COUNT; i++) {
                String output = FormulaConditionCalculation.calculateCondition(
                        input.starting, input.score, i, input.kyotaku, input.honba,
                        winningCondition, rule, input.playerNames, input.dealerIndex,
                        input.firstHanchanRank
                );
                resultAreas[i].setText(input.playerNames[i] + "条件\n" + output);
            }

            List<FormulaTenpaiAdvanceDecision.DecisionResult> results =
                    FormulaTenpaiAdvanceDecision.getAllTenpaiAdvance(
                            input.starting, input.score, rule, input.playerNames,
                            winningCondition, input.dealerIndex, input.firstHanchanRank
                    );
            StringBuilder sb = new StringBuilder("【听牌分支一览】\n");
            for (FormulaTenpaiAdvanceDecision.DecisionResult r : results) {
                sb.append(r).append("\n");
            }
            tenpaiArea.setText(sb.toString());
        });

        dangerButton.addActionListener(e -> {
            InputData input = parseInput();
            int winningCondition = advanceBox.getSelectedIndex() + 1;

            FormulaDangerDialog dialog = new FormulaDangerDialog(
                    this, input.starting, input.score, input.kyotaku, input.honba,
                    winningCondition, rule, input.playerNames, input.dealerIndex,
                    input.firstHanchanRank
            );
            dialog.setVisible(true);
        });
    }

    private InputData parseInput() {
        String[] playerNames = new String[PLAYER_COUNT];
        Double[] starting = new Double[PLAYER_COUNT];
        Integer[] score = new Integer[PLAYER_COUNT];
        int[] firstHanchanRank = new int[PLAYER_COUNT];
        for (int i = 0; i < PLAYER_COUNT; i++) {
            playerNames[i] = nameFields[i].getText().trim();
            try { starting[i] = Double.parseDouble(startingFields[i].getText().trim()); }
            catch (Exception ex) { starting[i] = 0.0; }
            try { score[i] = Integer.parseInt(scoreFields[i].getText().trim()); }
            catch (Exception ex) { score[i] = 25000; }
            firstHanchanRank[i] = rankBoxes[i].getSelectedIndex() + 1;
        }
        int kyotaku = 0, honba = 0;
        try { kyotaku = Integer.parseInt(kyotakuField.getText().trim()) * 1000; }
        catch (Exception ignored) {}
        try { honba = Integer.parseInt(honbaField.getText().trim()); }
        catch (Exception ignored) {}

        int dealerIndex = 3;
        for (int i = 0; i < PLAYER_COUNT; i++) {
            if (dealerButtons[i].isSelected()) {
                dealerIndex = i;
                break;
            }
        }
        return new InputData(playerNames, starting, score, kyotaku, honba, dealerIndex, firstHanchanRank);
    }

    private static JButton createHelpButton(JPanel parent, String description, String title) {
        JButton btn = new JButton("?");
        btn.setMargin(new Insets(0, 3, 0, 3));
        btn.setFocusable(false);
        btn.setToolTipText("点击查看说明");
        btn.addActionListener(e -> JOptionPane.showMessageDialog(parent, description, title, JOptionPane.INFORMATION_MESSAGE));
        return btn;
    }

    private record InputData(String[] playerNames, Double[] starting, Integer[] score, int kyotaku, int honba, int dealerIndex, int[] firstHanchanRank) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FormulaCalculator().setVisible(true));
    }
}
