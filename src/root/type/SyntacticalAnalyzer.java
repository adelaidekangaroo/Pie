package root.type;

import root.Predshest;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class SyntacticalAnalyzer {

    private List<Token> tokenTable;
    private List<Predshest> predshestTable;

    private LinkedList<Token> inputQueue;
    private LinkedList<Token> memoryStack;

    public SyntacticalAnalyzer(List<Token> tokenTable, List<Predshest> predshestTable) {
        this.tokenTable = tokenTable;
        this.predshestTable = predshestTable;
    }

    public void analyze() {
        inputQueue = new LinkedList<>();
        memoryStack = new LinkedList<>();

        inputQueue.addAll(tokenTable);

        boolean empty;
        do {
            Main.writeForTypeSyn("");
            String text = "Лента - " + listToStr(inputQueue) + "   Память - " + listToStr(memoryStack);
            Main.writeForTypeSyn(text);

            Token nextToken = inputQueue.peek();

            // если память пуста берём первый из ленты
            if (memoryStack.isEmpty()) {
                Main.writeForTypeSyn("Действие - Перенос");
                memoryStack.add(inputQueue.poll());
            } else {
                // если память не пуста берём последний из памяти
                Token memToken = memoryStack.getLast();
                if (memToken.getKey().equals("E")) {
                    // если он Е и в памяти есть ещё элементы - то берём предпоследний
                    if (memoryStack.size() >= 2) {
                        memToken = memoryStack.get(memoryStack.size() - 2);

                    }
                }
                // сравнение
                String compare;
                // если в ленте есть элементы, то сравнить по таблице предшествования
                if (nextToken != null) {
                    compare = compareTokens(memToken, nextToken);
                } else {
                    //если в ленте пусто, то ">" (свёртка)
                    compare = ">";
                }

                if (compare.equals("<") || compare.equals("=")) {
                    Main.writeForTypeSyn("Действие - Перенос");
                    memoryStack.add(inputQueue.poll());
                } else {
                    Main.writeForTypeSyn("Действие - Свёртка " + wrap());
                }

            }

            empty = inputQueue.isEmpty() && (memoryStack.size() == 1);
            // продолжать пока в ленте не пусто, а в памяти не останется 1 элемент
        } while (!empty);

        Main.writeForTypeSyn("");
        String text = "Лента - " + listToStr(inputQueue) + "   Память - " + listToStr(memoryStack);
        Main.writeForTypeSyn(text);

    }

    String listToStr(List<Token> list) {
        StringBuffer s = new StringBuffer("[");
        list.forEach(object -> {
            if (object != null) {
                s.append(object.toString() + " ");
            } else {
                s.append("null");
            }
        });

        return s.toString().trim() + "]";
    }

    /**
     * Свёртка
     */

    int wrap() {
        Token last1 = memoryStack.size() != 0 ? memoryStack.get(memoryStack.size() - 1) : null;

        //Правило 9
        if (last1 != null) {
            if (last1.getKey().equals("a")) {
                if (memoryStack.size() >= 3) {
                    Token last2 = memoryStack.get(memoryStack.size() - 2);
                    Token last3 = memoryStack.get(memoryStack.size() - 3);
                    //Правило 10
                    if (last3.getKey().equals("E") && last2.getKey().equals(",") && last1.getKey().equals("a")) {
                        memoryStack.removeLast();
                        memoryStack.removeLast();
                        return 10;
                    }

                }
                memoryStack.set(memoryStack.size() - 1, new Token(Token.Type.E, "E"));

                return 9;
            }
        }


        Token last2 = memoryStack.size() != 1 ? memoryStack.get(memoryStack.size() - 2) : null;

        if (last2 != null) {
            //Правило 2
            if (last2.getKey().equals("E") && last1.getKey().equals(";")) {
                memoryStack.removeLast();
                return 2;
            }
        }

        Token last3 = memoryStack.size() != 2 ? memoryStack.get(memoryStack.size() - 3) : null;

        if (last3 != null) {
            //Правило 3
            if (last3.getKey().equals("E") && last2.getKey().equals(";") && last1.getKey().equals("E")) {
                memoryStack.removeLast();
                memoryStack.removeLast();
                return 3;
            }

            //Правило 4
            if (last3.getKey().equals("t") && last2.getKey().equals("=") && last1.getKey().equals("c")) {
                memoryStack.removeLast();
                memoryStack.removeLast();
                memoryStack.set(memoryStack.size() - 1, new Token(Token.Type.E, ""));
                return 4;
            }

            //Правило 5
            if (last3.getKey().equals("t") && last2.getKey().equals("=") && last1.getKey().equals("E")) {
                memoryStack.removeLast();
                memoryStack.removeLast();
                memoryStack.set(memoryStack.size() - 1, new Token(Token.Type.E, ""));
                return 5;
            }

            //Правило 6, 7, 8
            if (last3.getKey().equals("E") && last2.getKey().equals(":")) {
                int wrapId = 0;
                if (last1.getKey().equals("t")) wrapId = 6;
                if (last1.getKey().equals("c")) wrapId = 7;
                if (last1.getKey().equals("E")) wrapId = 8;
                memoryStack.removeLast();
                memoryStack.removeLast();
                return wrapId;
            }


            //Правило 11
            if (last3.getKey().equals("union") && last2.getKey().equals("E") && last1.getKey().equals("end")) {
                memoryStack.removeLast();
                memoryStack.removeLast();
                memoryStack.set(memoryStack.size() - 1, new Token(Token.Type.E, ""));
                return 11;
            }
        }
        Token last4 = memoryStack.size() != 3 ? memoryStack.get(memoryStack.size() - 4) : null;
        if (last4 != null) {
            //Правило 1
            if (last4.getKey().equals("type") && last3.getKey().equals("E")
                    && last2.getKey().equals("var") && last1.getKey().equals("E")) {
                memoryStack.removeLast();
                memoryStack.removeLast();
                memoryStack.removeLast();
                memoryStack.set(memoryStack.size() - 1, new Token(Token.Type.E, ""));
                return 1;
            }
        }

        Main.writeForTypeSyn("----ОШИБКА!!!---- Нет правила! - " + last1);
        String text = "Лента - " + listToStr(inputQueue) + "   Память - " + listToStr(memoryStack);
        Main.writeForTypeSyn(text);
        Main.closeStreams();
        System.exit(0);


        return 0;
    }

    String compareTokens(Token token1, Token token2) {
        String token1Str;
        String token2Str;
        String predshestResult = String.valueOf("");
        token1Str = token1.getKey();
        token2Str = token2.getKey();

        Optional<Predshest> predshestOptional = predshestTable.stream().filter(
                pred -> (pred.getLeft().equals(token1Str) && (pred.getRigth().equals(token2Str)))).findFirst();

        if (predshestOptional.isPresent()) {
            predshestResult = predshestOptional.get().getResult();
        } else {
            Main.writeForTypeSyn("ERROR! " + token1Str + " " + token2Str);
        }
        Main.writeForTypeSyn("Compare... " + token1Str + " " + predshestResult + " " + token2Str);
        return predshestResult;
    }

}
