package edu.born.pie.generator;

public class Triad {

    private int index;
    private String operator;
    private String operand1;
    private String operand2;
    private final CountOperands countOperands;

    public enum CountOperands {ONE, TWO}

    private Triad(String operator, String operand1, String operand2, CountOperands countOperands) {
        this.operator = operator;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.countOperands = countOperands;
    }

    public static Triad of(String operator, String operand1, String operand2, CountOperands countOperands) {
        return new Triad(operator, operand1, operand2, countOperands);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getOperator() {
        return operator;
    }

    public String getOperand1() {
        return operand1;
    }

    public String getOperand2() {
        return operand2;
    }

    public CountOperands getCountOperands() {
        return countOperands;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setOperand1(String operand1) {
        this.operand1 = operand1;
    }

    public void setOperand2(String operand2) {
        this.operand2 = operand2;
    }

    @Override
    public String toString() {
        return switch (countOperands) {
            case ONE -> String.format("%d: %s (%s)", index, operator, operand1);
            case TWO -> String.format("%d: %s (%s, %s)", index, operator, operand1, operand2);
        };
    }
}
