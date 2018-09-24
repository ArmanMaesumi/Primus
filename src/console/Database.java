package console;

import com.sun.javaws.exceptions.InvalidArgumentException;
import logic.Parser;
import objects.Function;
import objects.PrimusObject;
import objects.TemporaryVariable;
import objects.Variable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Database {

    private static Database db;
    private ArrayList<PrimusObject> defs = new ArrayList<>();

    private boolean init;
    private Function evalFunction;

    private static final Map<String, BigDecimal> constants = new HashMap<String, BigDecimal>() {{
        put("pi", new BigDecimal("3.1415926535897932384626433832795028841971693993751058209749"));
        put("e", new BigDecimal("2.718281828459045235360287471352662497757247093699959574966"));
    }};

    private static final String[] RESERVED_KEYWORDS =
            {"sqrt", "sin", "cos", "tan", "ln", "log", "abs", "fact", "factorial", "isPrime", "eval"};

    private Database() {

    }

    public static Database getDatabase() {
        if (db == null)
            db = new Database();
        return db;
    }


    public ArrayList<PrimusObject> getDefs() {
        return defs;
    }


    public void resetDatabase() {
        defs.clear();
        init = false;
        populateDefaultDefinitions();
    }

    public void populateDefaultDefinitions() {
        if (init)
            return;

        for (Map.Entry<String, BigDecimal> entry : constants.entrySet()) {
            defineVariable(entry.getKey(), entry.getValue().toPlainString());
        }
        evalFunction = new Function("eval", new String[1], "");
        defs.add(evalFunction);

        init = true;
    }

    public BigDecimal eval(String exp){
        System.out.println("exp:"+exp);
        evalFunction.setValue(exp);
        return evalFunction.eval(new String[1]);
    }

    public PrimusObject getPrimusObjectById(String id) {
        for (PrimusObject o : defs) {
            if (o.getId().equals(id))
                return o;
        }
        return null;
    }


    public String getValueOfObjectById(String id) {
        PrimusObject o = getPrimusObjectById(id);
        if (o != null)
            return o.getValue();
        throw new IllegalArgumentException("PrimusObject not found: " + id);
    }


    public boolean isPrimusObject(String id) {
        PrimusObject o = getPrimusObjectById(id);
        return o != null;
    }


    public void removePrimusObjectById(String id) {
        PrimusObject o = getPrimusObjectById(id);
        if (o != null)
            defs.remove(o);
    }


    public int getIndexOfPrimusObjectById(String id) {
        for (int i = 0; i < defs.size(); i++) {
            if (defs.get(i).getId().equals(id))
                return i;
        }
        return -1;
    }


    public void removeExpiredTemporaryVariables() {
        Iterator<PrimusObject> i = defs.iterator();
        while (i.hasNext()) {
            PrimusObject o = i.next();
            if (o.getClass() == TemporaryVariable.class) {
                if (((TemporaryVariable) o).isExpired())
                    defs.remove(o);
            }
        }
    }


    public boolean isReserved(String s) {
        for (String keyword : RESERVED_KEYWORDS) {
            if (keyword.equals(s))
                return true;
        }
        return false;
    }


    public void defineVariable(String id, String val) {
        if (isReserved(id))
            throw new IllegalArgumentException("Reserved Primus keyword cannot be used in definition: " + id);
        if (val.trim().equals(""))
            throw new IllegalArgumentException("Invalid expression in variable " + id);
        // Compute new value before deleting old value.
        // Reason: defVar x = x + 1
        BigDecimal newVal = Parser.eval(val);
        removePrimusObjectById(id);
        defs.add(new Variable(id, newVal));
    }


    public void defineFunction(String id, String[] args, String exp) {
        if (isReserved(id))
            throw new IllegalArgumentException("Reserved Primus keyword cannot be used in definition: " + id);

        if (args.length == 1 && args[0].equals(""))
            throw new IllegalArgumentException("Too few arguments in function " + id);

        if (exp.trim().equals(""))
            throw new IllegalArgumentException("Invalid expression in variable " + id);

        removePrimusObjectById(id);
        defs.add(new Function(id, args, exp));
    }


    public void defineTemporaryVariable(String id, String val) {
        if (isReserved(id))
            throw new IllegalArgumentException("Reserved Primus keyword cannot be used in definition: " + id);

        // Compute new value before deleting old value.
        // Reason: defVar x = x + 1
        BigDecimal newVal = Parser.eval(val);
        removePrimusObjectById(id);
        defs.add(new TemporaryVariable(id, newVal));
    }


    public void addAllPrimusObjects(ArrayList<PrimusObject> coll) {
        for (PrimusObject o : coll) {
            removePrimusObjectById(o.getId());
            defs.add(o);
        }
    }


    public void removeAllPrimusObjects(ArrayList<PrimusObject> coll) {
        for (PrimusObject o : coll) {
            removePrimusObjectById(o.getId());
        }
    }
}
