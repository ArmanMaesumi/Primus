package objects;

import console.Database;
import logic.ExecuteCommand;
import scripteditor.ScriptProcessor;
import utils.PrimusUtils;

import java.util.ArrayList;

/**
 * PrimusObject Method class.
 * Used in the form test(defVar x, defFunction f)
 */
public class Method extends PrimusObject {

    // Method Arguments
    private String[] args;

    // Return type
    private Class type;

    /**
     * Constructs a Method object.
     *
     * @param id   - PrimusObject id.
     * @param args - Method arguments.
     * @param code - Method script.
     */
    public Method(String id, Class type, String args[], String code) {
        super(id, code);
//        if (!PrimusUtils.isPrimusObjectClass(type))
//            throw new IllegalArgumentException("Invalid method type: " + type.toString());

        this.type = type;
        this.args = new String[args.length];
        System.arraycopy(args, 0, this.args, 0, args.length);
    }

    /**
     * Constructs a clone of another Method.
     *
     * @param clone - Method to clone from.
     */
    public Method(Method clone) {
        super(clone);

        this.type = clone.type;
        this.args = new String[clone.args.length];
        System.arraycopy(clone.args, 0, this.args, 0, args.length);
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        System.arraycopy(args, 0, this.args, 0, args.length);
    }

    public Class getType() {
        return type;
    }

    /**
     * Parses the script contained in this method instance.
     *
     * @return The script in line-by-line form.
     */
    public String getScript() {
        StringBuilder sb = new StringBuilder();
        String[] script = getValue().split("\n");

        for (String line : script) {
            sb.append(line);
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Pushes this method's code to the ScriptProcessor
     */
    public void runMethod(String[] args) {
        if (args.length != this.args.length)
            throw new IllegalArgumentException("Argument mismatch in method: " + getId());

        // Create argument variables:
        for (int i = 0; i < args.length; i++) {
            String defStatement = this.args[i] + " = " + args[i];
            ExecuteCommand.send(defStatement, true);
        }

        // Run script in method:
        ScriptProcessor sc = new ScriptProcessor(this.getValue());
        sc.runScript();

        // Delete argument variables:
        for (String arg : this.args) {
            System.out.println("arg:" + arg);
            String id = PrimusUtils.getIdFromDefinitionStatement(arg + "=");
            Database.getDatabase().removePrimusObjectById(id);
        }
    }

    /**
     * Override toString method.
     *
     * @return The string form of this method instance.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.type.getName());
        sb.append(" ");
        sb.append(this.getId());
        sb.append("(");
        sb.append(args[0]);
        for (int i = 1; i < args.length; i++) {
            sb.append(", ");
            sb.append(args[i]);
        }
        sb.append(") = ");
        sb.append("\n");
        sb.append(this.getValue());

        return sb.toString();
    }
}
