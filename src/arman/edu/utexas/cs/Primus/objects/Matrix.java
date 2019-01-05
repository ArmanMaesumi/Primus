package arman.edu.utexas.cs.Primus.objects;

import arman.edu.utexas.cs.Primus.logic.Parser;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

public class Matrix extends PrimusObject {

    private int[] dimensions;

    private Object con;

    public Matrix(String id, int[] dims) {
        super(id, "");
        dimensions = Arrays.copyOf(dims, dims.length);
        con = Array.newInstance(BigDecimal.class, dims);
        initValues();
    }

    private void initValues() {
        int[] currCoord;
        int product = 1;
        for (int dimension : dimensions) {
            product *= dimension;
        }
        for (int i = 0; i < product; i++) {
            currCoord = coordinateOfNthItem(i);
            setElement(currCoord, new BigDecimal(BigInteger.ZERO));
        }
    }


    public int[] coordinateOfNthItem(int n) {
        int[] coord = new int[dimensions.length];
        int nn = 1 + n;
        int k = dimensions.length - 1;
        int product = 1;
        for (int dimension : dimensions) {
            product *= dimension;
        }

        int q = 0;
        int r = 0;
        for (int i = 0; i < k; i++) {
            product /= dimensions[i];
            q = nn / product;
            r = nn % product;
            if (r == 0) {
                q--;
                r = product;
            }
            coord[i] = q;
            nn = r;
        }

        coord[k] = nn - 1;
        return coord;
    }

    public BigDecimal getElement(int[] indices) {
        int depth = indices.length;
        Object[] o = (Object[]) con;
        for (int i = 0; i < indices.length - 1; i++) {
            int currIndex = indices[i];
            o = (Object[]) o[currIndex];
        }

        return (BigDecimal) o[indices[indices.length - 1]];
    }

    public void setElement(int[] indices, BigDecimal newValue) {
        Object[] o = (Object[]) con;
        for (int i = 0; i < indices.length - 1; i++) {
            int currIndex = indices[i];
            o = (Object[]) o[currIndex];
        }

        o[indices[indices.length - 1]] = newValue;
    }

    public void setElement(int[] indices, String newValue) {
        Object[] o = (Object[]) con;
        for (int i = 0; i < indices.length - 1; i++) {
            int currIndex = indices[i];
            o = (Object[]) o[currIndex];
        }

        o[indices[indices.length - 1]] = Parser.eval(newValue);
    }

    public static int[] evalCoordinates(String[] expressions) {
        int[] dims = new int[expressions.length];

        for (int i = 0; i < expressions.length; i++) {
            dims[i] = Parser.eval(expressions[i]).intValue();
        }

        return dims;
    }
}
