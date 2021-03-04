package edu.born.pie.lex;

public class Token {

    public enum Type {ID, ASSIGNMENT, BRACE, KEYWORD, END_STATEMENT, HEX, E}

    private final Token.Type type;
    private final String label;

    private Token(Token.Type type, String label) {
        this.type = type;
        this.label = label;
    }

    public static Token of(Token.Type type, char label) {
        return new Token(type, Character.toString(label));
    }

    public static Token of(Token.Type type, String label) {
        return new Token(type, label);
    }

    public String getKey() {
        return switch (type) {
            case ID, HEX -> "a";
            case E -> "E";
            default -> label;
        };
    }

    public String getLabel() {
        return label;
    }

    public Token.Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return getKey();
    }
}
