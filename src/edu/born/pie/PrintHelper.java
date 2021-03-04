package edu.born.pie;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class PrintHelper {

    private static final String OUT_FILE = "Lex&SynOut.txt";
    private static BufferedWriter OUT_WRITER;

    public static final String INPUT_TITLE = "---- Input ---- \n";

    static {
        try {
            OUT_WRITER = new BufferedWriter(new FileWriter(OUT_FILE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void ln() {
        print("");
    }

    public static void print(String line) {
        try {
            OUT_WRITER.write(line + System.getProperty("line.separator"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
