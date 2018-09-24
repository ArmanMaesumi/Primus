package logic;

import console.Database;
import utils.PrimusUtils;

public class ExecuteCommand {

    public static String send(String input, boolean suppress) {
        if (PrimusUtils.isBlank(input))
            return "";

        Database db = Database.getDatabase();
        String ret = "";
        if (PrimusUtils.isSuppressed(input)) {
            suppress = true;
            input = input.substring(1, input.length());
        }
        String[] command = input.split("\\s+");
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
