package logic;

import console.Database;
import objects.Function;
import objects.PrimusObject;
import objects.Variable;
import utils.PrimusUtils;

import java.math.BigDecimal;
import java.util.Arrays;

public class ExecuteCommand {

    public static String send(String input, boolean suppress) {
        Database db = Database.getDatabase();
//        defineVariable("x", "2");
//        System.out.println(Database.getValueOfObjectById("x"));
//        System.out.println(Database.getPrimusObjectById("x"));
//        System.out.println(Database.isPrimusObject("x"));
//        System.out.println("---");
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
                    defineVariable(command[1], PrimusUtils.afterFirstEquals(input));
                    break;
                case "defFunc":
                case "defFunction":
                    String id = PrimusUtils.getFunctionId(command[1]);
                    String[] args = PrimusUtils.getFunctionArgs(input);
                    defineFunction(id, args, PrimusUtils.afterFirstEquals(input));
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
            ret = e.toString();
        }
        System.out.println("Return value: " + ret);
        System.out.println("Command arr: " + Arrays.toString(command));
        if (suppress)
            ret = "";
        return ret;
    }

    private static void defineVariable(String id, String val) {
        // Compute new value before deleting old value.
        // Reason: defVar x = x + 1
        BigDecimal newVal = Parser.eval(val);
        Database.getDatabase().removePrimusObjectById(id);
        Database.getDatabase().getDefs().add(new Variable(id, newVal));
    }

    private static void defineFunction(String id, String[] args, String exp) {
        Database.getDatabase().removePrimusObjectById(id);
        Database.getDatabase().getDefs().add(new Function(id, args, exp));
    }


}
