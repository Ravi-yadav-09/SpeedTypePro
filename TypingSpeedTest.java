// TypingSpeedTest.java
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TypingSpeedTest extends JFrame {
    // Color Constants
    private static final Color PRIMARY_LIGHT = new Color(37, 99, 235);
    private static final Color BG_LIGHT = new Color(248, 250, 252);
    private static final Color TEXT_LIGHT = new Color(30, 41, 59);
    private static final Color CARD_LIGHT = new Color(226, 232, 240);

    private static final Color PRIMARY_DARK = new Color(59, 130, 246);
    private static final Color BG_DARK = new Color(30, 41, 59);
    private static final Color TEXT_DARK = new Color(248, 250, 252);
    private static final Color CARD_DARK = new Color(51, 65, 85);

    // Test sentences
    private final String[] sentences = {
            "The quick brown fox jumps over the lazy dog.",
            "Java is a high-level programming language.",
            "Practice makes perfect.",
            "Typing fast is a useful skill.",
            "Never stop learning and improving."
    };

    // UI Components
    private JLabel sentenceLabel, resultLabel, timerLabel, liveWPMLabel, accuracyLabel;
    private JTextField inputField;
    private JButton startButton, resetButton, refreshSentenceButton, viewHistoryButton;
    private JComboBox<String> timeSelector;
    private JCheckBox darkModeToggle;

    // Game state
    private String currentSentence;
    private long startTime;
    private Timer timer;
    private int timeLeft;
    private boolean isRunning;
    private final List<TestResult> history = new ArrayList<>();

    // Result record
    private record TestResult(int wpm, double accuracy, String date) {
        TestResult(int wpm, double accuracy) {
            this(wpm, accuracy, new java.util.Date().toString());
        }
    }

    public TypingSpeedTest() {
        initializeUI();
        setupEventHandlers();
        updateUITheme();
        setVisible(true);
    }

    private void initializeUI() {
        setTitle("Typing Master");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(850, 550);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("Typing Speed Test");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        darkModeToggle = new JCheckBox("Dark Mode");
        darkModeToggle.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(darkModeToggle, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        timerLabel = createStatLabel("0");
        liveWPMLabel = createStatLabel("0");
        accuracyLabel = createStatLabel("0%");

        statsPanel.add(createStatCard("TIME REMAINING", timerLabel));
        statsPanel.add(createStatCard("CURRENT WPM", liveWPMLabel));
        statsPanel.add(createStatCard("ACCURACY", accuracyLabel));
        contentPanel.add(statsPanel, BorderLayout.NORTH);

        // Sentence Display
        sentenceLabel = new JLabel("Click 'Start' to begin!", SwingConstants.CENTER);
        sentenceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        JScrollPane sentenceScroll = new JScrollPane(sentenceLabel);
        sentenceScroll.setBorder(BorderFactory.createEmptyBorder());
        contentPanel.add(sentenceScroll, BorderLayout.CENTER);

        // Input Field
        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        inputField.setHorizontalAlignment(JTextField.CENTER);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        contentPanel.add(inputField, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

        // Control Panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        timeSelector = new JComboBox<>(new String[]{"15", "30", "60"});
        timeSelector.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        startButton = createControlButton("START", PRIMARY_LIGHT);
        resetButton = createControlButton("RESET", new Color(239, 68, 68));
        refreshSentenceButton = createControlButton("NEW SENTENCE", new Color(59, 130, 246));
        viewHistoryButton = createControlButton("VIEW HISTORY", new Color(139, 92, 246));

        controlPanel.add(timeSelector);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(startButton);
        controlPanel.add(Box.createHorizontalStrut(5));
        controlPanel.add(resetButton);
        controlPanel.add(Box.createHorizontalStrut(5));
        controlPanel.add(refreshSentenceButton);
        controlPanel.add(Box.createHorizontalStrut(5));
        controlPanel.add(viewHistoryButton);

        // Result Label
        resultLabel = new JLabel(" ", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        // Combine controlPanel + resultLabel into one south panel
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(controlPanel, BorderLayout.CENTER);
        southPanel.add(resultLabel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

        inputField.setEnabled(false);
    }

    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        return label;
    }

    private JPanel createStatCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(15, 10, 15, 10)
        ));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JButton createControlButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return button;
    }

    private void setupEventHandlers() {
        startButton.addActionListener(e -> startTest());
        resetButton.addActionListener(e -> resetTest());
        refreshSentenceButton.addActionListener(e -> refreshSentence());
        viewHistoryButton.addActionListener(e -> showHistory());
        darkModeToggle.addActionListener(e -> updateUITheme());

        inputField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (isRunning) {
                    checkLiveStats();
                    updateSentenceDisplay();
                }
            }
        });
    }

    private void refreshSentence() {
        currentSentence = sentences[(int) (Math.random() * sentences.length)];
        sentenceLabel.setText("<html><div style='text-align:center'>" + currentSentence + "</div></html>");
        inputField.setText("");
        accuracyLabel.setText("0%");
        updateSentenceDisplay();
    }

    private void startTest() {
        if (isRunning) return;

        refreshSentence();
        isRunning = true;
        inputField.setEnabled(true);
        inputField.requestFocus();

        timeLeft = Integer.parseInt((String) timeSelector.getSelectedItem());
        timerLabel.setText(String.valueOf(timeLeft));
        startTime = System.currentTimeMillis();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    if (--timeLeft <= 0) {
                        finishTest();
                        timerLabel.setText("0");
                    } else {
                        timerLabel.setText(String.valueOf(timeLeft));
                    }
                });
            }
        }, 1000, 1000);
    }

    private void checkLiveStats() {
        String typed = inputField.getText();
        double elapsedMinutes = (System.currentTimeMillis() - startTime) / 60000.0;

        int wordCount = typed.trim().isEmpty() ? 0 : typed.trim().split("\\s+").length;
        int wpm = (int) (wordCount / Math.max(elapsedMinutes, 0.0167));

        double accuracy = calculateAccuracy(typed);

        liveWPMLabel.setText(String.valueOf(wpm));
        accuracyLabel.setText(String.format("%.0f%%", accuracy));
    }

    private double calculateAccuracy(String typed) {
        if (typed.isEmpty() || currentSentence == null) return 0;

        int correctChars = 0;
        int minLength = Math.min(typed.length(), currentSentence.length());

        for (int i = 0; i < minLength; i++) {
            if (typed.charAt(i) == currentSentence.charAt(i)) {
                correctChars++;
            }
        }

        return (correctChars * 100.0) / currentSentence.length();
    }

    private void updateSentenceDisplay() {
        if (currentSentence == null) return;

        String typed = inputField.getText();
        StringBuilder coloredText = new StringBuilder("<html><div style='text-align:center'>");

        for (int i = 0; i < currentSentence.length(); i++) {
            if (i < typed.length()) {
                boolean correct = typed.charAt(i) == currentSentence.charAt(i);
                String color = correct ? "#4ade80" : "#f87171";
                coloredText.append("<span style='color:").append(color).append("'>")
                        .append(currentSentence.charAt(i))
                        .append("</span>");
            } else {
                coloredText.append(currentSentence.charAt(i));
            }
        }

        coloredText.append("</div></html>");
        sentenceLabel.setText(coloredText.toString());
    }

    private void finishTest() {
        timer.cancel();
        isRunning = false;
        inputField.setEnabled(false);

        String typed = inputField.getText();
        double elapsedMinutes = (System.currentTimeMillis() - startTime) / 60000.0;
        int wordCount = typed.trim().isEmpty() ? 0 : typed.trim().split("\\s+").length;
        int finalWPM = (int) (wordCount / Math.max(elapsedMinutes, 0.0167));
        double finalAccuracy = calculateAccuracy(typed);

        liveWPMLabel.setText(String.valueOf(finalWPM));
        accuracyLabel.setText(String.format("%.0f%%", finalAccuracy));
        resultLabel.setText(String.format("Test Complete! %d WPM • %.0f%% Accuracy", finalWPM, finalAccuracy));

        history.add(new TestResult(finalWPM, finalAccuracy));
    }

    private void resetTest() {
        if (timer != null) timer.cancel();
        isRunning = false;
        inputField.setText("");
        resultLabel.setText(" ");
        liveWPMLabel.setText("0");
        accuracyLabel.setText("0%");
        timerLabel.setText("0");
        inputField.setEnabled(false);
        sentenceLabel.setText("Click 'Start' to begin!");
    }

    private void updateUITheme() {
        boolean dark = darkModeToggle.isSelected();
        Color bg = dark ? BG_DARK : BG_LIGHT;
        Color fg = dark ? TEXT_DARK : TEXT_LIGHT;
        Color cardBg = dark ? CARD_DARK : CARD_LIGHT;
        Color primary = dark ? PRIMARY_DARK : PRIMARY_LIGHT;

        getContentPane().setBackground(bg);

        Component[] components = getContentPane().getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                comp.setBackground(bg);
                for (Component subComp : ((JPanel) comp).getComponents()) {
                    subComp.setBackground(cardBg);
                    subComp.setForeground(fg);
                    if (subComp instanceof JScrollPane) {
                        subComp.setBackground(bg);
                    }
                }
            }
            comp.setBackground(bg);
            comp.setForeground(fg);
        }

        inputField.setBackground(dark ? new Color(51, 65, 85) : Color.white);
        inputField.setForeground(fg);

        startButton.setBackground(primary);
        startButton.setForeground(Color.blue);

        resetButton.setBackground(dark ? new Color(239, 68, 68) : new Color(220, 38, 38));
        resetButton.setForeground(Color.BLUE);

        refreshSentenceButton.setBackground(dark ? new Color(59, 130, 246) : new Color(37, 99, 235));
        refreshSentenceButton.setForeground(Color.blue);

        viewHistoryButton.setBackground(dark ? new Color(139, 92, 246) : new Color(124, 58, 237));
        viewHistoryButton.setForeground(Color.blue);

    }

    private void showHistory() {
        if (history.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No test history yet.", "History", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog historyDialog = new JDialog(this, "Test History", true);
        historyDialog.setSize(600, 400);
        historyDialog.setLayout(new BorderLayout());
        historyDialog.setLocationRelativeTo(this);

        String[] columns = {"Date", "WPM", "Accuracy"};
        Object[][] data = new Object[history.size()][3];

        for (int i = 0; i < history.size(); i++) {
            TestResult result = history.get(i);
            data[i][0] = result.date.substring(0, 16);
            data[i][1] = result.wpm;
            data[i][2] = String.format("%.0f%%", result.accuracy);
        }

        JTable table = new JTable(data, columns);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(table);
        historyDialog.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> historyDialog.dispose());
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        historyDialog.add(buttonPanel, BorderLayout.SOUTH);

        historyDialog.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new TypingSpeedTest());
    }
}
