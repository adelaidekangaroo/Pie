package edu.born.pie.model;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private final List<Node> children = new ArrayList<>();
    private final String text;

    private Node(String text) {
        this.text = text;
    }

    public void addChildNode(Node childNode) {
        children.add(childNode);
    }

    public String getText() {
        return text;
    }

    public List<Node> getChildren() {
        return children;
    }

    public static Node of(String text) {
        return new Node(text);
    }
}
