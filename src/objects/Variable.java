package objects;

import logic.Parser;

import java.math.BigDecimal;

/**
 * Primus Variable class. Stores a changeable arbitrary-precision value.
 */
public class Variable extends PrimusObject {

    private BigDecimal numericValue;

    /**
     * Constructs a Variable object.
     *
     * @param id    - PrimusObject id.
     * @param value - Variable value as a String.
     */
    public Variable(String id, String value) {
        super(id, value);
        this.numericValue = new BigDecimal(value);
    }

    /**
     * Constructs a Variable object.
     *
     * @param id    - PrimusObject id.
     * @param value - Variable value as a BigDecimal object.
     */
    public Variable(String id, BigDecimal value) {
        super(id, value.toPlainString());
        this.numericValue = new BigDecimal(value.toPlainString());
    }

    /**
     * Constructs a clone of another Variable.
     *
     * @param clone - Variable to clone from.
     */
    public Variable(Variable clone) {
        super(clone);
        this.numericValue = new BigDecimal(clone.getNumericValue());
    }

    /**
     * @return The String form of this Variable object.
     */
    public String getNumericValue() {
        return numericValue.toPlainString();
    }

    /**
     * Sets this Variable's value.
     *
     * @param value - A evaluable String expression.
     */
    public void setValue(String value) {
        this.numericValue = Parser.eval(value);
        super.setValue(numericValue.toPlainString());
    }

    /**
     * @return This Variable in String form. Example:
     * "x = 250.2"
     */
    public String toString() {
        return this.getId() + " = " + this.getNumericValue();
    }

}
