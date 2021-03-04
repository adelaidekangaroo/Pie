package edu.born.pie.lex;

import edu.born.pie.Main;
import edu.born.pie.States;
import edu.born.pie.Token;

import java.util.List;
import java.util.regex.Pattern;

import static edu.born.pie.LexicalUtil.*;
import static edu.born.pie.Token.Type;
import static edu.born.pie.Token.of;

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
                tokenTable.add(of(Type.KEYWORD, inputId));

            } else if (Pattern.matches(Main.HEX_PATTERN, inputId)) {
                tokenTable.add(of(Type.HEX, inputId));
            } else {
                tokenTable.add(of(Type.ID, inputId));
            }
            inputId = "";
        }
    }

    void handle(char ch) {

        Main.writeForLexSyn("currentState" + currentState + " " + ch);

        switch (currentState) {
            case N -> {
                switch (ch) {
                    case ':' -> currentState = States.A;
                    case '\n' -> {
                        endInputId();
                        currentState = States.N;
                    }
                    case ' ' -> endInputId();
                    case '#' -> currentState = States.C;
                    case '0' -> {
                        currentState = States.Z;
                        inputId += ch;
                    }
                    case '(', ')' -> {
                        currentState = States.N;
                        tokenTable.add(of(Type.BRACE, ch));
                    }
                    case ';' -> {
                        endInputId();
                        currentState = States.S;
                        tokenTable.add(of(Type.END_STATEMENT, ";"));
                    }
                    default -> {
                        if (isAZ(ch)) {
                            currentState = States.I;
                            inputId += ch;
                            return;
                        }
                        currentState = States.E;
                    }
                }
            }
            case A -> {
                if (ch == '=') {
                    currentState = States.N;
                    tokenTable.add(of(Type.ASSIGNMENT, ":="));
                    return;
                }
                currentState = States.E;
            }
            case I -> {
                switch (ch) {
                    case ':' -> {
                        endInputId();
                        currentState = States.A;
                    }
                    case ';' -> {
                        endInputId();
                        currentState = States.S;
                        tokenTable.add(of(Type.END_STATEMENT, ";"));
                    }
                    case '\n', ' ' -> {
                        endInputId();
                        currentState = States.N;
                    }
                    case '#' -> currentState = States.C;
                    case '(', ')' -> {
                        currentState = States.N;
                        endInputId();
                        tokenTable.add(of(Type.BRACE, ch));
                    }
                    default -> {
                        if (isValidSymbol(ch)) {
                            currentState = States.I;
                            inputId += ch;
                            return;
                        }
                        currentState = States.E;
                    }
                }
            }
            case C -> {
                if (ch == '\n') {
                    currentState = States.N;
                    return;
                }
            }
            case Z -> {
                if (ch == 'x') {
                    currentState = States.X;
                    inputId += ch;
                    return;
                }
                currentState = States.E;
            }
            case X -> {
                if (isHexPart(ch)) {
                    currentState = States.H;
                    inputId += ch;
                    return;
                }
                currentState = States.E;
            }
            case H -> {
                switch (ch) {
                    case ';' -> {
                        endInputId();
                        currentState = States.S;
                        tokenTable.add(of(Type.END_STATEMENT, ";"));
                    }
                    case '\n', ' ' -> {
                        endInputId();
                        currentState = States.N;
                    }
                    case '#' -> currentState = States.C;
                    case '(', ')' -> {
                        currentState = States.N;
                        endInputId();
                        tokenTable.add(of(Type.BRACE, ch));
                    }
                    default -> {
                        if (isHexPart(ch)) {
                            currentState = States.H;
                            inputId += ch;
                            return;
                        }
                        currentState = States.E;
                    }
                }
            }
        }
    }
}
