package edu.born.pie.syn;

import edu.born.pie.Node;
import edu.born.pie.Precedence;
import edu.born.pie.Token;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static edu.born.pie.PrecedenceTable.PRECEDENCE_TABLE;
import static edu.born.pie.PrintHelper.*;
import static edu.born.pie.Token.Type;
import static edu.born.pie.Token.of;

public class SyntacticalAnalyzer {

    private final List<Token> tokenTable;
    private final LinkedList<Token> inputQueue = new LinkedList<>();
    private final LinkedList<Token> memoryStack = new LinkedList<>();
    private final LinkedList<Node> nodes = new LinkedList<>();
    private Node rootNode;

    public SyntacticalAnalyzer(List<Token> tokenTable) {
        this.tokenTable = tokenTable;
    }

    public Node analyze() {
        inputQueue.addAll(tokenTable);

        boolean queueIsEmpty;
        do {
            print("");
            print(String.format("Stack - %s Memory - %s", listToStr(inputQueue), listToStr(memoryStack)));

            Token nextToken = inputQueue.peek();

            // if memory is empty get first by stack
            if (memoryStack.isEmpty()) {
                print("Action - Transfer");
                memoryStack.add(inputQueue.poll());
            } else {
                // if memory is not empty get last by memory
                Token memToken = memoryStack.getLast();
                if (memToken.getKey().equals("E")) {
                    // if it is E and there are more elements in memory, then we take the penultimate
                    if (memoryStack.size() >= 2) {
                        memToken = memoryStack.get(memoryStack.size() - 2);
                    }
                }
                // compare
                String compare;
                // if there are elements on the stack, then compare against the precedence table
                if (nextToken != null) {
                    compare = compareTokens(memToken, nextToken);
                } else {
                    //if stack is empty, then ">" (convolution)
                    compare = ">";
                }

                if (compare.equals("<") || compare.equals("=")) {
                    print("Action - Transfer");
                    memoryStack.add(inputQueue.poll());
                } else {
                    print("Action - Convolution " + convolution());
                }
            }

            queueIsEmpty = inputQueue.isEmpty() && (memoryStack.size() == 1);
            // continue until the stack is empty and there is no 1 element left in memory
        } while (!queueIsEmpty);

        print("");
        print(String.format("Stack - %s Memory - %s", listToStr(inputQueue), listToStr(memoryStack)));

        return rootNode;
    }

    private String listToStr(List<Token> list) {
        StringBuffer s = new StringBuffer("[");

        list.forEach(token -> {
            if (token != null) {
                s.append(token.toString()).append(" ");
            } else {
                s.append("null");
            }
        });

        return s.toString().trim() + "]";
    }

    private int convolution() {
        Token last1 = memoryStack.get(memoryStack.size() - 1);

        //Rule 9
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

        //Rule 2
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

        //Rules 7 и 8
        if (last1.getKey().equals(")") && last2.getKey().equals("E") && last3.getKey().equals("(")) {
            //Rule 8
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

            } else { //Rule 7
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

        //Rule 5
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
        //Rule 3
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

        //Rule 1
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

            return 1;
        } else {
            print(String.format("%s No rule!", ERROR_TITLE));
            closeStream();
            System.exit(0);
        }

        return 0;
    }

    private String compareTokens(Token token1, Token token2) {
        String token1Str;
        String token2Str;
        String precedenceResult = "";
        token1Str = token1.getKey();
        token2Str = token2.getKey();

        Optional<Precedence> precedenceOptional = PRECEDENCE_TABLE.stream()
                .filter(precedence -> (precedence.getLeft().equals(token1Str)
                        && (precedence.getRight().equals(token2Str))))
                .findFirst();

        if (precedenceOptional.isPresent()) {
            precedenceResult = precedenceOptional.get().getResult();
        } else {
            print(String.format("%s %s %s", ERROR_TITLE, token1Str, token2Str));
        }

        print(String.format("Compare... %s %s %s", token1Str, precedenceResult, token2Str));

        return precedenceResult;
    }
}