package edu.born.pie;

import edu.born.pie.generator.ObjectCodeGenerator;
import edu.born.pie.lexical.LexicalAnalyzer;
import edu.born.pie.syntactical.SyntacticalAnalyzer;

import java.util.Arrays;
import java.util.List;

import static edu.born.pie.utils.FileUtil.readFileAsString;
import static edu.born.pie.utils.PrintUtil.closeStream;

public class Main {

    public static final List<String> KEY_WORDS_LIST = Arrays.asList("or", "xor", "and", "not");
    public static final List<String> BRACE_LIST = Arrays.asList("(", ")");
    public static final String HEX_PATTERN = "0[x][0-9A-F]+";
    public static final String HEX_ZERO = "0x00";
    public static final String HEX_NOT_ZERO = "0x01";
    public static final List<String> OPERATORS_LIST = Arrays.asList("or", "xor", "and", "not", ":=");

    public static void main(String[] args) {
      //  new Main("LexSynGenInput_1.txt");
        new Main("LexSynGenInput_2.txt");
//        new Main("LexSynGenInput_3.txt");
    }

    public Main(String file) {
        var data = readFileAsString(file);//Начало программы
        var tokenTable = new LexicalAnalyzer(data).analyze();
        var rootNode = new SyntacticalAnalyzer(tokenTable).analyze();
        new ObjectCodeGenerator(rootNode).generate();

        //traceRootNode(rootNode);
        closeStream();
    }
}

