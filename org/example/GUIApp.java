package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class GUIApp extends JFrame {
    private JTable inputTable;
    private JTable outputTable;

    private final Color accentColor = new Color(52, 152, 219);
    private final Color successColor = new Color(39, 174, 96);
    private final Font headerFont = new Font("Arial", Font.BOLD, 14);

    private JButton clearButton = new JButton("Очистить");
    private JButton loadButton = new JButton("Загрузить из файла");
    private JButton saveResultButton = new JButton("Сохранить результат");
    private JButton processButton = new JButton("Решить");

    private JButton addRowButton = new JButton("+");
    private JButton removeRowButton = new JButton("-");
    private JButton addColButton = new JButton("+");
    private JButton removeColButton = new JButton("-");

    public GUIApp() {
        super("Задача 8: Удаление повторяющихся строк");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // --- Инициализация и настройка таблиц ---
        inputTable = new JTable(createEmptyTableModel(0, 5));
        outputTable = new JTable(createEmptyTableModel(0, 0));
        outputTable.setEnabled(false);
        outputTable.setBackground(new Color(240, 240, 255));

        inputTable.getTableHeader().setReorderingAllowed(false);
        outputTable.getTableHeader().setReorderingAllowed(false);

        makeButtonStylish(loadButton, accentColor);
        makeButtonStylish(processButton, successColor);
        makeButtonStylish(saveResultButton, accentColor);
        makeButtonStylish(clearButton, Color.GRAY);

        makeButtonStylish(addRowButton, new Color(102, 187, 106));
        makeButtonStylish(removeRowButton, new Color(239, 83, 80));
        makeButtonStylish(addColButton, new Color(102, 187, 106));
        makeButtonStylish(removeColButton, new Color(239, 83, 80));

        Dimension smallButtonSize = new Dimension(45, 25);
        addRowButton.setPreferredSize(smallButtonSize);
        removeRowButton.setPreferredSize(smallButtonSize);
        addColButton.setPreferredSize(smallButtonSize);
        removeColButton.setPreferredSize(smallButtonSize);

        loadButton.addActionListener(this::loadFromFile);
        saveResultButton.addActionListener(this::saveResultToFile);
        processButton.addActionListener(this::processMatrix);

        clearButton.addActionListener(e -> clearMatrix());

        removeRowButton.addActionListener(e -> removeSelectedRow());
        addRowButton.addActionListener(e -> addRow());
        removeColButton.addActionListener(e -> removeColumn());
        addColButton.addActionListener(e -> addColumn());


        JPanel topControlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        topControlPanel.add(clearButton);
        topControlPanel.add(loadButton);

        JPanel rowControls = new JPanel(new GridLayout(2, 1, 5, 5));
        rowControls.add(addRowButton);
        rowControls.add(removeRowButton);
        rowControls.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel colControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        colControls.add(addColButton);
        colControls.add(removeColButton);

        JPanel colHeaderPanel = new JPanel(new BorderLayout());
        colHeaderPanel.add(colControls, BorderLayout.EAST);

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(accentColor),
                "Входная матрица (для редактирования)",
                TitledBorder.LEFT, TitledBorder.TOP, headerFont, accentColor));

        inputPanel.add(new JScrollPane(inputTable), BorderLayout.CENTER);
        inputPanel.add(rowControls, BorderLayout.WEST);
        inputPanel.add(colHeaderPanel, BorderLayout.NORTH);

        JPanel processPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        processPanel.add(processButton);

        JPanel outputPanel = new JPanel(new BorderLayout(5, 5));
        outputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(successColor),
                "Матрица-результат",
                TitledBorder.LEFT, TitledBorder.TOP, headerFont, successColor));

        JPanel resultRowControls = new JPanel(new GridLayout(2, 1, 5, 5));
        resultRowControls.add(new JButton("+"));
        resultRowControls.add(new JButton("-"));
        for(Component c : resultRowControls.getComponents()) c.setEnabled(false);

        outputPanel.add(new JScrollPane(outputTable), BorderLayout.CENTER);
        outputPanel.add(resultRowControls, BorderLayout.WEST);

        JPanel bottomControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        bottomControlPanel.add(saveResultButton);

        setLayout(new BorderLayout());

        JPanel topSection = new JPanel(new BorderLayout(10, 10));
        topSection.add(topControlPanel, BorderLayout.NORTH);
        topSection.add(inputPanel, BorderLayout.CENTER);
        topSection.setBorder(new EmptyBorder(10, 10, 5, 10)); // Общие отступы сверху

        JPanel bottomSection = new JPanel(new BorderLayout(10, 10));
        bottomSection.add(processPanel, BorderLayout.NORTH);
        bottomSection.add(outputPanel, BorderLayout.CENTER);
        bottomSection.add(bottomControlPanel, BorderLayout.SOUTH);
        bottomSection.setBorder(new EmptyBorder(5, 10, 10, 10)); // Общие отступы снизу

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSection, bottomSection);
        splitPane.setResizeWeight(0.5);

        add(splitPane, BorderLayout.CENTER);

        pack();
        setMinimumSize(new Dimension(800, 650));
        setLocationRelativeTo(null);
        setVisible(true);
    }
    private void makeButtonStylish(JButton button, Color accentColor) {
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);

        button.setFocusPainted(false);

        // Создание составной границы: рамка + внутренний отступ
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor, 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        button.setFont(new Font("Arial", Font.PLAIN, 12));
    }
    private String[][] convertListToMatrix(List<List<String>> list) {
        if (list == null || list.isEmpty()) {
            return new String[0][0];
        }
        int rows = list.size();
        // Используем Stream API для простого преобразования, или обычный цикл
        String[][] matrix = new String[rows][];

        for (int i = 0; i < rows; i++) {
            matrix[i] = list.get(i).toArray(new String[0]);
        }
        return matrix;
    }
    private List<List<String>> convertMatrixToList(String[][] matrix) {
        List<List<String>> list = new ArrayList<>();
        if (matrix == null || matrix.length == 0) {
            return list;
        }
        for (String[] row : matrix) {
            list.add(Arrays.asList(row));
        }
        return list;
    }
    private DefaultTableModel createEmptyTableModel(int rows, int cols) {
        return new DefaultTableModel(rows, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
            @Override
            public String getColumnName(int column) {
                return "";
            }
        };
    }

    private List<List<String>> readMatrixFromJTable(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int rowCount = model.getRowCount();
        int colCount = model.getColumnCount();
        List<List<String>> matrix = new ArrayList<>();

        for (int i = 0; i < rowCount; i++) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < colCount; j++) {
                Object value = model.getValueAt(i, j);
                row.add(value != null ? value.toString() : "");
            }
            matrix.add(row);
        }
        return matrix;
    }

    private void displayMatrixInJTable(JTable table, List<List<String>> matrix) {
        if (matrix == null || matrix.isEmpty()) {
            table.setModel(createEmptyTableModel(0, 0));
            return;
        }

        int rowCount = matrix.size();
        int colCount = matrix.get(0).size();

        DefaultTableModel model = new DefaultTableModel(0, colCount) {
            @Override
            public String getColumnName(int column) {
                return "";
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return table != outputTable;
            }
        };

        for (int i = 0; i < rowCount; i++) {
            Vector<Object> rowData = new Vector<>(matrix.get(i));
            model.addRow(rowData);
        }

        table.setModel(model);
    }
    private void clearMatrix() {
        inputTable.setModel(createEmptyTableModel(0, 5));
        outputTable.setModel(createEmptyTableModel(0, 0));
    }

    private void addRow() {
        DefaultTableModel model = (DefaultTableModel) inputTable.getModel();
        int cols = model.getColumnCount();
        if (cols == 0) {
            inputTable.setModel(createEmptyTableModel(1, 1));
            return;
        }

        Object[] newRow = new Object[cols];
        Arrays.fill(newRow, "");
        model.addRow(newRow);
    }

    private void removeSelectedRow() {
        DefaultTableModel model = (DefaultTableModel) inputTable.getModel();
        int selectedRow = inputTable.getSelectedRow();

        if (model.getRowCount() > 0) {
            if (model.getRowCount() == 1) {
                inputTable.setModel(createEmptyTableModel(0, model.getColumnCount()));
            } else if (selectedRow != -1) {
                model.removeRow(selectedRow);
            } else {
                model.removeRow(model.getRowCount() - 1);
            }
        }
    }

    private void addColumn() {
        DefaultTableModel model = (DefaultTableModel) inputTable.getModel();
        int newColIndex = model.getColumnCount();

        if (model.getRowCount() == 0) {
            inputTable.setModel(createEmptyTableModel(1, 1));
            return;
        }

        model.addColumn("");

        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt("", i, newColIndex);
        }
    }

    private void removeColumn() {
        DefaultTableModel model = (DefaultTableModel) inputTable.getModel();
        int cols = model.getColumnCount();

        if (cols > 1) {
            try {
                List<List<String>> currentMatrix = readMatrixFromJTable(inputTable);
                List<List<String>> newMatrix = new ArrayList<>();

                for (List<String> row : currentMatrix) {
                    List<String> newRow = new ArrayList<>(row.subList(0, cols - 1));
                    newMatrix.add(newRow);
                }

                displayMatrixInJTable(inputTable, newMatrix);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при удалении столбца: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        } else if (cols == 1) {
            inputTable.setModel(createEmptyTableModel(0, 0));
        } else if (cols == 0) {
            JOptionPane.showMessageDialog(this, "Таблица пуста.", "Ошибка", JOptionPane.WARNING_MESSAGE);
        }
    }
    private void loadFromFile(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser("src/tests/input");
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String[][] matrixArray = Solution.readMatrixFromFile(fileChooser.getSelectedFile().getPath());

                if (matrixArray.length == 0) {
                    JOptionPane.showMessageDialog(this, "Ошибка: Файл пуст или содержит только пустые строки.", "Ошибка данных", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!Solution.isRectangular(matrixArray)) {
                    JOptionPane.showMessageDialog(this, "Ошибка: Массив в файле не является прямоугольным.", "Ошибка данных", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                List<List<String>> matrixList = convertMatrixToList(matrixArray);

                displayMatrixInJTable(inputTable, matrixList);

            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка: Файл не найден.", "Ошибка ввода/вывода", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Непредвиденная ошибка чтения файла: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void saveResultToFile(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser("src/tests/output");
        fileChooser.setSelectedFile(new File("output_result.txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                List<List<String>> matrixList = readMatrixFromJTable(outputTable);

                if (matrixList.isEmpty() || (matrixList.size() > 0 && matrixList.get(0).isEmpty())) {
                    JOptionPane.showMessageDialog(this, "Сначала необходимо обработать матрицу.", "Ошибка сохранения", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String[][] matrixArray = convertListToMatrix(matrixList);

                String filePath = fileChooser.getSelectedFile().getPath();
                if (!filePath.toLowerCase().endsWith(".txt")) {
                    filePath += ".txt";
                }

                Solution.writeMatrixToFile(filePath, matrixArray);
                JOptionPane.showMessageDialog(this, "Результат успешно сохранен.", "Успех", JOptionPane.INFORMATION_MESSAGE);

            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка ввода/вывода при сохранении: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Непредвиденная ошибка при сохранении: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void processMatrix(ActionEvent e) {
        try {
            List<List<String>> initialList = readMatrixFromJTable(inputTable);

            if (initialList.isEmpty() || initialList.get(0).isEmpty()) {
                JOptionPane.showMessageDialog(this, "Входная таблица не должна быть пустой.", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String[][] initialMatrix = convertListToMatrix(initialList);

            String[][] resultArray = Solution.processMatrix(initialMatrix);

            List<List<String>> resultList = convertMatrixToList(resultArray);

            displayMatrixInJTable(outputTable, resultList);

            JOptionPane.showMessageDialog(this, "Матрица успешно обработана.", "Успех", JOptionPane.INFORMATION_MESSAGE);

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка данных: " + ex.getMessage(), "Ошибка данных", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Непредвиденная ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Main для запуска ---

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new GUIApp();
        });
    }
}
