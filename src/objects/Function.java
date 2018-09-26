package objects;

import console.Database;
import logic.Parser;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * PrimusObject Function class.
 * Used in the form f(a,b,c,...) = a*b + c
 */
public class Function extends PrimusObject {

    // Function arguments
    private String[] args;

    /**
     * Constructs a Function object.
     *
     * @param id   - PrimusObject id.
     * @param args - Function arguments.
     * @param exp  - Expression.
     */
    public Function(String id, String[] args, String exp) {
        super(id, exp);
        this.args = new String[args.length];
        System.arraycopy(args, 0, this.args, 0, args.length);
    }

    /**
     * Constructs a clone of another Function.
     *
     * @param clone - Function to clone from.
     */
    public Function(Function clone) {
        super(clone);
        this.args = new String[clone.args.length];
        System.arraycopy(clone.args, 0, this.args, 0, args.length);
    }

    public String[] getArgs() {
        return this.args;
    }

    public void setArgs(String[] args) {
        System.arraycopy(args, 0, this.args, 0, args.length);
    }

    /**
     * Evaluates this function with a given set of input arguments.
     * preconditions: inputArgs.length == args.length
     *
     * @param inputArgs - The input to this function.
     * @return The evaluation of this function for the given arguments.
     */
    public BigDecimal eval(String[] inputArgs) {
        if (args.length != inputArgs.length)
            throw new IllegalArgumentException("Argument length mismatch in function: " + toString());

        Database db = Database.getDatabase();
        BigDecimal res;

        // Use generic eval function if this function is eval function.
        if (getId().equals("eval")) {
            res = Parser.eval(getValue());
        } else {
            ArrayList<PrimusObject> temporaryVariables = new ArrayList<>();
            for (int i = 0; i < args.length; i++) {
                temporaryVariables.add(new TemporaryVariable(args[i], inputArgs[i]));
            }
            db.addAllPrimusObjects(temporaryVariables);
            res = Parser.eval(getValue());
            db.removeAllPrimusObjects(temporaryVariables);
        }

        return res;
    }

    /**
     * Override toString method.
     *
     * @return The string form of this function instance.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getId());
        sb.append("(");
        sb.append(args[0]);
        for (int i = 1; i < args.length; i++) {
            sb.append(", ");
            sb.append(args[i]);
        }
        sb.append(") = ");
        sb.append(this.getValue());
        return sb.toString();
    }
}
