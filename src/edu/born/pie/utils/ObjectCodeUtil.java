package edu.born.pie.utils;

import edu.born.pie.model.Node;
import edu.born.pie.model.Triad;

import java.util.List;
import java.util.Optional;

import static edu.born.pie.Pie.BRACE_LIST;
import static edu.born.pie.Pie.OPERATORS_LIST;

public class ObjectCodeUtil {

    private ObjectCodeUtil() {
    }

    public static int parseIndex(String index) {
        return Integer.parseInt(index.substring(1));
    }

    public static boolean isOperand(Node node) {

        for (String keyWord : OPERATORS_LIST)
            if (node.getText().equals(keyWord))
                return true;

        return false;
    }

    public static boolean isBrace(Node node) {

        for (String brace : BRACE_LIST)
            if (node.getText().equals(brace))
                return true;

        return false;
    }

    public static String minimizePushPop(String code) {
        return code.replaceAll("PUSH AX.*\n---------------\nPOP AX", "")
                .replaceAll("PUSH AX.*\n---------------\nPOP BX", "\nMOVE BX, AX")
                .replaceAll("---------------\n", "");
    }

    public static Optional<Node> findE(List<Node> nodes) {

        return nodes.stream()
                .filter(node -> !isOperand(node) && !isBrace(node))
                .findFirst();
    }

    public static Triad findTriadByIndex(List<Triad> triads, int index) {

        return triads.stream()
                .filter(triad -> triad.getIndex() == index)
                .findFirst()
                .orElse(null);
    }

    public static Triad findTriadByLink(List<Triad> triads, String link) {

        return triads.stream()
                .filter(triad -> triad.getOperand1().equals(link) || triad.getOperand2().equals(link))
                .findFirst()
                .orElse(null);
    }
}
