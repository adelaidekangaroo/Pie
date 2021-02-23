package root.type;

import java.util.List;

public class LexicalAnalyzer {

    private States currentState;
    private String data;
    private List<Token> tokenTable;

    private String lineSeparator = System.getProperty("line.separator");//Символ-разделитель строк

    public LexicalAnalyzer(States currentState, String data, List<Token> tokenTable) {
        this.currentState = currentState;
        this.data = data;
        this.tokenTable = tokenTable;
    }

    public void analyze() {
        data = data.replace(lineSeparator, "\n");//Чтобы переносы строк  занимали не 2 символа, а 1
        Main.writeForTypeSyn("----Входные данные---- \n\n" + data);

       /* Main.writeForTypeSyn("");
        Main.writeForTypeSyn("---- Отладка ---- \n");*/

        char[] chars = data.toCharArray();
        for (char ch : chars) {
            handle(ch);
            if (currentState == States.E) {
                Main.writeForTypeSyn("----ОШИБКА!!!---- символ - " + ch);
                Main.closeStreams();
                System.exit(0);
            }
            if (currentState == States.S) {
                currentState = States.N; //Перезапуск автомата
            }
        }

        Main.writeForTypeSyn("\n----Таблица лексем---- \n");
        for (Token token : tokenTable) {
            Main.writeForTypeSyn(String.format("%14s  %s", token.type, token.getStr()));
        }
    }

    String inputId = "";

    void endInputId(String sign) {
        if (!inputId.equals("")) {
            if (Main.KEY_WORDS_LIST.contains(inputId)) {
                tokenTable.add(new Token(Token.Type.KEYWORD, inputId));

            } else if (Main.SCALAR_TYPES.contains(inputId)) {
                tokenTable.add(new Token(Token.Type.SCALAR_TYPE, inputId));

            } else if (sign.equals(";")) {
                tokenTable.add(new Token(Token.Type.DATA_TYPE, inputId));
            } else if (sign.equals("=")) {
                tokenTable.add(new Token(Token.Type.DATA_TYPE, inputId));
            } else if (sign.equals(":") || sign.equals(",")) {
                tokenTable.add(new Token(Token.Type.ID, inputId));
            }
            inputId = "";
        }
    }

    void handle(char ch) {
       // Main.writeForTypeSyn("currentState" + currentState + " " + ch);

        switch (currentState) {
            case N:
                switch (ch) {
                    case '\t':
                    case '\n':
                    case ' ':
                        currentState = States.N;
                        return;
                    case '#':
                        currentState = States.C;
                        return;
                    default:
                        if ((ch >= 'a') && (ch <= 'z')
                                || (ch >= 'A') && (ch <= 'Z')) {
                            currentState = States.I;
                            inputId += ch;
                            return;
                        }
                        currentState = States.E;
                }
                break;
            case I:
                switch (ch) {
                    case ':':
                        endInputId(String.valueOf(ch));
                        currentState = States.N;
                        tokenTable.add(new Token(Token.Type.INIT, ":"));
                        return;
                    case ';':
                        endInputId(String.valueOf(ch));
                        currentState = States.S;
                        tokenTable.add(new Token(Token.Type.END_STATEMENT, ";"));
                        return;
                    case '\n':
                        endInputId(String.valueOf(ch));
                        currentState = States.N;
                        return;
                    case ' ':
                     //   endInputId(String.valueOf(ch));
                        currentState = States.I;
                        return;
                    case '=':
                        endInputId(String.valueOf(ch));
                        tokenTable.add(new Token(Token.Type.ASSIGNMENT, "="));
                        currentState = States.N;
                        return;
                    case ',':
                        endInputId(String.valueOf(ch));
                        currentState = States.I;
                        tokenTable.add(new Token(Token.Type.COMMA, ","));
                        return;
                    case '#':
                        endInputId(String.valueOf(ch));
                        currentState = States.C;
                        return;
                    default:
                        if (isValidSymbol(ch)) {
                            currentState = States.I;
                            inputId += ch;
                            return;
                        }
                        currentState = States.E;
                        return;
                }

            case A:
                switch (ch) {
                    case ' ':
                        currentState = States.N;
                        tokenTable.add(new Token(Token.Type.ASSIGNMENT, "="));
                        return;

                    default:
                        currentState = States.E;
                        return;

                }
            case C:
                switch (ch) {
                    case '\n':
                        currentState = States.N;
                        return;
                    default:
                        return;
                }

            case T:
                switch (ch) {
                    case '=':
                        currentState = States.A;
                        return;
                    case ' ':
                        currentState = States.T;
                        return;
                    default:
                        currentState = States.E;
                        return;
                }

            case IN:
                switch (ch) {
                    case ' ':
                        currentState = States.N;
                        return;
                    default:
                        currentState = States.E;
                        return;
                }

        }
    }

    boolean isValidSymbol(char ch) {
        return ((ch >= 'a') && (ch <= 'z'))
                || ((ch >= 'A') && (ch <= 'Z')
                || ((ch >= '0') && (ch <= '9'))
        );
    }

}
