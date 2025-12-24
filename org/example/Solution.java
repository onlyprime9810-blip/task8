package org.example;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Solution {
    public static boolean areRowsEqual(String[] row1, String[] row2) {
        if (row1 == null || row2 == null) {
            return row1 == row2;
        }
        if (row1.length != row2.length) {
            return false;
        }
        for (int i = 0; i < row1.length; i++) {
            if (!row1[i].equals(row2[i])) {
                return false;
            }
        }
        return true;
    }

    public static boolean isRectangular(String[][] matrix) {
        if (matrix == null || matrix.length == 0) {
            return true;
        }
        int expectedLength = matrix[0].length;

        if (expectedLength == 0 && matrix.length > 0) return false;

        for (String[] row : matrix) {
            if (row.length != expectedLength) {
                return false;
            }
        }
        return true;
    }

    public static String[][] processMatrix(String[][] matrix) {
        if (matrix == null || matrix.length == 0) {
            return new String[0][];
        }
        if (!isRectangular(matrix)) {
            throw new IllegalArgumentException("Ошибка: Входная матрица не является прямоугольной.");
        }
        List<String[]> tempResultList = new ArrayList<>();

        int i = 0;
        int N = matrix.length;

        while (i < N) {
            String[] currentRow = matrix[i];
            boolean isStartOfSequence = (i + 1 < N) && areRowsEqual(currentRow, matrix[i + 1]);

            if (isStartOfSequence) {
                tempResultList.add(currentRow);

                int j = i + 1;
                while (j < N && areRowsEqual(currentRow, matrix[j])) {
                    j++;
                }
                i = j;
            } else {
                tempResultList.add(currentRow);
                i++;
            }
        }
        return tempResultList.toArray(new String[0][]);
    }

    public static String[][] readMatrixFromFile(String fileName) throws FileNotFoundException {
        List<String[]> tempMatrixList = new ArrayList<>();
        File file = new File(fileName);

        try (Scanner scanner = new Scanner(file, "UTF-8")) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String trimmedLine = line.trim();

                if (trimmedLine.isEmpty()) {
                    continue;
                }
                String[] parts = trimmedLine.split("\\s+");

                if (parts.length > 0) {
                    tempMatrixList.add(parts);
                }
            }
        }
        if (tempMatrixList.isEmpty()) {
            return new String[0][0];
        }
        return tempMatrixList.toArray(new String[0][]);
    }

    public static void writeMatrixToFile(String fileName, String[][] matrix) throws FileNotFoundException {
        try (PrintWriter writer = new PrintWriter(fileName)) {
            for (String[] row : matrix) {
                writer.println(String.join(" ", row));
            }
        }
    }
}