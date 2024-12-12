import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import java.util.Arrays;
import java.util.Stack;
import java.awt.Color;



public class WeightConverter extends JFrame {


    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textField;
    private String[] history = new String[10]; // Array for storing history
    private int historyCount = 0; // Tracks the number of entries in history
    private Stack<String> undoStack = new Stack<>(); // Stack for undo functionality

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                WeightConverter frame = new WeightConverter();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public WeightConverter() {
        setTitle("WeightConverter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 400);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(169, 169, 169));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        textField = new JTextField();
        textField.setBounds(231, 24, 117, 20);
        contentPane.add(textField);
        textField.setColumns(10);

        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setModel(new DefaultComboBoxModel<>(new String[]{
            "Select a choice", "Kilogram", "Pounds", "Ounces", "Grams", "Milligrams"
        }));
        comboBox.setBounds(155, 55, 131, 22);
        contentPane.add(comboBox);

        JTextArea txtrFrom = new JTextArea("From");
        txtrFrom.setBackground(new Color(169, 169, 169));
        txtrFrom.setBounds(117, 54, 40, 22);
        contentPane.add(txtrFrom);
        
        JTextArea txtrTo = new JTextArea();
        txtrTo.setBackground(new Color(169, 169, 169));
        txtrTo.setText("To");
        txtrTo.setBounds(306, 54, 31, 22);
        contentPane.add(txtrTo);

        JComboBox<String> comboBox_1 = new JComboBox<>();
        comboBox_1.setModel(new DefaultComboBoxModel<>(new String[]{
            "Select a choice", "Kilogram", "Pounds", "Ounces", "Grams", "Milligrams"
        }));
        comboBox_1.setBounds(337, 55, 131, 22);
        contentPane.add(comboBox_1);

        JButton btnCalculate = new JButton("Calculate");
        btnCalculate.setBounds(30, 89, 138, 23);
        contentPane.add(btnCalculate);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBounds(225, 88, 300, 22);
        contentPane.add(textArea);

        JButton btnShowHistory = new JButton("Show History");
        btnShowHistory.setBounds(30, 127, 138, 23);
        contentPane.add(btnShowHistory);

        JTextArea textAreaHistory = new JTextArea();
        textAreaHistory.setEditable(false);
        textAreaHistory.setBounds(225, 126, 300, 198);
        contentPane.add(textAreaHistory);

        JButton btnUndo = new JButton("Undo Last Conversion");
        btnUndo.setBounds(30, 161, 180, 23);
        contentPane.add(btnUndo);

        JButton btnSortAsc = new JButton("Sort History (Asc)");
        btnSortAsc.setBounds(30, 195, 180, 23);
        contentPane.add(btnSortAsc);

        JButton btnSortDesc = new JButton("Sort History (Desc)");
        btnSortDesc.setBounds(30, 229, 180, 23);
        contentPane.add(btnSortDesc);

        // Action listener for "Calculate" button
        btnCalculate.addActionListener(e -> {
            String fromUnit = (String) comboBox.getSelectedItem();
            String toUnit = (String) comboBox_1.getSelectedItem();
            String inputText = textField.getText();

            if (fromUnit.equals("Select a choice") || toUnit.equals("Select a choice") || inputText.isEmpty()) {
                return;
            }

            try {
                double value = Double.parseDouble(inputText);
                double convertedValue = convertWeight(value, fromUnit, toUnit);
                String result = String.format("%.2f %s = %.2f %s", value, fromUnit, convertedValue, toUnit);
                textArea.setText(result);
                addToHistory(result);
                undoStack.push(result); // Add the result to the undo stack
            } catch (NumberFormatException ex) {
                textArea.setText("Invalid number format!");
            }
        });

        // Action listener for "Show History" button
        btnShowHistory.addActionListener(e -> textAreaHistory.setText(getHistory()));

        // Action listener for "Undo Last Conversion" button
        btnUndo.addActionListener(e -> {
            if (!undoStack.isEmpty()) {
                undoStack.pop(); // Remove the last conversion
                removeLastHistory(); // Remove it from the history array
                textAreaHistory.setText(getHistory()); // Update history display
            } else {
                textAreaHistory.setText("Nothing to undo!");
            }
        });

        // Action listener for "Sort History (Asc)" button
        btnSortAsc.addActionListener(e -> {
            sortHistory(true);
            textAreaHistory.setText(getHistory());
        });

        // Action listener for "Sort History (Desc)" button
        btnSortDesc.addActionListener(e -> {
            sortHistory(false);
            textAreaHistory.setText(getHistory());
            
        });
    }

    private double convertWeight(double value, String fromUnit, String toUnit) {
        double kgValue = switch (fromUnit) {
            case "Kilogram" -> value;
            case "Pounds" -> value * 0.453592;
            case "Ounces" -> value * 0.0283495;
            case "Grams" -> value / 1000;
            case "Milligrams" -> value / 1_000_000;
            default -> throw new IllegalArgumentException("Invalid unit");
        };

        return switch (toUnit) {
            case "Kilogram" -> kgValue;
            case "Pounds" -> kgValue / 0.453592;
            case "Ounces" -> kgValue / 0.0283495;
            case "Grams" -> kgValue * 1000;
            case "Milligrams" -> kgValue * 1_000_000;
            default -> throw new IllegalArgumentException("Invalid unit");
        };
    }

    private void addToHistory(String result) {
        if (historyCount < history.length) {
            history[historyCount++] = result;
        }
    }

    private void removeLastHistory() {
        if (historyCount > 0) {
            history[--historyCount] = null;
        }
    }

    private String getHistory() {
        StringBuilder historyText = new StringBuilder("Conversion History:\n");
        for (int i = 0; i < historyCount; i++) {
            historyText.append(history[i]).append("\n");
        }
        return historyText.toString();
    }

    private void sortHistory(boolean ascending) {
        String[] validHistory = Arrays.copyOf(history, historyCount);
        Arrays.sort(validHistory);
        if (!ascending) {
            for (int i = 0; i < validHistory.length / 2; i++) {
                String temp = validHistory[i];
                validHistory[i] = validHistory[validHistory.length - 1 - i];
                validHistory[validHistory.length - 1 - i] = temp;
            }
        }
        System.arraycopy(validHistory, 0, history, 0, historyCount);
    }
}
