package edu.born.pie.lex;

import edu.born.pie.Main;
import edu.born.pie.States;
import edu.born.pie.Token;

import java.util.List;
import java.util.regex.Pattern;

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
        Main.writeForLexSyn("----Входные данные---- \n\n" + data);

        Main.writeForLexSyn("");
        Main.writeForLexSyn("---- Отладка ---- \n");

        char[] chars = data.toCharArray();
        for (char ch : chars) {
            handle(ch);
            if (currentState == States.E) {
                Main.writeForLexSyn("----ОШИБКА!!!---- символ - " + ch);
                Main.closeStreams();
                System.exit(0);
            }
            if (currentState == States.S) {
                currentState = States.N; //Перезапуск автомата
            }
        }

        Main.writeForLexSyn("\n----Таблица лексем---- \n");
        for (Token token : tokenTable) {
            Main.writeForLexSyn(String.format("%14s  %s", token.getType(), token.getLabel()));
        }
    }

    String inputId = "";

    void endInputId() {
        if (!inputId.equals("")) {
            if (Main.KEY_WORDS_LIST.contains(inputId)) {
                tokenTable.add(new Token(Token.Type.KEYWORD, inputId));

            } else if (Pattern.matches(Main.HEX_PATTERN, inputId)) {
                tokenTable.add(new Token(Token.Type.HEX, inputId));
            } else {
                tokenTable.add(new Token(Token.Type.ID, inputId));
            }
            inputId = "";
        }
    }

    void handle(char ch) {

        Main.writeForLexSyn("currentState" + currentState + " " + ch);

        switch (currentState) {
            case N:
                switch (ch) {
                    case ':':
                        currentState = States.A;
                        return;
                    case '\n':
                        endInputId();
                        currentState = States.N;
                        return;
                    case ' ':
                        endInputId();
                        return;
                    case '#':
                        currentState = States.C;
                        return;
                    case '0':
                        currentState = States.Z;
                        inputId += ch;
                        return;
                    case '(':
                    case ')':
                        currentState = States.N;
                        tokenTable.add(new Token(Token.Type.BRACE, Character.toString(ch)));
                        return;
                    case ';':
                        endInputId();
                        currentState = States.S;
                        tokenTable.add(new Token(Token.Type.END_STATEMENT, ";"));
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

            case A:
                switch (ch) {
                    case '=':
                        currentState = States.N;
                        tokenTable.add(new Token(Token.Type.ASSIGNMENT, ":="));
                        return;

                    default:
                        currentState = States.E;
                        return;

                }

            case I:
                switch (ch) {
                    case ':':
                        endInputId();
                        currentState = States.A;
                        return;
                    case ';':
                        endInputId();
                        currentState = States.S;
                        tokenTable.add(new Token(Token.Type.END_STATEMENT, ";"));
                        return;
                    case '\n':
                    case ' ':
                        endInputId();
                        currentState = States.N;
                        return;
                    case '#':
                        currentState = States.C;
                        return;
                    case '(':
                    case ')':
                        currentState = States.N;
                        endInputId();
                        tokenTable.add(new Token(Token.Type.BRACE, Character.toString(ch)));
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

            case C:
                switch (ch) {
                    case '\n':
                        currentState = States.N;
                        return;
                    default:
                        return;
                }

            case Z:
                switch (ch) {
                    case 'x':
                        currentState = States.X;
                        inputId += ch;
                        return;
                    default:
                        currentState = States.E;
                        return;
                }

            case X:
                if (isHexPart(ch)) {
                    currentState = States.H;
                    inputId += ch;
                    return;
                }
                currentState = States.E;
                return;

            case H:
                switch (ch) {
                    case ';':
                        endInputId();
                        currentState = States.S;
                        tokenTable.add(new Token(Token.Type.END_STATEMENT, ";"));
                        return;
                    case '\n':
                    case ' ':
                        endInputId();
                        currentState = States.N;
                        return;
                    case '#':
                        currentState = States.C;
                        return;
                    case '(':
                    case ')':
                        currentState = States.N;
                        endInputId();
                        tokenTable.add(new Token(Token.Type.BRACE, Character.toString(ch)));
                        return;
                    default:
                        if (isHexPart(ch)) {
                            currentState = States.H;
                            inputId += ch;
                            return;
                        }
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

    boolean isHexPart(char ch) {
        return ((ch >= 'A') && (ch <= 'F') || ((ch >= '0') && (ch <= '9')));
    }

}
