package console;

import logic.Parser;
import objects.Function;
import objects.PrimusObject;
import objects.TemporaryVariable;
import objects.Variable;

import java.math.BigDecimal;
import java.util.*;

/**
 * Singleton Database class for global access to user defined PrimusObjects.
 */
public class Database {

    // Singleton instance variables
    private static Database db;
    private boolean init;

    // ArrayList of user defined PrimusObjects and Primus constants
    private ArrayList<PrimusObject> defs = new ArrayList<>();
    private static final Map<String, BigDecimal> constants = new HashMap<String, BigDecimal>() {{
        put("pi", new BigDecimal("3.1415926535897932384626433832795028841971693993751058209749"));
        put("e", new BigDecimal("2.718281828459045235360287471352662497757247093699959574966"));
        put("true", new BigDecimal("1"));
        put("false", new BigDecimal("0"));
    }};

    // For global use of eval() Primus function
    private Function evalFunction;

    // Reserved Primus keywords
    private static final String[] RESERVED_KEYWORDS =
            {"sqrt", "sin", "cos", "tan", "ln", "log", "abs", "fact", "factorial", "isPrime", "eval",
                    "true", "false", "sigma"};

    // Singleton constructor
    private Database() {

    }

    /**
     * Creates an instance of this singleton Database class. If it already is instantiated,
     * then return Database object.
     *
     * @return this Database instance
     */
    public static Database getDatabase() {
        if (db == null)
            db = new Database();
        return db;
    }

    /**
     * @return ArrayList of user defined Primus Objects, and Primus constants.
     */
    public ArrayList<PrimusObject> getDefs() {
        return defs;
    }

    /**
     * Clears user defined Primus Objects, and retains Primus constants.
     */
    public void resetDatabase() {
        defs.clear();
        init = false;
        populateDefaultDefinitions();
    }

    /**
     * Populates Primus constants if they are not already initialized.
     */
    public void populateDefaultDefinitions() {
        if (init)
            return;

        for (Map.Entry<String, BigDecimal> entry : constants.entrySet()) {
            defineReservedTerm(entry.getKey(), entry.getValue().toPlainString());
        }
        evalFunction = new Function("eval", new String[1], "");
        defs.add(evalFunction);

        init = true;
    }

    /**
     * Global evaluation function.
     *
     * @param exp - expression to parse.
     * @return BigDecimal result.
     */
    public BigDecimal eval(String exp) {
        System.out.println("exp:" + exp);
        evalFunction.setValue(exp);
        return evalFunction.eval(new String[1]);
    }

    /**
     * @param id - id of target Primus Object.
     * @return PrimusObject with id, null if Primus Object is not defined.
     */
    public PrimusObject getPrimusObjectById(String id) {
        for (PrimusObject o : defs) {
            if (o.getId().equals(id))
                return o;
        }
        return null;
    }

    /**
     * @param id - id of target Primus Object.
     * @return PrimusObject.getValue() with id.
     */
    public String getValueOfObjectById(String id) {
        PrimusObject o = getPrimusObjectById(id);
        if (o != null)
            return o.getValue();
        throw new IllegalArgumentException("PrimusObject not found: " + id);
    }

    /**
     * @param id - id of target Primus Object.
     * @return if any Primus Object has id.
     */
    public boolean isPrimusObject(String id) {
        PrimusObject o = getPrimusObjectById(id);
        return o != null;
    }

    /**
     * Removes Primus Object from defs ArrayList.
     * preconditions: Primus Object with id is defined.
     *
     * @param id - id of target Primus Object.
     */
    public void removePrimusObjectById(String id) {
        PrimusObject o = getPrimusObjectById(id);
        if (o != null)
            defs.remove(o);
    }

    /**
     * @param id - id of target Primus Object.
     * @return index of Primus Object with id in defs ArrayList.
     */
    public int getIndexOfPrimusObjectById(String id) {
        for (int i = 0; i < defs.size(); i++) {
            if (defs.get(i).getId().equals(id))
                return i;
        }
        return -1;
    }

    /**
     * Removes all expired TemporaryVariables from defs ArrayList.
     */
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


    /**
     * @param s - potential reserved keyword.
     * @return if keyword is reserved by Primus.
     */
    public boolean isReserved(String s) {
        for (String keyword : RESERVED_KEYWORDS) {
            if (keyword.equals(s))
                return true;
        }
        return false;
    }

    /**
     * Allows Primus to created Primus Variables with keyword restricted names.
     *
     * @param id  - Variable id.
     * @param val - Variable expression.
     */
    private void defineReservedTerm(String id, String val) {
        if (val.trim().equals(""))
            throw new IllegalArgumentException("Invalid expression in variable " + id);
        // Compute new value before deleting old value.
        // Reason: defVar x = x + 1
        BigDecimal newVal = Parser.eval(val);
        removePrimusObjectById(id);
        defs.add(new Variable(id, newVal));
    }

    /**
     * Creates a Primus Variable.
     *
     * @param id  - Variable id.
     * @param val - Variable expression.
     */
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

    /**
     * Creates a Primus Function.
     *
     * @param id   - Function id.
     * @param args - Function arguments.
     * @param exp  - Function expression.
     */
    public void defineFunction(String id, String[] args, String exp) {
        if (isReserved(id))
            throw new IllegalArgumentException("Reserved Primus keyword cannot be used in definition: " + id);

        if (args.length == 1 && args[0].equals(""))
            throw new IllegalArgumentException("Too few arguments in function " + id);

        if (exp.trim().equals(""))
            throw new IllegalArgumentException("Invalid expression in function " + id);

        // Remove and replace if Function already exists.
        removePrimusObjectById(id);
        defs.add(new Function(id, args, exp));
    }

    /**
     * Creates a Primus Temporary Variable.
     *
     * @param id  - Temporary Variable id.
     * @param val - Temporary Variable expression.
     */
    public void defineTemporaryVariable(String id, String val) {
        if (isReserved(id))
            throw new IllegalArgumentException("Reserved Primus keyword cannot be used in definition: " + id);

        // Compute new value before deleting old value.
        // Reason: defVar x = x + 1
        BigDecimal newVal = Parser.eval(val);
        removePrimusObjectById(id);
        defs.add(new TemporaryVariable(id, newVal));
    }


    /**
     * Adds a collection of Primus Objects to defs ArrayList.
     *
     * @param coll - A collection of Primus Objects.
     */
    public void addAllPrimusObjects(ArrayList<PrimusObject> coll) {
        for (PrimusObject o : coll) {
            removePrimusObjectById(o.getId());
            defs.add(o);
        }
    }


    /**
     * Removes a collection of Primus Objects from defs ArrayList.
     *
     * @param coll - A collection of Primus Objects.
     */
    public void removeAllPrimusObjects(ArrayList<PrimusObject> coll) {
        for (PrimusObject o : coll) {
            removePrimusObjectById(o.getId());
        }
    }
}
