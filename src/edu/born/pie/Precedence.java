package edu.born.pie;

public class Precedence {

    private final String left;
    private final String right;
    private final String result;

    private Precedence(String left, String right, String result) {
        this.left = left;
        this.right = right;
        this.result = result;
    }

    public String getLeft() {
        return left;
    }

    public String getRight() {
        return right;
    }

    public String getResult() {
        return result;
    }

    public static Precedence of(String left, String right, String result) {
        return new Precedence(left, right, result);
    }
}
