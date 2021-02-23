package root.type;

import java.util.*;

public class TypeAnalyzer {

    // таблица лексем
    private List<Token> tokenTable;
    private String data;
    private int resultSum = 0;

    // кратность памяти
    private static final int MULTIPLICITY_MEMORY = 8;
    // кратность элем. структур
    private static final boolean MULTIPLICITY_STRUCTURAL_ELEMENTS = true;
    // установленные скалярные типы
    private static final Map<String, Integer> SCALAR_TYPE_TABLE = new HashMap<>(Map.of("byte", 8, "extended", 10));

    // таблица структурных типов
    private Map<String, Integer> dataTypeTable = new HashMap<>();
    // таблица сохранения переменных одной структуры
    private Map<String, Integer> idsBySizeIntoSomeStruct = new HashMap<>();
    // таблица переменных в секции var
    private Map<String, Integer> idsTableByVarSector = new HashMap<>();

    // список, сохраняющий переменные одного типа на время подсчёта для всех них
    private List<String> lastIdsSomeType = new ArrayList<>();
    // содержит последний структурный тип
    private String lastDataType;

    public TypeAnalyzer(String data, List<Token> tokenTable) {
        this.data = data;
        this.tokenTable = tokenTable;
    }

    public void analyze() {
        Main.writeForTypeSize("----Входные данные---- \n");
        Main.writeForTypeSize(data);
        Main.writeForTypeSize("----Расчёт памяти---- \n");

        boolean startTypeStruct = false; // находимля ли внутри структуры
        boolean startVarSector = false; // началась ли секция var

        for (int i = 0; i < tokenTable.size() - 1; i++) {
            Token currentToken = tokenTable.get(i);
            Token nextToken = tokenTable.get(i + 1);

            if (currentToken.getType() == Token.Type.KEYWORD) {

                if (currentToken.getStr().equals("union")) {
                    startTypeStruct = true;
                    lastDataType = tokenTable.get(i - 2).getStr();
                }
                if (currentToken.getStr().equals("end")) {
                    // если структура закончилась, то идёт поиск самой большой по размеру переменной и запись структуры в таблицу
                    idsBySizeIntoSomeStruct.forEach((key, value) -> addToDataTypeTable(lastDataType, value));

                    printForTypeStruct();

                    startTypeStruct = false;
                    lastDataType = "";
                    idsBySizeIntoSomeStruct.clear();
                }
                if (currentToken.getStr().equals("var")) {
                    startVarSector = true;
                }

            }

            if (currentToken.getType() == Token.Type.ID) {
                // запись переменной в список на случай если пойдет несколько через запятую
                lastIdsSomeType.add(currentToken.getStr());
            }

            if (currentToken.getType() == Token.Type.INIT) {
                Integer sizeByScalarType = SCALAR_TYPE_TABLE.get(nextToken.getStr());
                // учитываем кратность
                int size = reductionToMultiplicity(
                        sizeByScalarType != null ? sizeByScalarType : dataTypeTable.get(nextToken.getStr()));

                if (startTypeStruct)
                    lastIdsSomeType.forEach(item -> idsBySizeIntoSomeStruct.put(item, size));

                if (startVarSector)
                    lastIdsSomeType.forEach(item -> idsTableByVarSector.put(item, size));

                lastIdsSomeType.clear();

            }
        }

        printForVarSector();
        calcSum();
        printResultSize();

    }

    // считаем по кратности
    private int reductionToMultiplicity(int size) {
        if (MULTIPLICITY_STRUCTURAL_ELEMENTS) {
            int mod = size % MULTIPLICITY_MEMORY;
            if (mod != 0)
                size += (MULTIPLICITY_MEMORY - mod);

        }

        return size;

    }

    // находим и записываем макс. для структуры
    private void addToDataTypeTable(String lastDataType, int size) {
        dataTypeTable.putIfAbsent(lastDataType, size);
        if (dataTypeTable.get(lastDataType) < size)
            dataTypeTable.put(lastDataType, size);
    }

    // считаем сумму среди перемнных секции var
    private void calcSum() {
        idsTableByVarSector.forEach((key, value) -> reduceSum(value));
    }

    private void reduceSum(int part) {
        resultSum += part;
    }

    private void printResultSize() {
        Main.writeForTypeSize("Всего: " + resultSum + " bytes");
    }

    private void printForTypeStruct() {
        StringBuilder builder = new StringBuilder();
        builder.append(lastDataType)
                .append(" = ")
                .append("max(");

        int i = 0;
        for(Map.Entry<String, Integer> entry : idsBySizeIntoSomeStruct.entrySet()) {
            builder.append(entry.getValue());
            if (i != idsBySizeIntoSomeStruct.size() - 1)
                builder.append(", ");
        }

        builder.replace(builder.length() - 2, builder.length(), ")")
                .append(" = ")
                .append(dataTypeTable.get(lastDataType));

        Main.writeForTypeSize(builder.toString());
    }

    private void printForVarSector() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");

        for(Map.Entry<String, Integer> entry : idsTableByVarSector.entrySet()) {
            builder.append(entry.getKey())
                    .append(" = ")
                    .append(entry.getValue())
                    .append("\n");
        }

        Main.writeForTypeSize(builder.toString());
    }

}
