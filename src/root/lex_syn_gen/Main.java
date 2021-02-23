package root.lex_syn_gen;

import root.*;
import root.lex_syn_gen.gen.ObjectCodeGenerator;
import root.lex_syn_gen.lex.LexicalAnalyzer;
import root.lex_syn_gen.syn.SyntacticalAnalyzer;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.*;
import java.util.*;

public class Main {
    private String data;
    private String outputFileForLexAndSyn = String.valueOf("Lex&SynOut.txt");
    private String outputFileForGenerate = String.valueOf("GenOut.txt");
    private static BufferedWriter fileWriterLS;
    private static BufferedWriter fileWriterG;
    private Node rootNode;
    private States currentState = States.N;
    //Таблица предшествования
    private List<Predshest> predshestTable = PredshestTable.predshest_table;

    public static final List<String> KEY_WORDS_LIST = Arrays.asList("or", "xor", "and", "not");
    public static final List<String> BRACE_LIST = Arrays.asList("(", ")");
    public static final String HEX_PATTERN = "0[x][0-9A-F]+";
    public static final String HEX_ZERO = "0x00";
    public static final String HEX_NOT_ZERO = "0x01";
    public static final List<String> OPERATORS_LIST = Arrays.asList("or", "xor", "and", "not", ":=");

    //Таблица лексем
    List<Token> tokenTable = new ArrayList<>();
    //Дерево
    LinkedList<Node> nodes = new LinkedList<>();
    //Триады
    LinkedList<Triad> triads = new LinkedList<>();

    public static void main(String[] args) throws IOException {
        new Main("LexSynGenInput_1.txt");
//        new Main("LexSynGenInput_2.txt");
//        new Main("LexSynGenInput_3.txt");
    }

    public Main(String file) throws IOException {
        fileWriterLS = new BufferedWriter(new FileWriter(outputFileForLexAndSyn));
        fileWriterG = new BufferedWriter(new FileWriter(outputFileForGenerate));
        this.data = readFileAsString(file);//Начало программы

        new LexicalAnalyzer(currentState, data, tokenTable)
                .analyze();
        new SyntacticalAnalyzer(tokenTable, predshestTable, nodes, rootNode, this)
                .analyze();
        new ObjectCodeGenerator(rootNode, triads)
                .generate();

//        traceRootNode();

        closeStreams();


    }

    public static void closeStreams() {
        try {
            fileWriterLS.close();
            fileWriterG.close();
        } catch (IOException e) {

        }
    }

    /**
     * UI
     **/

    void traceRootNode() {
        DefaultMutableTreeNode model =
                new DefaultMutableTreeNode();
        JTree tree = new JTree(model);
        JFrame frame = new JFrame();
        frame.add(tree);
        frame.setBounds(300, 100, 400, 600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.show();

        DefaultMutableTreeNode node = new DefaultMutableTreeNode("");
        model.add(node);
        buildTreeNode(node, rootNode);

        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    void buildTreeNode(DefaultMutableTreeNode root, Node node) {
        node.getChildren().forEach(someNode -> {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(someNode.getText());
            root.add(child);
            buildTreeNode(child, someNode);
        });
    }

    public void setRootNode(Node rootNode) {
        this.rootNode = rootNode;
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

    public static void writeForLexSyn(String line) {
        try {
            fileWriterLS.write(line + System.getProperty("line.separator"));
        } catch (IOException e) {
        }
    }

    public static void writeForGen(String line) {
        try {
            fileWriterG.write(line + System.getProperty("line.separator"));
        } catch (IOException e) {
        }
    }

}

