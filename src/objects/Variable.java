package objects;

import logic.Parser;

import java.math.BigDecimal;

/**
 * Primus Variable class. Stores a changeable arbitrary-precision value.
 */
public class Variable extends PrimusObject {

    private BigDecimal numericValue;

    public Variable(String id, String value) {
        super(id, value);
        this.numericValue = new BigDecimal(value);
    }

    public Variable(String id, BigDecimal value) {
        super(id, value.toPlainString());
        this.numericValue = new BigDecimal(value.toPlainString());
    }

    public Variable(Variable clone){
        super(clone);
        this.numericValue = new BigDecimal(clone.getNumericValue());
    }

    public String getNumericValue() {
        return numericValue.toPlainString();
    }

    public void setValue(String value){
        this.numericValue = Parser.eval(value);
        super.setValue(numericValue.toPlainString());
    }

    public String toString() {
        return this.getId() + " = " + this.getNumericValue();
    }

}
