package edu.born.pie;

import edu.born.pie.generator.ObjectCodeGenerator;
import edu.born.pie.lexical.LexicalAnalyzer;
import edu.born.pie.syntactical.SyntacticalAnalyzer;

import java.util.Arrays;
import java.util.List;

import static edu.born.pie.utils.FileUtil.readFileAsString;
import static edu.born.pie.utils.PrintUtil.closeStream;
import static edu.born.pie.utils.UIUtil.traceRootNode;

public class Main {

    public static final List<String> KEY_WORDS_LIST = Arrays.asList("or", "xor", "and", "not");
    public static final List<String> BRACE_LIST = Arrays.asList("(", ")");
    public static final String HEX_PATTERN = "0[x][0-9A-F]+";
    public static final String HEX_ZERO = "0x00";
    public static final String HEX_NOT_ZERO = "0x01";
    public static final List<String> OPERATORS_LIST = Arrays.asList("or", "xor", "and", "not", ":=");

    public static void main(String[] args) {
        //new Main("Input_1.txt");
        new Main("Input_2.txt", false);
        //new Main("Input_3.txt");
    }

    public Main(String file, boolean showTree) {
        var data = readFileAsString(file);//Начало программы
        var tokenTable = new LexicalAnalyzer(data).analyze();
        var rootNode = new SyntacticalAnalyzer(tokenTable).analyze();
        new ObjectCodeGenerator(rootNode).generate();

        if (showTree) traceRootNode(rootNode);
        closeStream();
    }
}

