package scripteditor;

import console.Database;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import logic.ExecuteCommand;
import logic.Parser;
import objects.Method;
import utils.PrimusUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Analyzes and executes Primus scripts.
 */
public class ScriptProcessor {

    public static TextArea in;
    public static TextArea out;
    public static ProgressBar progressBar;

    private String code;
    private String[] lines;
    private int[] tabs;

    private Method[] methods;

    public ScriptProcessor(String code) {
        this.code = code;
        init();
    }

    /**
     * Initializes important attributes before running the srcipt.
     */
    private void init() {
        // Split code by line-by-line.
        this.lines = code.split("\n");

        // Keep track of tabs used in each line.
        this.tabs = new int[lines.length];

        int i = 0;
        for (String line : lines) {
            tabs[i] = line.length() - line.replace("\t", "").length();
            i++;
        }

        // Pre-parse all methods in this script.
        parseMethods();
    }

    /**
     * Finds all methods in this script. Used as a pre-pass before running script.
     * Stores methods in Database singleton.
     */
    private void parseMethods() {
        int methodNum = 0;
        ArrayList<Integer> methodLines = new ArrayList<>();

        for (int i = 0; i < lines.length; i++) {
            if (lines[i].startsWith("method ")) {
                methodNum++;
                methodLines.add(i);
            }
        }

        methods = new Method[methodNum];

        for (int i = 0; i < methodLines.size(); i++) {
            int currentLineNum = methodLines.get(i);
            String methodLine = lines[currentLineNum];
            String[] methodLineSplit = methodLine.split("\\s+");

            String methodId = PrimusUtils.getFunctionId(methodLineSplit[2]);
            Class methodType = PrimusUtils.getClassFromType(methodLineSplit[1]);
            String[] args = PrimusUtils.getFunctionArgs2(methodLine);
            System.out.println("input to method:" + Arrays.toString(args));
            StringBuilder methodCode = new StringBuilder();
            int targetTab = tabs[currentLineNum];
            for (int j = currentLineNum + 1; j < lines.length; j++) {
                if (tabs[j] > targetTab) {
                    String remove = removeIndents(lines[j], 1);
                    methodCode.append(remove);
                    methodCode.append("\n");
                } else {
                    break;
                }
            }

            methods[i] = new Method(
                    methodId,
                    methodType,
                    args,
                    methodCode.toString());

            Database.getDatabase().defineMethod(methodId, methodType, args, methodCode.toString());
            Database.getDatabase().getDefs().add(methods[i]);
            System.out.println(methods[i].toString());
        }
    }

    /**
     * Executes this script.
     */
    public String runScript() {
        for (Method method : methods) {
            if (method.getId().equals("main")) {
                method.runMethod(new String[]{""});
            }
        }
        int lineNum = 0;
        int maxTab = 0;
        String scriptReturn = "";
        String lineReturn;
        for (String line : lines) {
            line = line.trim();

            if (maxTab >= tabs[lineNum]) {
                if (line.startsWith("return")) {
                    scriptReturn = Parser.eval(PrimusUtils.afterFirstSpace(line)).toPlainString();
                } else if (line.startsWith("method")) {

                } else if (line.startsWith("if")) {
                    lineReturn = ExecuteCommand.send(line, false);
                    if (lineReturn.equals("true")) {
                        maxTab++;
                    } else {
                        maxTab = maxTab > 0 ? --maxTab : 0;
                    }
                } else if (line.startsWith("for")) {
                    //for defVar x = 0:condition eval(x<10):operation (x+1)
                    StringBuilder forCode = new StringBuilder();

                    String[] forLoop = line.split(":");
                    forLoop[0] = forLoop[0].substring("for ".length(), forLoop[0].length());
                    ExecuteCommand.send(forLoop[0], true);

                    String varId = forLoop[0].substring("defVar ".length(), forLoop[0].indexOf("="));

                    int forLoopIndent = tabs[lineNum];

                    for (int i = lineNum + 1; i < tabs.length; i++) {
                        if (tabs[i] > tabs[lineNum]) {
                            String remove = removeIndents(lines[i], forLoopIndent + 1);
                            forCode.append(remove);
                            forCode.append("\n");
                        } else {
                            break;
                        }
                    }
                    System.out.println("for code:");
                    System.out.println(forCode.toString());
                    ScriptProcessor forScript = new ScriptProcessor(forCode.toString());
                    while (ExecuteCommand.send("if " + forLoop[1], false).equals("true")) {
                        forScript.runScript();
                        ExecuteCommand.send("defVar " + varId + " = " + forLoop[2], true);
                    }

                    maxTab = tabs[lineNum];
                } else {
                    maxTab = tabs[lineNum];
                    lineReturn = ExecuteCommand.send(line, false);
                    if (!PrimusUtils.isSuppressed(lineReturn) && !PrimusUtils.isBlank(lineReturn)) {
                        String finalRet = lineReturn;
                        Platform.runLater(() -> out.appendText(finalRet + "\n"));
                    }
                }
            }

            lineNum++;
        }

        return scriptReturn;
    }

    /**
     * Removes a specific number of indents from the beginning of a String.
     *
     * @param s      - String to modify.
     * @param remove - Number of indents to remove.
     * @return String s without "remove" number of indents.
     */
    private static String removeIndents(String s, int remove) {
        int tabs = s.length() - s.replace("\t", "").length();
        while (tabs > 0 && remove > 0) {
            s = s.replaceFirst("\t", "");
            tabs--;
            remove--;
        }
        return s;
    }
}
