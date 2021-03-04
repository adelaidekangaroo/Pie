package edu.born.pie.utils;

public class LexicalUtil {

    private LexicalUtil() {
    }

    public static boolean isValidSymbol(char ch) {
        return ((ch >= 'a') && (ch <= 'z'))
                || ((ch >= 'A') && (ch <= 'Z')
                || ((ch >= '0') && (ch <= '9'))
        );
    }

    public static boolean isAZ(char ch) {
        return ((ch >= 'a') && (ch <= 'z')
                || (ch >= 'A') && (ch <= 'Z')
        );
    }

    public static boolean isHexPart(char ch) {
        return ((ch >= 'A') && (ch <= 'F')
                || ((ch >= '0') && (ch <= '9')));
    }
}
