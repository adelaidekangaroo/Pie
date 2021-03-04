package edu.born.pie;

public class Triad {

    private int index;
    private String operator;
    private String operand1;
    private String operand2;
    private COUNT_OPERANDS count_operands;

    public enum COUNT_OPERANDS {
        ONE,
        TWO
    }

    public Triad(String operator, String operand1, String operand2, COUNT_OPERANDS count_operands) {
        this.operator = operator;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.count_operands = count_operands;
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

    public COUNT_OPERANDS getCount_operands() {
        return count_operands;
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

    public void setCount_operands(COUNT_OPERANDS count_operands) {
        this.count_operands = count_operands;
    }

    @Override
    public String toString() {
        switch (count_operands) {
            case ONE:
                return String.format("%d: %s (%s)", index, operator, operand1);

            case TWO:
                return String.format("%d: %s (%s, %s)", index, operator, operand1, operand2);

            default:
                return "";
        }

    }
}
