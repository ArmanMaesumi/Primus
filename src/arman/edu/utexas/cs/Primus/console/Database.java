package arman.edu.utexas.cs.Primus.console;

import arman.edu.utexas.cs.Primus.logic.Parser;
import arman.edu.utexas.cs.Primus.objects.*;
import arman.edu.utexas.cs.Primus.objects.Matrix;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

/**
 * Singleton Database class for global access to user defined PrimusObjects, as well as predefined
 * Primus functions such as sin, cos, sqrt, log, etc.
 */
public class Database {

    // Singleton instance variables
    private static Database db;
    private boolean init;

    // Maps of user defined PrimusObjects and Primus constants
    private Map<String, PrimusObject> defs = new HashMap<>();
    private static final Map<String, BigDecimal> constants = new HashMap<String, BigDecimal>() {{
        put("pi", new BigDecimal("3.1415926535897932384626433832795028841971693993751058209749"));
        put("e", new BigDecimal("2.718281828459045235360287471352662497757247093699959574966"));
        put("true", new BigDecimal("1"));
        put("false", new BigDecimal("0"));
    }};

    // For global use of eval() Primus function
    private Function evalFunction;

    // Reserved Primus keywords
    private static final HashSet<String> RESERVED = new HashSet<String>() {{
        add("sqrt");
        add("sin");
        add("cos");
        add("tan");
        add("ln");
        add("log");
        add("abs");
        add("fact");
        add("factorial");
        add("isPrime");
        add("eval");
        add("true");
        add("false");
        add("sum");
    }};

    // Imported packages:
    private HashSet<File> packages = new HashSet<File>();

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
    public Map<String, PrimusObject> getDefs() {
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

        for (String constant : constants.keySet()) {
            BigDecimal bd = constants.get(constant);
            defineReservedVariable(constant, bd.toPlainString());
        }

        evalFunction = new Function("eval", new String[1], "");
        defs.put(evalFunction.getId(), evalFunction);

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
        return defs.get(id);
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
    public PrimusObject removePrimusObjectById(String id) {
        return defs.remove(id);
    }


    /**
     * Removes all expired TemporaryVariables from defs ArrayList.
     *
     * @return if any variables were removed
     */
    public boolean removeExpiredTemporaryVariables() {
        boolean ret = false;

        for (String objId : defs.keySet()) {
            PrimusObject o = defs.get(objId);
            if (o.getClass() == TemporaryVariable.class) {
                if (((TemporaryVariable) o).isExpired()) {
                    defs.remove(objId);
                    ret = true;
                }
            }
        }

        return ret;
    }


    /**
     * @param s - potential reserved keyword.
     * @return if keyword is reserved by Primus.
     */
    public boolean isReserved(String s) {
        return RESERVED.contains(s);
    }


    /**
     * Allows Primus to created Primus Variables with keyword restricted names.
     *
     * @param id  - Variable id.
     * @param val - Variable expression.
     */
    private void defineReservedVariable(String id, String val) {
        if (val.trim().equals(""))
            throw new IllegalArgumentException("Invalid expression in variable " + id);
        // Compute new value before deleting old value.
        // Reason: defVar x = x + 1
        BigDecimal newVal = Parser.eval(val);
        defs.put(id, new Variable(id, newVal));
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
        defs.put(id, new Variable(id, newVal));
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
        defs.put(id, new Function(id, args, exp));
    }


    /**
     * Creates a Primus Method.
     *
     * @param id   - Method id.
     * @param type - Method type (void, var, function).
     * @param args - Method arguments
     * @param code - Code contained by method.
     */
    public void defineMethod(String id, Class type, String[] args, String code) {
        removePrimusObjectById(id);
        defs.put(id, new Method(
                id,
                type,
                args,
                code));
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
        defs.put(id, new TemporaryVariable(id, newVal));
    }


    /**
     * Creates a Primus Matrix object.
     *
     * @param id   - Matrix id.
     * @param dims - Array of lengths of dimensions.
     */
    public void defineMatrix(String id, int[] dims) {
        if (isReserved(id))
            throw new IllegalArgumentException("Reserved Primus keyword cannot be used in definition: " + id);

        defs.put(id, new Matrix(id, dims));
    }


    /**
     * Adds a collection of Primus Objects to defs ArrayList.
     *
     * @param coll - A collection of Primus Objects.
     */
    public void addAllPrimusObjects(ArrayList<PrimusObject> coll) {
        for (PrimusObject o : coll) {
            removePrimusObjectById(o.getId());
            defs.put(o.getId(), o);
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


    /**
     * @return The set of imported packages.
     */
    public HashSet<File> getPackages() {
        return packages;
    }


    /**
     * @return A formatted string of user defined PrimusObjects.
     */
    public String defsAsString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        sb.append("[");
        for (String def : defs.keySet()) {
            if (!first) sb.append(", ");
            else first = false;

            PrimusObject obj = defs.get(def);
            sb.append(obj.toString());
        }
        sb.append("]");

        return sb.toString();
    }
}
