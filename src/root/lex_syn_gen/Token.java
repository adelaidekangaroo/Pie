package root.lex_syn_gen;

public class Token {

    public enum Type {ID, ASSIGNMENT, BRACE, KEYWORD, END_STATEMENT, HEX, E}

    public Token.Type type;
    String str;

    public Token(Token.Type type, String str) {
        this.type = type;
        this.str = str;
    }

    public String getKey() {
        if (type == Token.Type.ID) {
            return "a";
        }
        if (type == Token.Type.E) {
            return "E";
        }
        if ((type == Token.Type.HEX)) {
            return "a";
        }
        return str;
    }

    public String getStr() {
        return str;
    }

    public Token.Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return getKey();
    }
}
