package edu.born.pie.utils;

import edu.born.pie.syn.Node;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class UIUtil {

    private UIUtil() {
    }

    public static void traceRootNode(Node rootNode) {
        DefaultMutableTreeNode model = new DefaultMutableTreeNode();
        JTree tree = new JTree(model);
        JFrame frame = new JFrame();
        frame.add(tree);
        frame.setBounds(300, 100, 400, 600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.show();

        DefaultMutableTreeNode node = new DefaultMutableTreeNode("");
        model.add(node);
        buildTreeNode(node, rootNode);

        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    private static void buildTreeNode(DefaultMutableTreeNode root, Node node) {
        node.getChildren().forEach(someNode -> {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(someNode.getText());
            root.add(child);
            buildTreeNode(child, someNode);
        });
    }
}
