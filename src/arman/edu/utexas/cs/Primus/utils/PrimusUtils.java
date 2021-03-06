package arman.edu.utexas.cs.Primus.utils;

import arman.edu.utexas.cs.Primus.objects.*;

import java.util.ArrayList;
import java.util.Arrays;

public class PrimusUtils {

    public static final String SET_SYNTAX = ":=";

    public static Class getClassFromDefinitionStatement(String def) {
        def = def.trim();
        if (def.startsWith("var"))
            return Variable.class;
        else if (def.startsWith("func"))
            return Function.class;

        throw new IllegalArgumentException("Invalid argument type: " + def);
    }

    public static Class getClassFromType(String type) {
        if (type.equals("var"))
            return Variable.class;
        else if (type.equals("function"))
            return Function.class;
        else if (type.equals("void"))
            return PrimusObject.class;

        throw new IllegalArgumentException("Invalid method type: " + type);
    }

    public static String getIdFromDefinitionStatement(String s) {
        return s.substring(s.indexOf(" "), s.indexOf("=")).trim();
    }

    public static boolean isPrimusObjectClass(Class c) {
        return PrimusObject.class.isAssignableFrom(c.getClass());
    }

    public static String getSetLeftSide(String s) {
        return s.trim().substring(0, s.indexOf(SET_SYNTAX));
    }

    public static String getSetExpression(String s) {
        return s.trim().substring(s.indexOf(SET_SYNTAX) + SET_SYNTAX.length(), s.length());
    }

    public static String getSetId(String s) {
        int pos = 0;
        String leftSide = s.trim().substring(0, s.indexOf(SET_SYNTAX)).trim();

        while (pos < leftSide.length()) {
            char c = leftSide.charAt(pos);
            if (!Character.isDigit(c) && !Character.isAlphabetic(c) && c != '_') {
                break;
            }
            pos++;
        }

        return leftSide.substring(0, pos);
    }

    public static boolean isErrorMessage(String s) {
        return s.startsWith("Error");
    }

    public static boolean isBlank(String s) {
        return (s.trim()).isEmpty();
    }

    public static String afterFirstEquals(String s) {
        return s.substring(s.indexOf("=") + 1, s.length());
    }

    public static String afterFirstSpace(String s) {
        return s.substring(s.indexOf(" ") + 1, s.length());
    }

    public static boolean isSuppressed(String s) {
        return s.startsWith("@");
    }

    public static int numberOfArgs(String s) {
        s = s.substring(s.indexOf("(") + 1, s.indexOf(")"));
        return (s.split(",").length);
    }

    public static String[] getFunctionArgs(String s) {
        s = s.substring(s.indexOf("(") + 1, s.indexOf(")"));
        s = s.replaceAll("\\s+", "");
        return s.split(",");
    }

    public static String[] getFunctionArgs2(String s) {
        ArrayList<String> argList = new ArrayList<>();

        int pos = s.indexOf("(") + 1;
        int startPos = pos;
        int parentheses = 0;
        char ch;

        while (pos < s.length()) {
            ch = s.charAt(pos);
            System.out.println(ch);
            if (ch == '(')
                parentheses++;
            else if (ch == ')')
                parentheses--;

            if ((ch == ',' && parentheses < 1) || (ch == ')' && parentheses < 0)) {
                argList.add(s.substring(startPos, pos));
                startPos = pos + 1;
            }
            pos++;
        }

        String[] argArr = new String[argList.size()];
        argArr = argList.toArray(argArr);
        System.out.println("Args: " + Arrays.toString(argArr));
        return argArr;
    }

    // input: func(a, b, c)
    public static String getFunctionId(String s) {
        if (!s.contains("("))
            return "";
        s = s.trim();
        System.out.println("xd" + s);
        return s.substring(0, s.indexOf("("));
    }

}
