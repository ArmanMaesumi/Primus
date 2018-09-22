package utils;

import java.util.ArrayList;
import java.util.Arrays;

public class PrimusUtils {

    public static boolean isBlank(String s) {
        return (s.trim()).isEmpty();
    }


    public static String afterFirstEquals(String s) {
        return s.substring(s.indexOf("=") + 1, s.length());
    }


    public static boolean isSuppressed(String s) {
        return s.startsWith("@");
    }


    public static int numberOfArgs(String s) {
        s = s.substring(s.indexOf("(") + 1, s.indexOf(")"));
        return (s.split(",").length);
    }


    public static String[] getFunctionArgs(String s){
        s = s.substring(s.indexOf("(") + 1, s.indexOf(")"));
        s = s.replaceAll("\\s+", "");
        System.out.println("Args:" + s);
        System.out.println(Arrays.toString(s.split(",")));
        return s.split(",");
    }


    public static String[] getFunctionArgs2(String s){
        System.out.println(s);

        ArrayList<String> argList = new ArrayList<>();

        int pos = s.indexOf("(") + 1;
        int startPos = pos;
        int parentheses = 0;
        char ch;

        while (pos < s.length()){
            ch = s.charAt(pos);
            System.out.println(ch);
            if (ch == '(')
                parentheses++;
            else if (ch == ')')
                parentheses--;

            if ((ch == ',' && parentheses < 1) || (ch == ')' && parentheses < 0)){
                //System.out.println(s.substring(startPos, pos));
                argList.add(s.substring(startPos, pos));
                startPos = pos + 1;
            }
            pos++;
        }

        String[] argArr = new String[argList.size()];
        argArr = argList.toArray(argArr);
        System.out.println("argArr:"+Arrays.toString(argArr));
        for (int i = 0; i < argArr.length; i++) {
            System.out.println(argArr[i]);
        }
        return argArr;
    }


    // input: func(a, b, c)
    public static String getFunctionId(String s){
        s = s.trim();
        return s.substring(0, s.indexOf("("));
    }

}
