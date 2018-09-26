package scripteditor;

import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import logic.ExecuteCommand;
import objects.Method;
import objects.PrimusObject;
import objects.Variable;
import utils.PrimusUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ScriptProcessor {

    private TextArea in;
    private TextArea out;
    private ProgressBar progressBar;

    private String code;
    private String[] lines;
    private int[] tabs;

    private Method[] methods;

    public ScriptProcessor(TextArea in, TextArea out, ProgressBar progressBar) {
        this.in = in;
        this.out = out;
        this.progressBar = progressBar;
        this.code = in.getText();
        init();
    }

    public ScriptProcessor(TextArea out, ProgressBar progressBar, String code) {
        this.out = out;
        this.progressBar = progressBar;
        this.code = code;
        init();
    }

    private void init() {
        this.lines = code.split("\n");
        this.tabs = new int[lines.length];

        int i = 0;
        for (String line : lines) {
            tabs[i] = line.length() - line.replace("\t", "").length();
            i++;
        }

        parseMethods();
    }

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

            String methodId = PrimusUtils.getFunctionId(methodLineSplit[1]);
            String[] args = PrimusUtils.getFunctionArgs(methodLine);

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
                    new PrimusObject[]{new Variable("x", "1")},
                    methodCode.toString());

            System.out.println(methods[i].toString());
        }
    }

    public void runScript() {
        int lineNum = 0;
        //int maxTab = Integer.MAX_VALUE;
        int maxTab = 0;
        String ret;
        for (String line : lines) {
            line = line.trim();

            if (maxTab >= tabs[lineNum]) {
                if (line.startsWith("method")){

                } else if (line.startsWith("if")) {
                    ret = ExecuteCommand.send(line, false);
                    if (ret.equals("true")) {
                        //maxTab = Integer.MAX_VALUE;
                        maxTab++;
                    } else {
                        //maxTab = tabs[lineNum];
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
                    ScriptProcessor forScript = new ScriptProcessor(out, progressBar, forCode.toString());
                    while (ExecuteCommand.send("if " + forLoop[1], false).equals("true")) {
                        forScript.runScript();
                        ExecuteCommand.send("defVar " + varId + " = " + forLoop[2], true);
                    }

                    maxTab = tabs[lineNum];
                } else {
                    maxTab = tabs[lineNum];
                    ret = ExecuteCommand.send(line, false);
                    if (!PrimusUtils.isSuppressed(ret) && !PrimusUtils.isBlank(ret)) {
                        String finalRet = ret;
                        Platform.runLater(() -> out.appendText(finalRet + "\n"));
                    }
                }
            }

            lineNum++;
        }
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
        while (tabs > 0 && remove > 0){
            s = s.replaceFirst("\t", "");
            tabs--;
            remove--;
        }
        return s;
    }
}
