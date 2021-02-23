package root.type;

public class Token {

    public enum Type {ID, ASSIGNMENT, KEYWORD, END_STATEMENT, INIT, DATA_TYPE, SCALAR_TYPE, COMMA, E}

    public Token.Type type;
    private String str;

    public Token(Token.Type type, String str) {
        this.type = type;
        this.str = str;
    }

    public String getKey() {
        if (type == Token.Type.ID) {
            return "a";
        }
        if (type == Type.DATA_TYPE) {
            return "t";
        }
        if ((type == Type.SCALAR_TYPE)) {
            return "c";
        }
        if (type == Type.E) {
            return "E";
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
