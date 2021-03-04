package edu.born.pie.utils;

import edu.born.pie.Main;
import edu.born.pie.syntactical.Node;

public class ObjectCodeUtil {

    private ObjectCodeUtil() {
    }

    public static int parseIndex(String index) {
        return Integer.parseInt(index.substring(1));
    }

    public static boolean isOperand(Node node) {

        for (String keyWord : Main.OPERATORS_LIST)
            if (node.getText().equals(keyWord))
                return true;


        return false;
    }

    public static boolean isBrace(Node node) {

        for (String brace : Main.BRACE_LIST)
            if (node.getText().equals(brace))
                return true;

        return false;
    }
}
