package objects;

import java.math.BigDecimal;

public class Variable extends PrimusObject{

    private BigDecimal val;

    public Variable(String id, String value) {
        super(id, value);
        this.val = new BigDecimal(value);
    }

    public Variable(String id, BigDecimal value){
        super(id, value.toPlainString());
        this.val = new BigDecimal(value.toPlainString());
    }

    public String getVal(){
        return val.toString();
    }

    public void setVal(String value){
        this.val = new BigDecimal(value);
        this.setValue(value);
    }

    public String toString(){
        return this.getId() + " = " + this.getVal();
    }

}
