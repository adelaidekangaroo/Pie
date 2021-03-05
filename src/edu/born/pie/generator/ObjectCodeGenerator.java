package edu.born.pie.generator;

import edu.born.pie.model.Node;
import edu.born.pie.model.Triad;
import edu.born.pie.utils.ObjectCodeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.regex.Pattern;

import static edu.born.pie.Pie.*;
import static edu.born.pie.model.Triad.CountOperands.ONE;
import static edu.born.pie.model.Triad.CountOperands.TWO;
import static edu.born.pie.model.Triad.of;
import static edu.born.pie.utils.ObjectCodeUtil.*;
import static edu.born.pie.utils.PrintUtil.*;

public class ObjectCodeGenerator {

    private final Node rootNode;
    private final List<Triad> triads = new ArrayList<>();
    private int triadCounter = 0;

    public ObjectCodeGenerator(Node rootNode) {
        this.rootNode = rootNode;
    }

    public void generate() {
        // Generation

        ln();
        print(TRIADS_TITLE);

        walkToTree(rootNode);
        print(triads);

        ln();
        print(CODE_TITLE);

        var lastTriad = triads.get(triads.size() - 1);
        var code = generateAsm(lastTriad);
        print(code);

        // Optimization

        print(CONVOLUTION_TRIADS_TITLE);

        print("Step 1:");
        convolutionTriads(triads);
        print(triads);

        print("Step 2:");
        removeCollapsedTriads(triads);
        print(triads);

        lastTriad = triads.get(triads.size() - 1);
        code = generateAsm(lastTriad);
        code = minimizePushPop(code);

        ln();
        print(OPTIMIZED_CODE_TITLE);

        print(code);
    }

    // Recursive tree traversal and triad generation

    private String walkToTree(Node node) {

        List<Node> children;

        // if the vertex has no leaves, then return the vertex itself
        if (node.getChildren().size() == 0) return node.getText();
        // if a vertex among the leaves does not have an operator,
        // then for each E - leaf, call a deeper traversal
        children = node.getChildren();
        if (children.stream().noneMatch(ObjectCodeUtil::isOperand)) {
            for (Node child : children) {
                if (!isBrace(child)) return walkToTree(child);
            }
        } else {
            // if there is an operator, then call a traversal for two of its operands
            Optional<Node> firstOperand = Optional.empty();
            Optional<Node> secondOperand = Optional.empty();
            String operator = "";

            for (int i = 0; i < children.size() - 1; i++) {
                if (isOperand(children.get(i))) {
                    operator = children.get(i).getText();
                    firstOperand = findE(children.subList(0, i));
                    secondOperand = findE(children.subList(i + 1, children.size()));
                }
            }

            Triad triad;

            // if there is no operand to the left of the operator
            if (firstOperand.isEmpty()) {
                triad = of(operator, walkToTree(secondOperand.get()), "", ONE);
            } else {
                // operator with two operands
                triad = of(operator, walkToTree(firstOperand.get()), walkToTree(secondOperand.get()), TWO);
            }

            ++triadCounter;
            triad.setIndex(triadCounter);
            triads.add(triad);

            // return triad index
            return "^" + triadCounter;
        }
        return "";
    }

    // Recursive Triad Traversal and Assembly Code Generation

    private String generateAsm(Triad triad) {
        var builder = new StringBuilder();

        String operand1;
        String operand2;

        // if the operand is a reference to a triad, then first execute for that triad
        switch (triad.getOperator()) {
            case ":=" -> {
                operand1 = triad.getOperand1();
                operand2 = triad.getOperand2();
                if (operand2.startsWith("^")) {
                    builder.append(generateAsm(findTriadByIndex(triads, parseIndex(operand2))))
                            .append("POP AX")
                            .append("\n");
                }
                if (!operand2.startsWith("^")) {
                    builder.append("MOV AX, ")
                            .append(operand2)
                            .append("\n");
                }
                builder.append("MOV ")
                        .append(operand1)
                        .append(", AX");
            }
            case "or", "and", "xor" -> {
                operand1 = triad.getOperand1();
                operand2 = triad.getOperand2();
                if (operand1.startsWith("^")) {
                    builder.append(generateAsm(findTriadByIndex(triads, parseIndex(operand1))))
                            .append("POP AX")
                            .append("\n");
                }
                if (operand2.startsWith("^")) {
                    builder.append(generateAsm(findTriadByIndex(triads, parseIndex(operand2))))
                            .append("POP BX")
                            .append("\n");
                }
                if (!operand1.startsWith("^")) {
                    builder.append("MOV AX, ")
                            .append(operand1)
                            .append("\n");
                }
                if (!operand2.startsWith("^")) {
                    builder.append("MOV BX, ")
                            .append(operand2)
                            .append("\n");
                }
                builder.append(triad.getOperator().toUpperCase())
                        .append(" AX, BX")
                        .append("\n")
                        .append("PUSH AX");
            }
            case "not" -> {
                operand1 = triad.getOperand1();

                if (operand1.startsWith("^")) {
                    builder.append(generateAsm(findTriadByIndex(triads, parseIndex(operand1))))
                            .append("POP AX")
                            .append("\n");
                }

                if (!operand1.startsWith("^")) {
                    builder.append("MOV AX, ")
                            .append(operand1)
                            .append("\n");
                }

                builder.append(triad.getOperator().toUpperCase())
                        .append(" AX")
                        .append("\n")
                        .append("PUSH AX");
            }
        }

        builder.append("\n")
                .append("---------------")
                .append("\n");
        return builder.toString();
    }

    // Convolution of triads

    private void convolutionTriads(List<Triad> noOptimizedTriads) {
        for (int i = 0; i < noOptimizedTriads.size() - 1; i++) {
            singleConvolution(noOptimizedTriads);
            var triad = noOptimizedTriads.get(i);
            if (triad.getOperator().equals("W")) {
                var found = findTriadByLink(triads, "^" + triad.getIndex());
                if (found != null) {
                    if (found.getOperand1().startsWith("^")) found.setOperand1(triad.getOperand1());
                    else found.setOperand2(triad.getOperand1());
                }
            }
        }
    }

    // One cycle of traversal of all triads for convolution

    private void singleConvolution(List<Triad> noOptimizedTriads) {
        for (int j = 0; j < noOptimizedTriads.size() - 1; j++) {
            var triad = noOptimizedTriads.get(j);
            var operand1 = triad.getOperand1();
            var operand2 = triad.getOperand2();
            // if the triad has one operand and it is hex. number OR two operands and they are both hex. numbers
            if ((Pattern.matches(HEX_PATTERN, operand1) && triad.getCountOperands() == ONE)
                    || (Pattern.matches(HEX_PATTERN, operand1)
                    && Pattern.matches(HEX_PATTERN, operand2)
                    && triad.getCountOperands() == TWO)) {
                switch (triad.getOperator()) {
                    case "not" -> {
                        if (operand1.equals(HEX_ZERO))
                            triad.setOperand1(HEX_NOT_ZERO);
                        else
                            triad.setOperand1(HEX_ZERO);
                        triad.setOperator("W");
                    }
                    case "or" -> {
                        if (operand1.equals(HEX_ZERO)) {
                            if (!operand2.equals(HEX_ZERO)) {
                                triad.setOperand1(operand2);
                            }
                        }
                        triad.setOperand2("0");
                        triad.setOperator("W");
                    }
                    case "and" -> {
                        if (operand1.equals(HEX_ZERO) || operand2.equals(HEX_ZERO)) {
                            triad.setOperand1(HEX_ZERO);
                        }
                        triad.setOperand2("0");
                        triad.setOperator("W");
                    }
                    case "xor" -> {
                        if ((!operand1.equals(HEX_ZERO) && operand2.equals(HEX_ZERO))) {
                            triad.setOperand2("0");
                        } else if ((operand1.equals(HEX_ZERO) && !operand2.equals(HEX_ZERO))) {
                            triad.setOperand1(operand2);
                            triad.setOperand2("0");
                        } else {
                            triad.setOperand1(HEX_ZERO);
                            triad.setOperand2("0");
                        }
                        triad.setOperator("W");
                    }
                }
            }
        }
    }

    // Removing collapsed triads

    private void removeCollapsedTriads(List<Triad> optimizedTriads) {
        var countCollapsedTriads = 0;

        // counting collapsed triads and removing them
        ListIterator<Triad> iterator = optimizedTriads.listIterator();
        while (iterator.hasNext()) {
            if (iterator.next().getOperator().equals("W")) {
                iterator.remove();
                ++countCollapsedTriads;
            }
        }

        // changing links to triads relative to collapsed
        // and changing the indices of the triads themselves
        for (Triad triad : optimizedTriads) {
            if (triad.getOperand1().startsWith("^")) {
                var link = Integer.parseInt(triad.getOperand1().substring(1));
                link -= countCollapsedTriads;
                triad.setOperand1("^" + link);
            } else if (triad.getOperand2().startsWith("^")) {
                var link = Integer.parseInt(triad.getOperand2().substring(1));
                link -= countCollapsedTriads;
                triad.setOperand2("^" + link);

            }
            triad.setIndex((triad.getIndex() - countCollapsedTriads));
        }
    }
}