package root.type;

import root.Predshest;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    private String data;
    private String outForTypeSyn = String.valueOf("TypeSynOut.txt");
    private String outForTypeSize = String.valueOf("TypeSizeOut.txt");
    private static BufferedWriter writerTypeSyn;
    private static BufferedWriter writerTypeSize;

    private States currentState = States.N;

    //Таблица предшествования
    private List<Predshest> predshestTable = PredshestTable.predshest_table;

    public static final List<String> KEY_WORDS_LIST = Arrays.asList("type", "var", "union", "end");
    public static final List<String> SCALAR_TYPES = Arrays.asList("byte", "extended");

    //Таблица лексем
    List<Token> tokenTable = new ArrayList<>();


    public static void main(String[] args) throws IOException {
        new Main("TypeInput_1.txt");
    }

    public Main(String file) throws IOException {
        writerTypeSyn = new BufferedWriter(new FileWriter(outForTypeSyn));
        writerTypeSize = new BufferedWriter(new FileWriter(outForTypeSize));
        this.data = readFileAsString(file);//Начало программы

        new LexicalAnalyzer(currentState, data, tokenTable)
                .analyze();
        new SyntacticalAnalyzer(tokenTable, predshestTable)
                .analyze();
        new TypeAnalyzer(data, tokenTable)
                .analyze();
        closeStreams();

    }

    public static void closeStreams() {
        try {
            writerTypeSyn.close();
            writerTypeSize.close();
        } catch (IOException e) {

        }
    }

    /**
     * Прочитать файл в строку
     */
    private String readFileAsString(String filePath) {
        StringBuilder fileData = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(
                    new FileReader(filePath));
            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
            }
            reader.close();
        } catch (Throwable t) {
        }
        return fileData.toString();
    }

    public static void writeForTypeSyn(String line) {
        try {
            writerTypeSyn.write(line + System.getProperty("line.separator"));
        } catch (IOException e) {
        }
    }

    public static void writeForTypeSize(String line) {
        try {
            writerTypeSize.write(line + System.getProperty("line.separator"));
        } catch (IOException e) {
        }
    }

}

