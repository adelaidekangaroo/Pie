package edu.born.pie;

import java.util.List;

import static edu.born.pie.Precedence.of;

public class PrecedenceTable {

    private PrecedenceTable() {
    }

    public static final List<Precedence> PRECEDENCE_TABLE = List.of(
            of("(", "(", "<"),
            of("(", ")", "="),
            of("(", "or", "<"),
            of("(", "xor", "<"),
            of("(", "and", "<"),
            of("(", "not", "<"),
            of("(", "a", "<"),
            of("(", ";", " "),
            of("(", ":=", " "),

            of(")", "(", " "),
            of(")", ")", ">"),
            of(")", "or", ">"),
            of(")", "xor", ">"),
            of(")", "and", ">"),
            of(")", "not", " "),
            of(")", "a", " "),
            of(")", ";", ">"),
            of(")", ":=", " "),

            of("or", "(", "<"),
            of("or", ")", ">"),
            of("or", "or", ">"),
            of("or", "xor", ">"),
            of("or", "and", "<"),
            of("or", "not", "<"),
            of("or", "a", "<"),
            of("or", ";", ">"),
            of("or", ":=", " "),

            of("xor", "(", "<"),
            of("xor", ")", ">"),
            of("xor", "or", ">"),
            of("xor", "xor", ">"),
            of("xor", "and", "<"),
            of("xor", "not", "<"),
            of("xor", "a", "<"),
            of("xor", ";", ">"),
            of("xor", ":=", " "),

            of("and", "(", "<"),
            of("and", ")", ">"),
            of("and", "or", ">"),
            of("and", "xor", ">"),
            of("and", "and", ">"),
            of("and", "not", "<"),
            of("and", "a", "<"),
            of("and", ";", ">"),
            of("and", ":=", " "),

            of("not", "(", "<"),
            of("not", ")", " "),
            of("not", "or", " "),
            of("not", "xor", " "),
            of("not", "and", " "),
            of("not", "not", " "),
            of("not", "a", " "),
            of("not", ";", " "),
            of("not", ":=", " "),

            of("a", "(", " "),
            of("a", ")", ">"),
            of("a", "or", ">"),
            of("a", "xor", ">"),
            of("a", "and", ">"),
            of("a", "not", " "),
            of("a", "a", " "),
            of("a", ";", ">"),
            of("a", ":=", "="),

            of(";", "(", " "),
            of(";", ")", " "),
            of(";", "or", " "),
            of(";", "xor", " "),
            of(";", "and", " "),
            of(";", "not", " "),
            of(";", "a", " "),
            of(";", ";", " "),
            of(";", ":=", " "),

            of(":=", "(", "<"),
            of(":=", ")", " "),
            of(":=", "or", "<"),
            of(":=", "xor", "<"),
            of(":=", "and", "<"),
            of(":=", "not", "<"),
            of(":=", "a", "<"),
            of(":=", ";", "="),
            of(":=", ":=", " ")
    );
}
