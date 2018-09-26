package logic;

import console.Database;
import utils.PrimusUtils;

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

        // Process input:
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
                    ret = "Command not recognized: " + input;
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
}
