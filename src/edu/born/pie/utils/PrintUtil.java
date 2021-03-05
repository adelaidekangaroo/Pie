package edu.born.pie.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class PrintUtil {

    private static final String OUT_FILE = "Out.txt";
    private static BufferedWriter OUT_WRITER;
    private static final String lineSeparator = System.getProperty("line.separator");

    public static final String INPUT_TITLE = "---- Input ----";
    public static final String DEBUGGING_TITLE = "---- Debugging ----";
    public static final String ERROR_TITLE = "---- ERROR ----";
    public static final String TOKEN_TABLE_TITLE = "---- Token table ----";
    public static final String TRIADS_TITLE = "---- Triads ----";
    public static final String CODE_TITLE = "---- Code ----";
    public static final String CONVOLUTION_TRIADS_TITLE = "---- Convolution triads ----";
    public static final String OPTIMIZED_CODE_TITLE = "---- Optimized code ----";

    static {
        try {
            OUT_WRITER = new BufferedWriter(new FileWriter(OUT_FILE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // So that line breaks take not 2 characters, but 1
    public static String splitLines(String data) {
        return data.replace(lineSeparator, "\n");
    }

    public static void ln() {
        print("");
    }

    public static <T> void print(Iterable<T> collection) {
        collection.forEach(item -> print(item.toString()));
    }

    public static void print(String line) {
        try {
            OUT_WRITER.write(line + System.getProperty("line.separator"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeStream() {
        try {
            OUT_WRITER.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
