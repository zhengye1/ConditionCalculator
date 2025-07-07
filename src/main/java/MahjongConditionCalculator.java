import javax.swing.*;
import java.awt.*;

public class MahjongConditionCalculator extends JFrame {
    private static final int PLAYER_COUNT = 4;
    private final JTextField[] nameFields = new JTextField[PLAYER_COUNT];
    private final JTextField[] startingFields = new JTextField[PLAYER_COUNT];
    private final JTextField[] scoreFields = new JTextField[PLAYER_COUNT];
    private final JTextField kyotakuField = new JTextField(5);
    private final JTextField honbaField = new JTextField(5);
    private final JTextArea[] resultAreas = new JTextArea[PLAYER_COUNT];
    // ==== 新增亲家选择 ====
    private final JRadioButton[] dealerButtons = new JRadioButton[PLAYER_COUNT];

    public MahjongConditionCalculator() {
        setTitle("晋级/优胜条件计算");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 720); // 推荐更宽
        setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(7, 12, 7, 12);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;

        Dimension tfDim = new Dimension(68, 25);

        // ==== 亲家选择（单独一行推荐加在最上面） ====
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.gridwidth = PLAYER_COUNT ;
        JPanel dealerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dealerPanel.add(new JLabel("当前亲家："));
        String[] seats = new String[]{"东", "南", "西", "北"};
        ButtonGroup dealerGroup = new ButtonGroup();
        for (int i = 0; i < PLAYER_COUNT; i++) {
            dealerButtons[i] = new JRadioButton(seats[i]);
            dealerGroup.add(dealerButtons[i]);
            dealerPanel.add(dealerButtons[i]);
        }
        dealerButtons[0].setSelected(true); // 默认东家
        inputPanel.add(dealerPanel, gbc);
        gbc.gridwidth = 1;

        // ===== 下面恢复你的输入区 =====
        gbc.gridy = 1;
        gbc.gridx = 0;
        inputPanel.add(new JLabel("座次"), gbc);

        // 表头
        gbc.gridy = 1;
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

        // Starting
        gbc.gridy = 3;
        gbc.gridx = 0;
        // 1. 创建一个小面板把“开始前总分”和问号包起来
        JPanel startLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        startLabelPanel.add(new JLabel("开始前总分"));
        JButton helpStartingBtn = getJButton(inputPanel,
                "开始前总分：指的是这一半庄/一场比赛开打前，各自的积分（包含带入等）。\n如无特殊规定请填0。",
                "开始前总分说明");
        startLabelPanel.add(helpStartingBtn);
        // 用小面板替代单一Label
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

        // 当前分数
        gbc.gridy = 4;
        gbc.gridx = 0;
        // 1. 创建一个小面板把“开始前总分”和问号包起来
        JPanel currentScorePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        currentScorePanel.add(new JLabel("当前分数"));
        JButton helpCurrentScoreBtn = getJButton(inputPanel,
                "当前分数：指的是这一半庄目前的得点。\n如无特殊规定请填25000。",
                "当前分数说明");
        currentScorePanel.add(helpCurrentScoreBtn);
        // 用小面板替代单一Label
        inputPanel.add(currentScorePanel, gbc);
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

        // 下方控件
        gbc.gridy = 5;
        gbc.gridx = 0;
        JCheckBox kiriageBox = new JCheckBox("切上满贯");
        kiriageBox.setEnabled(false);
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

        String[] ruleOptions = {"A规", "WRC", "M League", "WRC-R"};
        JComboBox<String> ruleBox = new JComboBox<>(ruleOptions);
        ruleBox.setPreferredSize(new Dimension(85, 25));
        gbc.gridx = 4;
        inputPanel.add(ruleBox, gbc);

        gbc.gridx = 5;
        inputPanel.add(new JLabel("供托"), gbc);
        // 创建一个小面板装供托输入框和000
        JPanel kyotakuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        kyotakuPanel.add(kyotakuField);
        kyotakuPanel.add(new JLabel("x1000"));
        kyotakuField.setPreferredSize(new Dimension(50, 25));
        gbc.gridx = 6;
        inputPanel.add(kyotakuPanel, gbc);

        gbc.gridx = 7;
        inputPanel.add(new JLabel("本场"), gbc);

        honbaField.setPreferredSize(new Dimension(50, 25));
        gbc.gridx = 8;
        inputPanel.add(honbaField, gbc);

        gbc.gridx = 9;
        gbc.gridwidth = 1;
        JButton calcButton = new JButton("计算");
        calcButton.setPreferredSize(new Dimension(65, 29));
        inputPanel.add(calcButton, gbc);

        gbc.gridx = 10;
        gbc.weightx = 1;
        inputPanel.add(Box.createHorizontalGlue(), gbc);
        gbc.weightx = 0;

        // 输出区域
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

        ruleBox.addActionListener(e -> {
            String ruleType = (String) ruleBox.getSelectedItem();
            kiriageBox.setSelected(!"A规".equals(ruleType));
        });

        // ========= 修改这里：获取当前亲家index并传给算法 =========
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
            try { kyotaku = Integer.parseInt(kyotakuField.getText().trim()) * 1000; }
            catch (Exception ignored) { }
            try { honba = Integer.parseInt(honbaField.getText().trim()); }
            catch (Exception ignored) { }

            boolean kiriageMangan = kiriageBox.isSelected();
            String ruleType = (String) ruleBox.getSelectedItem();
            Rule rule = switch (ruleType) {
                case "WRC" -> new WRCRule();
                case "M League" -> new MLeague();
                case "WRC-R" -> new WRCRRule();
                case null, default -> new JPMLRule();
            };
            int winningCondition = advanceBox.getSelectedIndex() + 1;

            // ==== 这里读取当前亲家是谁 ====
            int dealerIndex = 0;
            for (int i = 0; i < PLAYER_COUNT; i++) {
                if (dealerButtons[i].isSelected()) {
                    dealerIndex = i;
                    break;
                }
            }

            for (int i = 0; i < PLAYER_COUNT; i++) {
                String output = ConditionCalculation.calculateCondition(
                        starting, score, i, kyotaku, honba, winningCondition, rule, playerNames, kiriageMangan, dealerIndex
                );
                resultAreas[i].setText(playerNames[i] + "晋级条件\n" + output);
            }
        });
    }

    private static JButton getJButton(JPanel inputPanel, String description, String title) {
        JButton helpStartingBtn = new JButton("?");
        helpStartingBtn.setMargin(new Insets(0, 3, 0, 3));
        helpStartingBtn.setFocusable(false);
        helpStartingBtn.setToolTipText("点击查看说明");
        helpStartingBtn.addActionListener(e -> JOptionPane.showMessageDialog(
                inputPanel,
                description,
                title,
                JOptionPane.INFORMATION_MESSAGE
        ));
        return helpStartingBtn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MahjongConditionCalculator().setVisible(true));
    }
}
