package root.lex_syn_gen;

import root.Predshest;

import java.util.ArrayList;
import java.util.List;

public class PredshestTable {
    //Таблица предшествования
    public static List<Predshest> predshest_table = new ArrayList<>();

    static {
        predshest_table.add(new Predshest("(", "(", "<"));
        predshest_table.add(new Predshest("(", ")", "="));
        predshest_table.add(new Predshest("(", "or", "<"));
        predshest_table.add(new Predshest("(", "xor", "<"));
        predshest_table.add(new Predshest("(", "and", "<"));
        predshest_table.add(new Predshest("(", "not", "<"));
        predshest_table.add(new Predshest("(", "a", "<"));
        predshest_table.add(new Predshest("(", ";", " "));
        predshest_table.add(new Predshest("(", ":=", " "));

        predshest_table.add(new Predshest(")", "(", " "));
        predshest_table.add(new Predshest(")", ")", ">"));
        predshest_table.add(new Predshest(")", "or", ">"));
        predshest_table.add(new Predshest(")", "xor", ">"));
        predshest_table.add(new Predshest(")", "and", ">"));
        predshest_table.add(new Predshest(")", "not", " "));
        predshest_table.add(new Predshest(")", "a", " "));
        predshest_table.add(new Predshest(")", ";", ">"));
        predshest_table.add(new Predshest(")", ":=", " "));

        predshest_table.add(new Predshest("or", "(", "<"));
        predshest_table.add(new Predshest("or", ")", ">"));
        predshest_table.add(new Predshest("or", "or", ">"));
        predshest_table.add(new Predshest("or", "xor", ">"));
        predshest_table.add(new Predshest("or", "and", "<"));
        predshest_table.add(new Predshest("or", "not", "<"));
        predshest_table.add(new Predshest("or", "a", "<"));
        predshest_table.add(new Predshest("or", ";", ">"));
        predshest_table.add(new Predshest("or", ":=", " "));

        predshest_table.add(new Predshest("xor", "(", "<"));
        predshest_table.add(new Predshest("xor", ")", ">"));
        predshest_table.add(new Predshest("xor", "or", ">"));
        predshest_table.add(new Predshest("xor", "xor", ">"));
        predshest_table.add(new Predshest("xor", "and", "<"));
        predshest_table.add(new Predshest("xor", "not", "<"));
        predshest_table.add(new Predshest("xor", "a", "<"));
        predshest_table.add(new Predshest("xor", ";", ">"));
        predshest_table.add(new Predshest("xor", ":=", " "));

        predshest_table.add(new Predshest("and", "(", "<"));
        predshest_table.add(new Predshest("and", ")", ">"));
        predshest_table.add(new Predshest("and", "or", ">"));
        predshest_table.add(new Predshest("and", "xor", ">"));
        predshest_table.add(new Predshest("and", "and", ">"));
        predshest_table.add(new Predshest("and", "not", "<"));
        predshest_table.add(new Predshest("and", "a", "<"));
        predshest_table.add(new Predshest("and", ";", ">"));
        predshest_table.add(new Predshest("and", ":=", " "));

        predshest_table.add(new Predshest("not", "(", "<"));
        predshest_table.add(new Predshest("not", ")", " "));
        predshest_table.add(new Predshest("not", "or", " "));
        predshest_table.add(new Predshest("not", "xor", " "));
        predshest_table.add(new Predshest("not", "and", " "));
        predshest_table.add(new Predshest("not", "not", " "));
        predshest_table.add(new Predshest("not", "a", " "));
        predshest_table.add(new Predshest("not", ";", " "));
        predshest_table.add(new Predshest("not", ":=", " "));

        predshest_table.add(new Predshest("a", "(", " "));
        predshest_table.add(new Predshest("a", ")", ">"));
        predshest_table.add(new Predshest("a", "or", ">"));
        predshest_table.add(new Predshest("a", "xor", ">"));
        predshest_table.add(new Predshest("a", "and", ">"));
        predshest_table.add(new Predshest("a", "not", " "));
        predshest_table.add(new Predshest("a", "a", " "));
        predshest_table.add(new Predshest("a", ";", ">"));
        predshest_table.add(new Predshest("a", ":=", "="));

        predshest_table.add(new Predshest(";", "(", " "));
        predshest_table.add(new Predshest(";", ")", " "));
        predshest_table.add(new Predshest(";", "or", " "));
        predshest_table.add(new Predshest(";", "xor", " "));
        predshest_table.add(new Predshest(";", "and", " "));
        predshest_table.add(new Predshest(";", "not", " "));
        predshest_table.add(new Predshest(";", "a", " "));
        predshest_table.add(new Predshest(";", ";", " "));
        predshest_table.add(new Predshest(";", ":=", " "));

        predshest_table.add(new Predshest(":=", "(", "<"));
        predshest_table.add(new Predshest(":=", ")", " "));
        predshest_table.add(new Predshest(":=", "or", "<"));
        predshest_table.add(new Predshest(":=", "xor", "<"));
        predshest_table.add(new Predshest(":=", "and", "<"));
        predshest_table.add(new Predshest(":=", "not", "<"));
        predshest_table.add(new Predshest(":=", "a", "<"));
        predshest_table.add(new Predshest(":=", ";", "="));
        predshest_table.add(new Predshest(":=", ":=", " "));

    }

}
