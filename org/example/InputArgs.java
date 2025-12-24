package org.example;

public class InputArgs {
    public String inputFile;
    public String outputFile;

    public InputArgs() {
    }
    public boolean isValid() {
        return inputFile != null && outputFile != null;
    }
}
