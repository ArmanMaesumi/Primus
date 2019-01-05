package arman.edu.utexas.cs.Primus.logic;

import arman.edu.utexas.cs.Primus.console.Database;
import arman.edu.utexas.cs.Primus.objects.*;
import arman.edu.utexas.cs.Primus.utils.PrimusUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that handles input Primus input commands.
 */
public class ExecuteCommand {

    public static Database db = Database.getDatabase();
    /**
     * Send command to Primus.
     *
     * @param input    - User input.
     * @param suppress - Whether or not to suppress the output of command.
     * @return The value of the processed input.
     */
    public static String send(String input, boolean suppress) {
        // If input is effectively blank, return nothing
        if (PrimusUtils.isBlank(input))
            return "";

        input = input.trim();
        String ret = "";

        if (PrimusUtils.isSuppressed(input)) {
            suppress = true;
            input = input.substring(1, input.length());
        }

        // Split input by spaces
        String[] command = input.split("\\s+");

        // Process command:
        try {
            switch (command[0]) {
                case "import":
                    if (!command[1].endsWith(".prm"))
                        command[1] += ".prm";

                    String appPath = System.getProperty("user.dir");
                    File primusDir = new File(appPath).getParentFile();
                    File packageDir = new File(primusDir.getAbsoluteFile() + "\\packages\\" + command[1]);
                    db.getPackages().add(packageDir);
                    System.out.println(packageDir);
                    System.out.println(db.getPackages());
                    break;
                case "solve":
                case "eval":
                    String exp = input.substring(input.indexOf(' '), input.length());
                    ret = Parser.eval(exp).toString();
                    break;
                case "var":
                case "variable":
                    db.defineVariable(command[1], PrimusUtils.afterFirstEquals(input));
                    break;
                case "func":
                case "function":
                    String id = PrimusUtils.getFunctionId(command[1]);
                    String[] args = PrimusUtils.getFunctionArgs(input);
                    db.defineFunction(id, args, PrimusUtils.afterFirstEquals(input));
                    break;
                case "matrix":
                    id = PrimusUtils.getFunctionId(command[1]);
                    args = PrimusUtils.getFunctionArgs2(input);

                    int[] dims = new int[args.length];
                    for (int i = 0; i < args.length; i++) {
                        BigDecimal arg = Parser.eval(args[i]);
                        dims[i] = arg.intValue();
                    }

                    db.defineMatrix(id, dims);
                    break;
                case "if":
                    exp = input.substring(input.indexOf(' '), input.length());
                    ret = String.valueOf(BooleanParser.eval(exp));
                    break;
                case "defs":
                    ret = db.defsAsString();
                    break;
                case "print":
                    String arg = PrimusUtils.afterFirstSpace(input);
                    ret = print(arg);
                    break;
                default:
                    ret = additionalCommands(input);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ret = "Error in line: " + input + "\n" +
                    "[ERROR]: " + e.toString();
            return ret;
        }
        System.out.println("Return value: " + ret);
        if (suppress && !PrimusUtils.isErrorMessage(ret))
            ret = "";
        return ret;
    }

    private static String print(String arg) {
        Pattern pattern = Pattern.compile("\"([^\"]*)\"");
        Matcher quoteMatcher = pattern.matcher(arg);

        List<String> quotedText = new ArrayList<>();
        List<String> unquotedText = new ArrayList<>();
        String unquotedArgument = arg;

        while (quoteMatcher.find()) {
            String match = quoteMatcher.group(1);
            quotedText.add(match);
            unquotedArgument = unquotedArgument.replace("\"" + match + "\"", "");
        }

        System.out.println(quotedText.toString());
        System.out.println(unquotedArgument);

        return "";
    }

    private static String additionalCommands(String input) {
        String ret = "";

        try {
            // Set operation
            if (input.contains(PrimusUtils.SET_SYNTAX)) {
                ret = setCommand(input);
            } else {
                if (!runVoidMethod(input))
                    ret = "Command not recognized: " + input;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ret = "Error in line: " + input + "\n" +
                    "[ERROR]: " + e.toString();
        }

        return ret;
    }

    private static String setCommand(String input) {
        String id = PrimusUtils.getSetId(input);
        System.out.println("id:" + id);
        PrimusObject target = db.getPrimusObjectById(id);
        if (target == null)
            throw new IllegalStateException("Undefined Primus object: " + id);

        if (target.getClass() == Variable.class) {
            target.setValue(PrimusUtils.getSetExpression(input));
        } else if (target.getClass() == Function.class) {
            db.defineFunction(
                    id,
                    PrimusUtils.getFunctionArgs(input),
                    PrimusUtils.getSetExpression(input));
        } else if (target.getClass() == Matrix.class) {
            String leftSide = PrimusUtils.getSetLeftSide(input);
            int[] coord = Matrix.evalCoordinates(PrimusUtils.getFunctionArgs2(leftSide));
            ((Matrix) target).setElement(coord, PrimusUtils.getSetExpression(input));
        }

        return target.toString();
    }

    private static boolean runVoidMethod(String input) {
        boolean ran = false;
        System.out.println("inputvoidmethod:" + input);
        String methodId = PrimusUtils.getFunctionId(input);
        PrimusObject method = db.getPrimusObjectById(methodId);

        if (method != null) {
            if (method instanceof Method) {
                String args[] = PrimusUtils.getFunctionArgs2(input);
                System.out.println("Method args:" + Arrays.toString(args));
                ((Method) method).runMethod(args);
                ran = true;
            }
        }

        return ran;
    }
}
