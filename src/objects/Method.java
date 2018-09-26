package objects;

/**
 * PrimusObject Method class.
 * Used in the form test(defVar x, defFunction f)
 */
public class Method extends PrimusObject {

    // Method Arguments
    private PrimusObject[] args;

    /**
     * Constructs a Method object.
     *
     * @param id    - PrimusObject id.
     * @param args  - Method arguments.
     * @param value - Method script.
     */
    public Method(String id, PrimusObject[] args, String value) {
        super(id, value);
        this.args = new PrimusObject[args.length];
        System.arraycopy(args, 0, this.args, 0, args.length);
    }

    /**
     * Constructs a clone of another Method.
     *
     * @param clone - Method to clone from.
     */
    public Method(Method clone) {
        super(clone);
        this.args = new PrimusObject[clone.args.length];
        System.arraycopy(clone.args, 0, this.args, 0, args.length);
    }

    public PrimusObject[] getArgs() {
        return args;
    }

    public void setArgs(PrimusObject[] args) {
        System.arraycopy(args, 0, this.args, 0, args.length);
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

    public void runMethod(){

    }

    /**
     * Override toString method.
     *
     * @return The string form of this method instance.
     */
    public String toString(){
        StringBuilder sb = new StringBuilder();
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
