package edu.born.pie.utils;

import edu.born.pie.lex.Token;

import java.util.List;

public class SyntacticalUtil {

    private SyntacticalUtil() {
    }

    public static String listToStr(List<Token> list) {
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
}
