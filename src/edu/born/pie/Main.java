package edu.born.pie;

import edu.born.pie.syn.SyntacticalAnalyzer;
import edu.born.pie.gen.ObjectCodeGenerator;
import edu.born.pie.lex.LexicalAnalyzer;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.*;
import java.util.*;

import static edu.born.pie.PrintHelper.closeStream;

public class Main {
    private String data;
    private String outputFileForGenerate = String.valueOf("GenOut.txt");
    private static BufferedWriter fileWriterG;
    private Node rootNode;
    //Таблица предшествования
    private List<Precedence> precedenceTable = PrecedenceTable.PRECEDENCE_TABLE;

    public static final List<String> KEY_WORDS_LIST = Arrays.asList("or", "xor", "and", "not");
    public static final List<String> BRACE_LIST = Arrays.asList("(", ")");
    public static final String HEX_PATTERN = "0[x][0-9A-F]+";
    public static final String HEX_ZERO = "0x00";
    public static final String HEX_NOT_ZERO = "0x01";
    public static final List<String> OPERATORS_LIST = Arrays.asList("or", "xor", "and", "not", ":=");

    //Дерево
    LinkedList<Node> nodes = new LinkedList<>();
    //Триады
    LinkedList<Triad> triads = new LinkedList<>();

    public static void main(String[] args) throws IOException {
      //  new Main("LexSynGenInput_1.txt");
        new Main("LexSynGenInput_2.txt");
//        new Main("LexSynGenInput_3.txt");
    }

    public Main(String file) throws IOException {
        fileWriterG = new BufferedWriter(new FileWriter(outputFileForGenerate));
        this.data = readFileAsString(file);//Начало программы

        var tokenTable = new LexicalAnalyzer(data).analyze();
        new SyntacticalAnalyzer(tokenTable, precedenceTable, nodes, rootNode, this)
                .analyze();
        new ObjectCodeGenerator(rootNode, triads)
                .generate();

      //  traceRootNode();

        closeStreams();
        closeStream();


    }

    public static void closeStreams() {
        try {
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

    public static void writeForGen(String line) {
        try {
            fileWriterG.write(line + System.getProperty("line.separator"));
        } catch (IOException e) {
        }
    }

}

