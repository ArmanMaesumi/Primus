package objects;

import java.math.BigDecimal;

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

    public void setNumericValue(String value) {
        this.numericValue = new BigDecimal(value);
        this.setValue(value);
    }

    public String toString() {
        return this.getId() + " = " + this.getNumericValue();
    }

}
