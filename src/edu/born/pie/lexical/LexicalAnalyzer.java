package edu.born.pie.lexical;

import edu.born.pie.model.States;
import edu.born.pie.model.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static edu.born.pie.Main.HEX_PATTERN;
import static edu.born.pie.Main.KEY_WORDS_LIST;
import static edu.born.pie.model.Token.Type.*;
import static edu.born.pie.model.Token.of;
import static edu.born.pie.utils.LexicalUtil.*;
import static edu.born.pie.utils.PrintUtil.*;

public class LexicalAnalyzer {

    private States currentState;
    private String data;
    private String inputId = "";
    private final List<Token> tokenTable = new ArrayList<>();

    public LexicalAnalyzer(String data) {
        this.currentState = States.N;
        this.data = data;
    }

    public List<Token> analyze() {
        data = splitLines(data);

        print(INPUT_TITLE);
        print(data);
        print(DEBUGGING_TITLE);

        char[] chars = data.toCharArray();

        for (char ch : chars) {
            print(String.format("currentState%s %s", currentState, ch));
            handle(ch);
            if (currentState == States.E) {
                print(String.format("%s symbol - %s", ERROR_TITLE, ch));
                closeStream();
                System.exit(0);
            }
            if (currentState == States.S) {
                currentState = States.N; // restart
            }
        }

        print(TOKEN_TABLE_TITLE);
        for (Token token : tokenTable) {
            print(String.format("%14s  %s", token.getType(), token.getLabel()));
        }

        return tokenTable;
    }

    private void endInputId() {
        if (!inputId.equals("")) {
            if (KEY_WORDS_LIST.contains(inputId)) {
                tokenTable.add(of(KEYWORD, inputId));
            } else if (Pattern.matches(HEX_PATTERN, inputId)) {
                tokenTable.add(of(HEX, inputId));
            } else {
                tokenTable.add(of(ID, inputId));
            }
            inputId = "";
        }
    }

    private void handle(char ch) {
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
                        tokenTable.add(of(BRACE, ch));
                    }
                    case ';' -> {
                        endInputId();
                        currentState = States.S;
                        tokenTable.add(of(END_STATEMENT, ";"));
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
                    tokenTable.add(of(ASSIGNMENT, ":="));
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
                        tokenTable.add(of(END_STATEMENT, ";"));
                    }
                    case '\n', ' ' -> {
                        endInputId();
                        currentState = States.N;
                    }
                    case '#' -> currentState = States.C;
                    case '(', ')' -> {
                        currentState = States.N;
                        endInputId();
                        tokenTable.add(of(BRACE, ch));
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
                        tokenTable.add(of(END_STATEMENT, ";"));
                    }
                    case '\n', ' ' -> {
                        endInputId();
                        currentState = States.N;
                    }
                    case '#' -> currentState = States.C;
                    case '(', ')' -> {
                        currentState = States.N;
                        endInputId();
                        tokenTable.add(of(BRACE, ch));
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