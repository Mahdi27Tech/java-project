import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Simple Calculator - Java Swing Implementation
 * Matches the classic Windows Calculator layout from the assignment.
 *
 * Features: MC, MR, MS, M+, M-, backspace, CE, C, ±, √, %, 1/x,
 *           digit buttons 0-9, decimal point, and +, -, *, / operators.
 *
 * Compile:  javac Calculator.java
 * Run:      java Calculator
 */
public class Calculator extends JFrame {

    // ── Display ──────────────────────────────────────────────────────────────
    private JTextField display;

    // ── State ─────────────────────────────────────────────────────────────────
    private double  memory      = 0;
    private double  operand     = 0;
    private String  operator    = "";
    private boolean startNew    = true;   // next digit press starts a fresh number
    private boolean justEvaled  = false;  // '=' was the last action

    // ── Constructor ───────────────────────────────────────────────────────────
    public Calculator() {
        setTitle("Calculator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // ── Overall panel with classic raised-bevel feel ──────────────────
        JPanel root = new JPanel(new BorderLayout(4, 4));
        root.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)));
        root.setBackground(new Color(242,242,242));

        // ── Menu bar ──────────────────────────────────────────────────────
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(242,242,242));
        menuBar.add(makeMenu("View"));
        menuBar.add(makeMenu("Edit"));
        menuBar.add(makeMenu("Help"));
        setJMenuBar(menuBar);

        // ── Display field ─────────────────────────────────────────────────
        display = new JTextField("0");
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.PLAIN, 22));
        display.setBackground(Color.WHITE);
        display.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLoweredBevelBorder(),
                BorderFactory.createEmptyBorder(2, 4, 2, 4)));
        display.setPreferredSize(new Dimension(300, 40));
        root.add(display, BorderLayout.NORTH);

        // ── Button grid ───────────────────────────────────────────────────
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setBackground(new Color(242,242,242));
        GridBagConstraints g = new GridBagConstraints();
        g.fill    = GridBagConstraints.BOTH;
        g.insets  = new Insets(2, 2, 2, 2);
        g.weightx = 1;
        g.weighty = 1;

        // Row 0 – memory buttons (salmon / pink style)
        String[] memRow = {"MC", "MR", "MS", "M+", "M-"};
        for (int c = 0; c < memRow.length; c++) {
            g.gridx = c; g.gridy = 0;
            grid.add(makeBtn(memRow[c], Color.WHITE, new Color(180, 90, 60)), g);
        }

        // Row 1 – editing / special
        //   col 0: ← (backspace)
        //   col 1: CE
        //   col 2: C
        //   col 3: ±
        //   col 4: √
        String[][] row1 = {{"←","←"}, {"CE","CE"}, {"C","C"}, {"±","±"}, {"√","√"}};
        Color editBg = new Color(230, 220, 210);
        for (int c = 0; c < row1.length; c++) {
            g.gridx = c; g.gridy = 1;
            grid.add(makeBtn(row1[c][0], editBg, Color.BLACK), g);
        }

        // Row 2 – 7 8 9  /  %
        String[][] row2 = {{"7"}, {"8"}, {"9"}, {"/"}, {"%"}};
        for (int c = 0; c < row2.length; c++) {
            g.gridx = c; g.gridy = 2;
            Color bg = (c >= 3) ? editBg : new Color(242,242,242);
            grid.add(makeBtn(row2[c][0], bg, Color.BLACK), g);
        }

        // Row 3 – 4 5 6  *  1/x
        String[][] row3 = {{"4"}, {"5"}, {"6"}, {"*"}, {"1/x"}};
        for (int c = 0; c < row3.length; c++) {
            g.gridx = c; g.gridy = 3;
            Color bg = (c >= 3) ? editBg : new Color(242,242,242);
            grid.add(makeBtn(row3[c][0], bg, Color.BLACK), g);
        }

        // Row 4 – 1 2 3  -  (= spans rows 4-5)
        String[][] row4 = {{"1"}, {"2"}, {"3"}, {"-"}};
        for (int c = 0; c < row4.length; c++) {
            g.gridx = c; g.gridy = 4;
            Color bg = (c == 3) ? editBg : new Color(242,242,242);
            grid.add(makeBtn(row4[c][0], bg, Color.BLACK), g);
        }
        // "=" button spans rows 4-5, column 4
        g.gridx = 4; g.gridy = 4; g.gridheight = 2;
        grid.add(makeBtn("=", editBg, Color.BLACK), g);
        g.gridheight = 1;

        // Row 5 – 0 (wide)  .  +
        g.gridx = 0; g.gridy = 5; g.gridwidth = 2;
        grid.add(makeBtn("0", new Color(242,242,242), Color.BLACK), g);
        g.gridwidth = 1;

        g.gridx = 2; g.gridy = 5;
        grid.add(makeBtn(".", new Color(242,242,242), Color.BLACK), g);

        g.gridx = 3; g.gridy = 5;
        grid.add(makeBtn("+", editBg, Color.BLACK), g);

        root.add(grid, BorderLayout.CENTER);
        add(root);
        pack();
        setMinimumSize(new Dimension(320, 300));
        setLocationRelativeTo(null);
    }

    // ── Button factory ────────────────────────────────────────────────────────
    private JButton makeBtn(String label, Color bg, Color fg) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createRaisedBevelBorder());
        btn.setPreferredSize(new Dimension(56, 36));
        btn.addActionListener(e -> handleAction(label));
        return btn;
    }

    // ── Menu factory ─────────────────────────────────────────────────────────
    private JMenu makeMenu(String name) {
        JMenu m = new JMenu(name);
        m.setFont(new Font("Arial", Font.PLAIN, 12));
        return m;
    }

    // ── Core logic ────────────────────────────────────────────────────────────
    private void handleAction(String cmd) {
        switch (cmd) {
            // ── Digits ──────────────────────────────────────────────────────
            case "0": case "1": case "2": case "3": case "4":
            case "5": case "6": case "7": case "8": case "9":
                if (startNew || justEvaled) {
                    display.setText(cmd.equals("0") ? "0" : cmd);
                    startNew   = false;
                    justEvaled = false;
                } else {
                    String cur = display.getText();
                    display.setText(cur.equals("0") ? cmd : cur + cmd);
                }
                break;

            // ── Decimal ─────────────────────────────────────────────────────
            case ".":
                if (startNew || justEvaled) {
                    display.setText("0.");
                    startNew   = false;
                    justEvaled = false;
                } else if (!display.getText().contains(".")) {
                    display.setText(display.getText() + ".");
                }
                break;

            // ── Operators ───────────────────────────────────────────────────
            case "+": case "-": case "*": case "/":
                if (!startNew) {
                    calculate();
                }
                operand  = parseDisplay();
                operator = cmd;
                startNew = true;
                break;

            // ── Equals ──────────────────────────────────────────────────────
            case "=":
                calculate();
                operator   = "";
                startNew   = true;
                justEvaled = true;
                break;

            // ── Editing ─────────────────────────────────────────────────────
            case "←": {   // backspace
                String s = display.getText();
                if (s.length() > 1) {
                    String trimmed = s.substring(0, s.length() - 1);
                    display.setText(trimmed.equals("-") ? "0" : trimmed);
                } else {
                    display.setText("0");
                }
                break;
            }
            case "CE":
                display.setText("0");
                startNew = false;
                break;
            case "C":
                display.setText("0");
                operand  = 0;
                operator = "";
                startNew = true;
                break;

            // ── Unary ───────────────────────────────────────────────────────
            case "±":
                double negVal = -parseDisplay();
                display.setText(formatNum(negVal));
                break;

            case "√": {
                double v = parseDisplay();
                if (v < 0) { display.setText("Error"); }
                else        { display.setText(formatNum(Math.sqrt(v))); }
                startNew = true;
                break;
            }
            case "%":
                display.setText(formatNum(operand * parseDisplay() / 100));
                startNew = true;
                break;

            case "1/x": {
                double d = parseDisplay();
                if (d == 0) { display.setText("Cannot divide by zero"); }
                else        { display.setText(formatNum(1.0 / d)); }
                startNew = true;
                break;
            }

            // ── Memory ──────────────────────────────────────────────────────
            case "MC": memory = 0;                                   break;
            case "MR": display.setText(formatNum(memory)); startNew = true; break;
            case "MS": memory = parseDisplay(); startNew = true;     break;
            case "M+": memory += parseDisplay(); startNew = true;    break;
            case "M-": memory -= parseDisplay(); startNew = true;    break;
        }
    }

    private void calculate() {
        if (operator.isEmpty()) return;
        double current = parseDisplay();
        double result;
        switch (operator) {
            case "+": result = operand + current; break;
            case "-": result = operand - current; break;
            case "*": result = operand * current; break;
            case "/":
                if (current == 0) { display.setText("Cannot divide by zero"); return; }
                result = operand / current;
                break;
            default: return;
        }
        display.setText(formatNum(result));
        operand = result;
    }

    private double parseDisplay() {
        try { return Double.parseDouble(display.getText()); }
        catch (NumberFormatException e) { return 0; }
    }

    /** Show whole numbers without ".0", trim trailing zeros otherwise. */
    private String formatNum(double v) {
        if (v == Math.floor(v) && !Double.isInfinite(v) && Math.abs(v) < 1e15)
            return String.valueOf((long) v);
        // Remove unnecessary trailing zeros
        String s = String.valueOf(v);
        return s;
    }

    // ── Entry point ───────────────────────────────────────────────────────────
    public static void main(String[] args) {
        // Use system look-and-feel for authentic Windows feel
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new Calculator().setVisible(true));
    }
}
