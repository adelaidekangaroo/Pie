package edu.born.pie.syn;

import edu.born.pie.Main;
import edu.born.pie.Node;
import edu.born.pie.Precedence;
import edu.born.pie.Token;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static edu.born.pie.PrintHelper.print;
import static edu.born.pie.Token.*;

public class SyntacticalAnalyzer {

    private List<Token> tokenTable;
    private List<Precedence> precedenceTable;

    private LinkedList<Token> inputQueue;
    private LinkedList<Token> memoryStack;

    private LinkedList<Node> nodes;
    private Node rootNode;
    private Main main;

    public SyntacticalAnalyzer(List<Token> tokenTable, List<Precedence> precedenceTable,
                               LinkedList<Node> nodes, Node rootNode, Main main) {
        this.tokenTable = tokenTable;
        this.precedenceTable = precedenceTable;
        this.nodes = nodes;
        this.rootNode = rootNode;
        this.main = main;
    }

    public void analyze() {
        inputQueue = new LinkedList<>();
        memoryStack = new LinkedList<>();

        inputQueue.addAll(tokenTable);

        boolean empty;
        do {
            print("");

            String text = "Line - " + listToStr(inputQueue) + "   Memory - " + listToStr(memoryStack);
            print(text);

            Token nextToken = inputQueue.peek();

            // если память пуста берём первый из ленты
            if (memoryStack.isEmpty()) {
                print("Action - Transfer");
                memoryStack.add(inputQueue.poll());
            } else {
                // если память не пуста берём последний из памяти
                Token memToken = memoryStack.getLast();
                if (memToken.getKey().equals("E")) {
                    // если он Е и в памяти есть ещё элементы - то берём предпоследний
                    if (memoryStack.size() >= 2) {
                        memToken = memoryStack.get(memoryStack.size() - 2);
                    }
                }
                // сравнение
                String compare;
                // если в ленте есть элементы, то сравнить по таблице предшествования
                if (nextToken != null) {
                    compare = compareTokens(memToken, nextToken);
                } else {
                    //если в ленте пусто, то ">" (свёртка)
                    compare = ">";
                }

                if (compare.equals("<") || compare.equals("=")) {
                    print("Action - Transfer");
                    memoryStack.add(inputQueue.poll());
                } else {
                    print("Action - Convolution " + wrap());
                }
            }

            empty = inputQueue.isEmpty() && (memoryStack.size() == 1);
            // продолжать пока в ленте не пусто, а в памяти не останется 1 элемент
        } while (!empty);

        print("");
        String text = "Line - " + listToStr(inputQueue) + "   Memory - " + listToStr(memoryStack);
        print(text);

    }

    String listToStr(List<Token> list) {
        StringBuffer s = new StringBuffer("[");
        list.forEach(object -> {
            if (object != null) {
                s.append(object.toString() + " ");
            } else {
                s.append("null");
            }
        });

        return s.toString().trim() + "]";
    }

    /**
     * Свёртка
     */

    int wrap() {
        Token last1 = memoryStack.get(memoryStack.size() - 1);

        //Правило 9
        if (last1.getKey().equals("a")) {
            memoryStack.set(memoryStack.size() - 1, of(Type.E, ""));

            Node nodeA = new Node(last1.getLabel());
            Node nodeE = new Node("E");
            nodeE.addChildNode(nodeA);
            nodes.add(nodeE);

            return 9;
        }

        Token last2 = memoryStack.get(memoryStack.size() - 2);
        Token last3 = memoryStack.get(memoryStack.size() - 3);

        //Правило 2
        if (last1.getKey().equals("E") && last2.getKey().equals("or") && last3.getKey().equals("E")) {
            memoryStack.removeLast();
            memoryStack.removeLast();

            Node nodeE = new Node("E");
            Node nodeOr = new Node("or");
            Node child2 = nodes.removeLast();
            Node child1 = nodes.removeLast();
            nodeE.addChildNode(child1);
            nodeE.addChildNode(nodeOr);
            nodeE.addChildNode(child2);
            nodes.add(nodeE);

            return 2;
        }

        Token last4 = memoryStack.get(memoryStack.size() - 4);

        // 7 и 8
        if (last1.getKey().equals(")") && last2.getKey().equals("E") && last3.getKey().equals("(")) {
            //Правило 8
            if (last4.getKey().equals("not")) {
                memoryStack.removeLast();
                memoryStack.remove(memoryStack.size() - 2);
                memoryStack.remove(memoryStack.size() - 2);

                Node nodeE = new Node("E");
                nodeE.addChildNode(new Node("not"));
                nodeE.addChildNode(new Node("("));
                nodeE.addChildNode(nodes.removeLast());
                nodeE.addChildNode(new Node(")"));
                nodes.add(nodeE);

                return 8;

            } else { //Правило 7
                memoryStack.remove(memoryStack.size() - 3);
                memoryStack.removeLast();

                Node nodeE = new Node("E");
                nodeE.addChildNode(new Node("("));
                nodeE.addChildNode(nodes.removeLast());
                nodeE.addChildNode(new Node(")"));
                nodes.add(nodeE);

                return 7;
            }
        }

        //Правило 5
        if (last1.getKey().equals("E") && last2.getKey().equals("and") && last3.getKey().equals("E")) {
            memoryStack.removeLast();
            memoryStack.removeLast();

            Node nodeE = new Node("E");
            Node nodeAnd = new Node("and");
            Node child2 = nodes.removeLast();
            Node child1 = nodes.removeLast();
            nodeE.addChildNode(child1);
            nodeE.addChildNode(nodeAnd);
            nodeE.addChildNode(child2);
            nodes.add(nodeE);

            return 5;
        }
        //Правило 3
        if (last1.getKey().equals("E") && last2.getKey().equals("xor") && last3.getKey().equals("E")) {
            memoryStack.removeLast();
            memoryStack.removeLast();

            Node nodeE = new Node("E");
            Node nodeXor = new Node("xor");
            Node child2 = nodes.removeLast();
            Node child1 = nodes.removeLast();
            nodeE.addChildNode(child1);
            nodeE.addChildNode(nodeXor);
            nodeE.addChildNode(child2);
            nodes.add(nodeE);

            return 3;
        }

        //Правило 1
        if (last1.getKey().equals(";") && (last2.getKey().equals("E") && (last3.getKey().equals(":=")) && (last4.getKey().equals("a")))) {
            memoryStack.remove(memoryStack.size() - 3);
            memoryStack.remove(memoryStack.size() - 3);
            memoryStack.removeLast();

            Node nodeE = new Node("E");

            nodeE.addChildNode(new Node(last4.getLabel()));
            nodeE.addChildNode(new Node(":="));
            nodeE.addChildNode(nodes.removeLast());
            nodeE.addChildNode(new Node(";"));

            rootNode = nodeE;
            main.setRootNode(rootNode);

            return 1;
        } else {
            print("----ОШИБКА!!!---- Нет правила! - ");
            Main.closeStreams();
            System.exit(0);
        }

        return 0;
    }

    String compareTokens(Token token1, Token token2) {
        String token1Str;
        String token2Str;
        String predshestResult = String.valueOf("");
        token1Str = token1.getKey();
        token2Str = token2.getKey();

        Optional<Precedence> predshestOptional = precedenceTable.stream().filter(
                pred -> (pred.getLeft().equals(token1Str) && (pred.getRight().equals(token2Str)))).findFirst();

        if (predshestOptional.isPresent()) {
            predshestResult = predshestOptional.get().getResult();
        } else {
            print("ERROR! " + token1Str + " " + token2Str);
        }

        print("Compare... " + token1Str + " " + predshestResult + " " + token2Str);
        return predshestResult;
    }

}
