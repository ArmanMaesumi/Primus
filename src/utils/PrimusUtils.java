package utils;

import objects.*;

import java.util.ArrayList;
import java.util.Arrays;

public class PrimusUtils {

    public static final String SET_SYNTAX = ":=";

    public static Class getClassFromType(String type) {
        if (type.equals("var"))
            return Variable.class;
        else if (type.equals("function"))
            return Function.class;
        else if (type.equals("void"))
            return PrimusObject.class;

        throw new IllegalArgumentException("Invalid method type: " + type);
    }

    public static String getIdFromDefinitionStatement(String s){
        if (!s.startsWith("def"))
            return s;
        return s.substring(s.indexOf(" "), s.indexOf("=")).trim();
    }

    public static boolean isPrimusObjectClass(Class c) {
        return PrimusObject.class.isAssignableFrom(c.getClass());
    }

    public static String getSetExpression(String s) {
        return s.trim().substring(s.indexOf(SET_SYNTAX) + SET_SYNTAX.length(), s.length());
    }

    public static String getSetId(String s) {
        return s.trim().substring(0, s.indexOf(SET_SYNTAX)).trim();
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

    public static String afterFirstSpace(String s){
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
        System.out.println("argArr:" + Arrays.toString(argArr));
        for (int i = 0; i < argArr.length; i++) {
            System.out.println(argArr[i]);
        }
        return argArr;
    }

    // input: func(a, b, c)
    public static String getFunctionId(String s) {
        if (!s.contains("("))
            return "";
        s = s.trim();
        System.out.println("xd"+s);
        return s.substring(0, s.indexOf("("));
    }

}
