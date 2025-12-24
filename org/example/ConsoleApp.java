package org.example;

import java.io.FileNotFoundException;

public class ConsoleApp {
    public static InputArgs parseCmdArgs(String[] args) {
        InputArgs inputArgs = new InputArgs();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.equals("-i") || arg.equals("--input-file")) {
                if (i + 1 < args.length) {
                    inputArgs.inputFile = args[i + 1];
                    i++;
                }
            } else if (arg.equals("-o") || arg.equals("--output-file")) {
                if (i + 1 < args.length) {
                    inputArgs.outputFile = args[i + 1];
                    i++;
                }
            }
        }

        if (!inputArgs.isValid() && args.length == 2) {
            inputArgs.inputFile = args[0];
            inputArgs.outputFile = args[1];
        }

        return inputArgs;
    }

    private static void printUsageAndExit() {
        System.err.println("Ошибка: Не указаны обязательные параметры входного и/или выходного файла.");
        System.err.println("Использование:");
        System.err.println("  java org.example.ConsoleApp.ConsoleApp <входной_файл> <выходной_файл>");
        System.err.println("  java org.example.ConsoleApp.ConsoleApp -i <входной_файл> -o <выходной_файл>");
        System.exit(1);
    }

    public static void main(String[] args) {
        InputArgs inputArgs = parseCmdArgs(args);

        if (!inputArgs.isValid()) {
            printUsageAndExit();
        }

        String inputFileName = inputArgs.inputFile;
        String outputFileName = inputArgs.outputFile;

        try {
            System.out.println("Чтение данных из: " + inputFileName);

            String[][] inputMatrix = Solution.readMatrixFromFile(inputFileName);

            String[][] resultMatrix = Solution.processMatrix(inputMatrix);

            Solution.writeMatrixToFile(outputFileName, resultMatrix);
            System.out.println("Результат записан в: " + outputFileName);
            System.out.println("Программа успешно завершена.");

        } catch (FileNotFoundException e) {
            System.err.println("Ошибка: Файл не найден или не может быть создан по пути: " + e.getMessage());
            System.exit(2);
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка данных: " + e.getMessage());
            System.exit(3);
        } catch (Exception e) {
            System.err.println("Непредвиденная ошибка: " + e.getMessage());
            e.printStackTrace();
            System.exit(4);
        }
    }
}