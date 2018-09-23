package objects;

import console.Database;
import logic.Parser;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Function extends PrimusObject {

    private String[] args;

    public Function(String id, String[] args, String exp) {
        super(id, exp);
        this.args = args;
    }

    public Function(Function clone){
        super(clone);

        String[] args = new String[clone.args.length];
        for (int i = 0; i < args.length; i++) {
            args[i] = clone.args[i];
        }
        this.args = args;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public BigDecimal eval(String[] inputArgs) {
        Database db = Database.getDatabase();
        if (args.length != inputArgs.length)
            throw new IllegalArgumentException("Argument length mismatch in function: " + toString());

        ArrayList<PrimusObject> temporaryVariables = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            temporaryVariables.add(new TemporaryVariable(args[i], inputArgs[i]));
            //db.defineTemporaryVariable(args[i], inputArgs[i]);
        }
        db.addAllPrimusObjects(temporaryVariables);
        BigDecimal res = Parser.eval(getValue());
        db.removeAllPrimusObjects(temporaryVariables);

        return res;
    }

    public String toString() {
        return this.getId() + " = " + this.getValue();
    }
}
