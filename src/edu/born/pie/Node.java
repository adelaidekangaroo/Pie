package edu.born.pie;

import java.util.ArrayList;
import java.util.List;

public class Node {

    List<Node> children = new ArrayList<>();

    public Node(String text) {
        this.text = text;
    }

    public void add(Node node) {
        children.add(node);
    }

    String text;

    public String getText() {
        return text;
    }

    public List<Node> getChildren() {
        return children;
    }

}
