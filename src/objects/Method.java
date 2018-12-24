package objects;

import console.Database;
import logic.ExecuteCommand;
import scripteditor.ScriptProcessor;
import utils.PrimusUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * PrimusObject Method class.
 * Used in the form test(defVar x, defFunction f)
 */
public class Method extends PrimusObject {

    // Method Arguments
    private String[] args;
    private Class[] argTypes;

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
        this.argTypes = new Class[args.length];
        this.args = new String[args.length];
        System.arraycopy(args, 0, this.args, 0, args.length);
        System.out.println(args.length);
        System.out.println(Arrays.toString(args));
        for (int i = 0; i < args.length; i++) {
            if (!PrimusUtils.isBlank(args[i])) {
                System.out.println(i + " , " + args[i]);
                Class argType = PrimusUtils.getClassFromDefinitionStatement(args[i]);
                argTypes[i] = argType;
            }
        }

        System.out.println("Arg types: " + Arrays.toString(argTypes));
    }

    /**
     * Constructs a clone of another Method.
     *
     * @param clone - Method to clone from.
     */
    public Method(Method clone) {
        this(clone.getId(),
                clone.getType(),
                clone.getArgs(),
                clone.getValue());
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
    public String runMethod(String[] args) {
        if (args.length != this.args.length)
            throw new IllegalArgumentException("Argument mismatch in method: " + getId());

        // If this method has arguments, then create argument variables:
        Database db = Database.getDatabase();

        if (args.length >= 1 && !args[0].trim().equals("")) {
            for (int i = 0; i < args.length; i++) {
                String funcVal = null;
                if (argTypes[i] == Function.class) {
                    PrimusObject func = db.getPrimusObjectById(args[i]);
                    if (func != null)
                        funcVal = func.getValue();
                }

                String defStatement;
                if (funcVal != null)
                    defStatement = this.args[i] + " = " + funcVal;
                else
                    defStatement = this.args[i] + " = " + args[i];

                ExecuteCommand.send(defStatement, true);
            }
        }

        // Run script in method:
        ScriptProcessor sc = new ScriptProcessor(this.getValue());
        String ret = sc.runScript();

        // Delete argument variables:
        for (String arg : this.args) {
            System.out.println("arg:" + arg);
            String id = PrimusUtils.getIdFromDefinitionStatement(arg + "=");
            Database.getDatabase().removePrimusObjectById(id);
        }

        return ret;
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
