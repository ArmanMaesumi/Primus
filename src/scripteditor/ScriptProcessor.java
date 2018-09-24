package scripteditor;

import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import logic.ExecuteCommand;
import utils.PrimusUtils;

public class ScriptProcessor {

    private TextArea in;
    private TextArea out;
    private ProgressBar progressBar;

    private String code;
    private String[] lines;
    private int[] tabs;

    public ScriptProcessor(TextArea in, TextArea out, ProgressBar progressBar) {
        this.in = in;
        this.out = out;
        this.progressBar = progressBar;
        this.code = in.getText();
        init();
    }

    public ScriptProcessor(TextArea out, ProgressBar progressBar, String code){
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
    }

    public void runScript() {
        int lineNum = 0;
        int maxTab = Integer.MAX_VALUE;
        String ret;
        for (String line : lines) {
            line = line.trim();

            if (maxTab >= tabs[lineNum]) {
                if (line.startsWith("if")) {
                    ret = ExecuteCommand.send(line, false);
                    if (ret.equals("true")) {
                        maxTab = Integer.MAX_VALUE;
                    } else {
                        maxTab = tabs[lineNum];
                    }
                } else if (line.startsWith("for")) {
                    //for defVar x = 0:condition eval(x<10):operation (x+1)
                    StringBuilder forCode = new StringBuilder();

                    String[] forLoop = line.split(":");
                    forLoop[0] = forLoop[0].substring("for ".length(), forLoop[0].length());
                    ExecuteCommand.send(forLoop[0], true);

                    String varId = forLoop[0].substring("defVar ".length(), forLoop[0].indexOf("="));

                    for (int i = lineNum+1; i < tabs.length; i++) {
                        if (tabs[i] > tabs[lineNum]){
                            forCode.append(lines[i]);
                            forCode.append("\n");
                        }else{
                            break;
                        }
                    }

                    ScriptProcessor forScript = new ScriptProcessor(out, progressBar, forCode.toString());
                    while (ExecuteCommand.send("if " + forLoop[1], false).equals("true")){
                        forScript.runScript();
                        ExecuteCommand.send("defVar " + varId + " = " + forLoop[2], true);
                    }

                    maxTab = tabs[lineNum];
                } else {
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
}
