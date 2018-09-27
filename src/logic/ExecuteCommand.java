package logic;

import console.Database;
import objects.Method;
import objects.PrimusObject;
import utils.PrimusUtils;

import java.util.Arrays;

/**
 * Class that handles input Primus input commands.
 */
public class ExecuteCommand {

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

        Database db = Database.getDatabase();
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
                case "solve":
                case "eval":
                    String exp = input.substring(input.indexOf(' '), input.length());
                    ret = Parser.eval(exp).toString();
                    break;
                case "defVar":
                case "defVariable":
                    db.defineVariable(command[1], PrimusUtils.afterFirstEquals(input));
                    break;
                case "defFunc":
                case "defFunction":
                    String id = PrimusUtils.getFunctionId(command[1]);
                    String[] args = PrimusUtils.getFunctionArgs(input);
                    db.defineFunction(id, args, PrimusUtils.afterFirstEquals(input));
                    break;
                case "if":
                    exp = input.substring(input.indexOf(' '), input.length());
                    ret = String.valueOf(BooleanParser.eval(exp));
                    break;
                case "defs":
                    ret = db.getDefs().toString();
                    break;
                default:
                    ret = additionalCommands(input);
                    //ret = "Command not recognized: " + input;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ret = "Error in line: " + input + "\n" +
                    "[ERROR]: " + e.getMessage();
        }
        System.out.println("Return value: " + ret);
        if (suppress && !PrimusUtils.isErrorMessage(ret))
            ret = "";
        return ret;
    }

    private static String additionalCommands(String input) {
        Database db = Database.getDatabase();
        String ret = "";

        try {
            // Set operation
            if (input.contains(":=")) {
                String id = PrimusUtils.getSetId(input);
                PrimusObject target = db.getPrimusObjectById(id);
                if (target != null) {
                    target.setValue(PrimusUtils.getSetExpression(input));
                    ret = target.toString();
                }
            } else {
                if (!runVoidMethod(input))
                    ret = "Command not recognized: " + input;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ret = "Error in line: " + input + "\n" +
                    "[ERROR]: " + e.getMessage();
        }

        return ret;
    }

    private static boolean runVoidMethod(String input) {
        boolean ran = false;
        System.out.println("input:"+input);
        Database db = Database.getDatabase();
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
