package root.lex_syn_gen.syn;

import root.lex_syn_gen.Main;
import root.lex_syn_gen.Node;
import root.Predshest;
import root.lex_syn_gen.Token;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class SyntacticalAnalyzer {

    private List<Token> tokenTable;
    private List<Predshest> predshestTable;

    private LinkedList<Token> inputQueue;
    private LinkedList<Token> memoryStack;

    private LinkedList<Node> nodes;
    private Node rootNode;
    private Main main;

    public SyntacticalAnalyzer(List<Token> tokenTable, List<Predshest> predshestTable,
                               LinkedList<Node> nodes, Node rootNode, Main main) {
        this.tokenTable = tokenTable;
        this.predshestTable = predshestTable;
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
            Main.writeForLexSyn("");

            String text = "Лента - " + listToStr(inputQueue) + "   Память - " + listToStr(memoryStack);
            Main.writeForLexSyn(text);

            Token nextToken = inputQueue.peek();

            // если память пуста берём первый из ленты
            if (memoryStack.isEmpty()) {
                Main.writeForLexSyn("Действие - Перенос");
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
                    Main.writeForLexSyn("Действие - Перенос");
                    memoryStack.add(inputQueue.poll());
                } else {
                    Main.writeForLexSyn("Действие - Свёртка " + wrap());
                }
            }

            empty = inputQueue.isEmpty() && (memoryStack.size() == 1);
            // продолжать пока в ленте не пусто, а в памяти не останется 1 элемент
        } while (!empty);

        Main.writeForLexSyn("");
        String text = "Лента - " + listToStr(inputQueue) + "   Память - " + listToStr(memoryStack);
        Main.writeForLexSyn(text);

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
            memoryStack.set(memoryStack.size() - 1, new Token(Token.Type.E, ""));

            Node nodeA = new Node(last1.getStr());
            Node nodeE = new Node("E");
            nodeE.add(nodeA);
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
            nodeE.add(child1);
            nodeE.add(nodeOr);
            nodeE.add(child2);
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
                nodeE.add(new Node("not"));
                nodeE.add(new Node("("));
                nodeE.add(nodes.removeLast());
                nodeE.add(new Node(")"));
                nodes.add(nodeE);

                return 8;

            } else { //Правило 7
                memoryStack.remove(memoryStack.size() - 3);
                memoryStack.removeLast();

                Node nodeE = new Node("E");
                nodeE.add(new Node("("));
                nodeE.add(nodes.removeLast());
                nodeE.add(new Node(")"));
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
            nodeE.add(child1);
            nodeE.add(nodeAnd);
            nodeE.add(child2);
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
            nodeE.add(child1);
            nodeE.add(nodeXor);
            nodeE.add(child2);
            nodes.add(nodeE);

            return 3;
        }

        //Правило 1
        if (last1.getKey().equals(";") && (last2.getKey().equals("E") && (last3.getKey().equals(":=")) && (last4.getKey().equals("a")))) {
            memoryStack.remove(memoryStack.size() - 3);
            memoryStack.remove(memoryStack.size() - 3);
            memoryStack.removeLast();

            Node nodeE = new Node("E");

            nodeE.add(new Node(last4.getStr()));
            nodeE.add(new Node(":="));
            nodeE.add(nodes.removeLast());
            nodeE.add(new Node(";"));

            rootNode = nodeE;
            main.setRootNode(rootNode);

            return 1;
        } else {
            Main.writeForLexSyn("----ОШИБКА!!!---- Нет правила! - ");
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

        Optional<Predshest> predshestOptional = predshestTable.stream().filter(
                pred -> (pred.getLeft().equals(token1Str) && (pred.getRigth().equals(token2Str)))).findFirst();

        if (predshestOptional.isPresent()) {
            predshestResult = predshestOptional.get().getResult();
        } else {
            Main.writeForLexSyn("ERROR! " + token1Str + " " + token2Str);
        }

        Main.writeForLexSyn("Compare... " + token1Str + " " + predshestResult + " " + token2Str);
        return predshestResult;
    }

}
