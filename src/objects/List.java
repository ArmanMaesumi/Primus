package objects;

import logic.Parser;
import utils.PrimusUtils;

import java.math.BigDecimal;
import java.util.ArrayList;

public class List extends PrimusObject {

    private ArrayList<BigDecimal> list;

    public List(String id, String value) {
        super(id, value);
        updateList(value);
    }

    private void updateList(String s) {
        String[] elements = PrimusUtils.getFunctionArgs2(s);
        for (String element : elements) {
            list.add(Parser.eval(element));
        }
    }

    public BigDecimal getElement(int i) {
        if (i < 0 || i >= list.size())
            throw new IndexOutOfBoundsException("Index " + i + " out of bounds in list " + getId());

        return list.get(i);
    }

    public void setElement(int i, BigDecimal newValue) {
        if (i < 0 || i >= list.size())
            throw new IndexOutOfBoundsException("Index " + i + " out of bounds in list " + getId());

        list.set(i, newValue);
    }
}
