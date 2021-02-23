package root.type;

import root.Predshest;

import java.util.ArrayList;
import java.util.List;

public class PredshestTable {
    //Таблица предшествования
    public static List<Predshest> predshest_table = new ArrayList<>();

    static {
        predshest_table.add(new Predshest("type", "type", " "));
        predshest_table.add(new Predshest("type", "var", "="));
        predshest_table.add(new Predshest("type", "t", "<"));
        predshest_table.add(new Predshest("type", "c", " "));
        predshest_table.add(new Predshest("type", ";", "<"));
        predshest_table.add(new Predshest("type", ":", " "));
        predshest_table.add(new Predshest("type", "=", "="));
        predshest_table.add(new Predshest("type", "union", " "));
        predshest_table.add(new Predshest("type", "end", " "));
        predshest_table.add(new Predshest("type", "a", " "));
        predshest_table.add(new Predshest("type", ",", " "));

        predshest_table.add(new Predshest("var", "type", " "));
        predshest_table.add(new Predshest("var", "var", " "));
        predshest_table.add(new Predshest("var", "t", " "));
        predshest_table.add(new Predshest("var", "c", " "));
        predshest_table.add(new Predshest("var", ";", "<"));
        predshest_table.add(new Predshest("var", ":", "<"));
        predshest_table.add(new Predshest("var", "=", " "));
        predshest_table.add(new Predshest("var", "union", " "));
        predshest_table.add(new Predshest("var", "end", " "));
        predshest_table.add(new Predshest("var", "a", "<"));
        predshest_table.add(new Predshest("var", ",", "<"));

        predshest_table.add(new Predshest("t", "type", " "));
        predshest_table.add(new Predshest("t", "var", " "));
        predshest_table.add(new Predshest("t", "t", " "));
        predshest_table.add(new Predshest("t", "c", " "));
        predshest_table.add(new Predshest("t", ";", ">"));
        predshest_table.add(new Predshest("t", ":", " "));
        predshest_table.add(new Predshest("t", "=", "="));
        predshest_table.add(new Predshest("t", "union", " "));
        predshest_table.add(new Predshest("t", "end", " "));
        predshest_table.add(new Predshest("t", "a", " "));
        predshest_table.add(new Predshest("t", ",", " "));

        predshest_table.add(new Predshest("c", "type", " "));
        predshest_table.add(new Predshest("c", "var", " "));
        predshest_table.add(new Predshest("c", "t", " "));
        predshest_table.add(new Predshest("c", "c", " "));
        predshest_table.add(new Predshest("c", ";", ">"));
        predshest_table.add(new Predshest("c", ":", " "));
        predshest_table.add(new Predshest("c", "=", " "));
        predshest_table.add(new Predshest("c", "union", " "));
        predshest_table.add(new Predshest("c", "end", " "));
        predshest_table.add(new Predshest("c", "a", " "));
        predshest_table.add(new Predshest("c", ",", " "));

        predshest_table.add(new Predshest(";", "type", " "));
        predshest_table.add(new Predshest(";", "var", ">"));
        predshest_table.add(new Predshest(";", "t", "<"));
        predshest_table.add(new Predshest(";", "c", " "));
        predshest_table.add(new Predshest(";", ";", "<"));
        predshest_table.add(new Predshest(";", ":", "<"));
        predshest_table.add(new Predshest(";", "=", " "));
        predshest_table.add(new Predshest(";", "union", " "));
        predshest_table.add(new Predshest(";", "end", ">"));
        predshest_table.add(new Predshest(";", "a", "<"));
        predshest_table.add(new Predshest(";", ",", "<"));

        predshest_table.add(new Predshest(":", "type", " "));
        predshest_table.add(new Predshest(":", "var", " "));
        predshest_table.add(new Predshest(":", "t", "="));
        predshest_table.add(new Predshest(":", "c", "="));
        predshest_table.add(new Predshest(":", ";", ">"));
        predshest_table.add(new Predshest(":", ":", " "));
        predshest_table.add(new Predshest(":", "=", " "));
        predshest_table.add(new Predshest(":", "union", "<"));
        predshest_table.add(new Predshest(":", "end", " "));
        predshest_table.add(new Predshest(":", "a", " "));
        predshest_table.add(new Predshest(":", ",", " "));

        predshest_table.add(new Predshest("=", "type", " "));
        predshest_table.add(new Predshest("=", "var", " "));
        predshest_table.add(new Predshest("=", "t", " "));
        predshest_table.add(new Predshest("=", "c", "="));
        predshest_table.add(new Predshest("=", ";", ">"));
        predshest_table.add(new Predshest("=", ":", " "));
        predshest_table.add(new Predshest("=", "=", " "));
        predshest_table.add(new Predshest("=", "union", "<"));
        predshest_table.add(new Predshest("=", "end", " "));
        predshest_table.add(new Predshest("=", "a", " "));
        predshest_table.add(new Predshest("=", ",", " "));

        predshest_table.add(new Predshest("union", "type", " "));
        predshest_table.add(new Predshest("union", "var", " "));
        predshest_table.add(new Predshest("union", "t", " "));
        predshest_table.add(new Predshest("union", "c", " "));
        predshest_table.add(new Predshest("union", ";", "<"));
        predshest_table.add(new Predshest("union", ":", "<"));
        predshest_table.add(new Predshest("union", "=", " "));
        predshest_table.add(new Predshest("union", "union", " "));
        predshest_table.add(new Predshest("union", "end", "="));
        predshest_table.add(new Predshest("union", "a", "<"));
        predshest_table.add(new Predshest("union", ",", "<"));

        predshest_table.add(new Predshest("end", "type", " "));
        predshest_table.add(new Predshest("end", "var", " "));
        predshest_table.add(new Predshest("end", "t", " "));
        predshest_table.add(new Predshest("end", "c", " "));
        predshest_table.add(new Predshest("end", ";", ">"));
        predshest_table.add(new Predshest("end", ":", " "));
        predshest_table.add(new Predshest("end", "=", " "));
        predshest_table.add(new Predshest("end", "union", " "));
        predshest_table.add(new Predshest("end", "end", " "));
        predshest_table.add(new Predshest("end", "a", " "));
        predshest_table.add(new Predshest("end", ",", " "));

        predshest_table.add(new Predshest("a", "type", " "));
        predshest_table.add(new Predshest("a", "var", " "));
        predshest_table.add(new Predshest("a", "t", " "));
        predshest_table.add(new Predshest("a", "c", " "));
        predshest_table.add(new Predshest("a", ";", " "));
        predshest_table.add(new Predshest("a", ":", ">"));
        predshest_table.add(new Predshest("a", "=", " "));
        predshest_table.add(new Predshest("a", "union", " "));
        predshest_table.add(new Predshest("a", "end", " "));
        predshest_table.add(new Predshest("a", "a", " "));
        predshest_table.add(new Predshest("a", ",", ">"));

        predshest_table.add(new Predshest(",", "type", " "));
        predshest_table.add(new Predshest(",", "var", " "));
        predshest_table.add(new Predshest(",", "t", " "));
        predshest_table.add(new Predshest(",", "c", " "));
        predshest_table.add(new Predshest(",", ";", " "));
        predshest_table.add(new Predshest(",", ":", " "));
        predshest_table.add(new Predshest(",", "=", " "));
        predshest_table.add(new Predshest(",", "union", " "));
        predshest_table.add(new Predshest(",", "end", " "));
        predshest_table.add(new Predshest(",", "a", "="));
        predshest_table.add(new Predshest(",", ",", " "));

    }

}
