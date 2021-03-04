package edu.born.pie;

public class Token {

    public enum Type {ID, ASSIGNMENT, BRACE, KEYWORD, END_STATEMENT, HEX, E}

    private final Token.Type type;
    private final String label;

    public Token(Token.Type type, String label) {
        this.type = type;
        this.label = label;
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
