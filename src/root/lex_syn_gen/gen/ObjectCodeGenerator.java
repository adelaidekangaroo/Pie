package root.lex_syn_gen.gen;

import root.lex_syn_gen.Main;
import root.lex_syn_gen.Node;
import root.lex_syn_gen.Triad;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.regex.Pattern;

public class ObjectCodeGenerator {

    private Node root;
    private List<Triad> triads;
    private int triadCounter = 0;

    public ObjectCodeGenerator(Node root, List<Triad> triads) {
        this.root = root;
        this.triads = triads;
    }

    public void generate() {
        walkToTree(root);

        Main.writeForGen("Триады:\n");

        triads.forEach(triad ->
                Main.writeForGen(triad.toString()));

        Main.writeForGen("\nКод:\n");

        Triad lastTriad = triads.get(triads.size() - 1);
        String code = generateAsm(lastTriad);

        Main.writeForGen(code);

        wrapTriads(triads);

        Main.writeForGen("Сворачиваем триады:\n");
        Main.writeForGen("Первый этап:\n");

        triads.forEach(triad ->
                Main.writeForGen(triad.toString()));

        skipWrappedTriad(triads);

        Main.writeForGen("\nВторой этап:\n");

        triads.forEach(triad ->
                Main.writeForGen(triad.toString()));

        lastTriad = triads.get(triads.size() - 1);
        code = generateAsm(lastTriad);

        code = code.replaceAll("PUSH AX.*\n" + "---------------\n" + "POP AX", "");
        code = code.replaceAll("PUSH AX.*\n" + "---------------\n" + "POP BX", "\nMOVE BX, AX");
        code = code.replaceAll("---------------\n", "");

        Main.writeForGen("\nОптимизированный код:\n");

        Main.writeForGen(code);
    }

    /**
     * Рекурсивный обход дерева и генерация триад
     */

    private String walkToTree(Node node) {

        List<Node> children = null;

        // если у вершины нет листьев, то вернуть саму вершину
        if (node.getChildren().size() == 0) return node.getText();
        // если у вершины среди листьев нет оператора, то для каждого E-листа вызвать обход глубже
        children = node.getChildren();
        if (children.stream().noneMatch(this::isOperand)) {
            for (Node child : children) {
                if (!isBrace(child)) return walkToTree(child);
            }
        } else {
            // если есть оператор, но вызвать обход для двух его операндов
            Optional<Node> firstOperand = Optional.empty();
            Optional<Node> secondOperand = Optional.empty();
            String operator = String.valueOf("");

            for (int i = 0; i < children.size() - 1; i++) {
                if (isOperand(children.get(i))) {
                    operator = children.get(i).getText();
                    firstOperand = findE(children.subList(0, i));
                    secondOperand = findE(children.subList(i + 1, children.size()));
                }
            }

            Triad triad;

            // если нет операнда слева от оператора
            if (!firstOperand.isPresent()) {
                triad = new Triad(operator, walkToTree(secondOperand.get()), "", Triad.COUNT_OPERANDS.ONE);
            } else {
                // оператор с двумя операндами
                triad = new Triad(operator, walkToTree(firstOperand.get()), walkToTree(secondOperand.get()), Triad.COUNT_OPERANDS.TWO);

            }

            ++triadCounter;
            triad.setIndex(triadCounter);
            triads.add(triad);
            // вернуть индекс триады
            return "^" + triadCounter;

        }

        return "";

    }

    /**
     * Рекурсивный обход триад и генерация ассемблерного кода
     */

    private String generateAsm(Triad triad) {
        StringBuilder builder = new StringBuilder();

        String operand1;
        String operand2;

        switch (triad.getOperator()) {

            case ":=":
                operand1 = triad.getOperand1();
                operand2 = triad.getOperand2();

                // если операнд ссылка на триаду, то сначала выполнить для той триады
                if (operand2.startsWith("^")) {
                    builder.append(generateAsm(findTriadByIndex(parseIndex(operand2))));
                    builder.append("POP AX");
                    builder.append("\n");
                }

                if (!operand2.startsWith("^")) {
                    builder.append("MOV AX, " + operand2);
                    builder.append("\n");
                }

                builder.append("MOV " + operand1 + ", AX");

                break;

            case "or":
            case "and":
            case "xor":
                operand1 = triad.getOperand1();
                operand2 = triad.getOperand2();

                if (operand1.startsWith("^")) {
                    builder.append(generateAsm(findTriadByIndex(parseIndex(operand1))));
                    builder.append("POP AX");
                    builder.append("\n");
                }

                if (operand2.startsWith("^")) {
                    builder.append(generateAsm(findTriadByIndex(parseIndex(operand2))));
                    builder.append("POP BX");
                    builder.append("\n");
                }

                if (!operand1.startsWith("^")) {
                    builder.append("MOV AX, " + operand1);
                    builder.append("\n");
                }

                if (!operand2.startsWith("^")) {
                    builder.append("MOV BX, " + operand2);
                    builder.append("\n");
                }

                builder.append(triad.getOperator().toUpperCase() + " AX, BX");
                builder.append("\n");
                builder.append("PUSH AX");

                break;

            case "not": {
                operand1 = triad.getOperand1();

                if (operand1.startsWith("^")) {
                    builder.append(generateAsm(findTriadByIndex(parseIndex(operand1))));
                    builder.append("POP AX");
                    builder.append("\n");
                }

                if (!operand1.startsWith("^")) {
                    builder.append("MOV AX, " + operand1);
                    builder.append("\n");
                }

                builder.append(triad.getOperator().toUpperCase() + " AX");
                builder.append("\n");
                builder.append("PUSH AX");

                break;
            }
        }

        builder.append("\n");
        builder.append("---------------");
        builder.append("\n");
        return builder.toString();

    }

    // свёртка триад
    private void wrapTriads(List<Triad> noOptimizedTriads) {
        for (int i = 0; i < noOptimizedTriads.size() - 1; i++) {

            singleWrap(noOptimizedTriads);
            Triad triad = noOptimizedTriads.get(i);
            if (triad.getOperator().equals("W")) {
                Triad finded = findTriadByLink("^" + triad.getIndex());
                if (finded != null) {
                    if (finded.getOperand1().startsWith("^")) finded.setOperand1(triad.getOperand1());
                    else finded.setOperand2(triad.getOperand1());
                }
            }

        }
    }

    // один цикл обхода всех триад для свёртки
    private void singleWrap(List<Triad> noOptimizedTriads) {
        for (int j = 0; j < noOptimizedTriads.size() - 1; j++) {
            Triad triad = noOptimizedTriads.get(j);
            String operand1 = triad.getOperand1();
            String operand2 = triad.getOperand2();
            // если у триады один опренад и он шестн. число ИЛИ два операнда, и они оба шестн. числа
            if ((Pattern.matches(Main.HEX_PATTERN, operand1) && triad.getCount_operands() == Triad.COUNT_OPERANDS.ONE) ||
                    (Pattern.matches(Main.HEX_PATTERN, operand1) && Pattern.matches(Main.HEX_PATTERN, operand2)
                            && triad.getCount_operands() == Triad.COUNT_OPERANDS.TWO)) {
                switch (triad.getOperator()) {
                    case "not":
                        if (operand1.equals(Main.HEX_ZERO))
                            triad.setOperand1(Main.HEX_NOT_ZERO);
                        else
                            triad.setOperand1(Main.HEX_ZERO);
                        triad.setOperator("W");
                        break;
                    case "or":
                        if (operand1.equals(Main.HEX_ZERO)) {
                            if (operand2.equals(Main.HEX_ZERO)) {
                                triad.setOperand2("0");
                            } else {
                                triad.setOperand1(operand2);
                                triad.setOperand2("0");
                            }
                        } else {
                            if (operand2.equals(Main.HEX_ZERO)) {
                                triad.setOperand2("0");
                            } else {
                                triad.setOperand2("0");
                            }
                        }
                        triad.setOperator("W");
                        break;
                    case "and":
                        if (operand1.equals(Main.HEX_ZERO) || operand2.equals(Main.HEX_ZERO)) {
                            triad.setOperand1(Main.HEX_ZERO);
                            triad.setOperand2("0");
                        } else {
                            triad.setOperand2("0");
                        }
                        triad.setOperator("W");
                        break;
                    case "xor":
                        if ((!operand1.equals(Main.HEX_ZERO) && operand2.equals(Main.HEX_ZERO))) {
                            triad.setOperand2("0");
                        } else if ((operand1.equals(Main.HEX_ZERO) && !operand2.equals(Main.HEX_ZERO))) {
                            triad.setOperand1(operand2);
                            triad.setOperand2("0");
                        } else {
                            triad.setOperand1(Main.HEX_ZERO);
                            triad.setOperand2("0");
                        }
                        triad.setOperator("W");
                        break;

                }


            }
        }
    }

    // удаление свёрнутых триад
    private void skipWrappedTriad(List<Triad> optimizedTriads) {
        int countWrappedTriads = 0;

        // подсчёт свёрнутых триад и их удаление
        ListIterator<Triad> iter = optimizedTriads.listIterator();
        while (iter.hasNext()) {
            if (iter.next().getOperator().equals("W")) {
                iter.remove();
                ++countWrappedTriads;
            }
        }

        // изменение ссылок на триады относительно свёрнутых
        // и изменение индексов самих триад
        for (Triad triad : optimizedTriads) {
            if (triad.getOperand1().startsWith("^")) {
                int link = Integer.valueOf(triad.getOperand1().substring(1));
                link -= countWrappedTriads;
                triad.setOperand1("^" + link);
            } else if (triad.getOperand2().startsWith("^")) {
                int link = Integer.valueOf(triad.getOperand2().substring(1));
                link -= countWrappedTriads;
                triad.setOperand2("^" + link);

            }
            triad.setIndex((triad.getIndex() - countWrappedTriads));
        }

    }

    private boolean isOperand(Node node) {

        for (String keyWord : Main.OPERATORS_LIST)
            if (node.getText().equals(keyWord))
                return true;


        return false;
    }

    private boolean isBrace(Node node) {

        for (String brace : Main.BRACE_LIST)
            if (node.getText().equals(brace))
                return true;

        return false;
    }

    private Optional<Node> findE(List<Node> nodes) {

        return nodes.stream()
                .filter(node -> !isOperand(node) && !isBrace(node))
                .findFirst();

    }

    private Triad findTriadByIndex(int index) {

        return triads.stream()
                .filter(triad -> triad.getIndex() == index)
                .findFirst()
                .orElse(null);
    }

    private Triad findTriadByLink(String link) {

        return triads.stream()
                .filter(triad -> triad.getOperand1().equals(link) || triad.getOperand2().equals(link))
                .findFirst()
                .orElse(null);
    }

    private int parseIndex(String index) {
        return Integer.valueOf(index.substring(1));
    }

}
