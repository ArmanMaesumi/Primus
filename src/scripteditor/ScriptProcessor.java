package scripteditor;

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

    public ScriptProcessor(TextArea in, TextArea out, ProgressBar progressBar){
        this.in = in;
        this.out = out;
        this.progressBar = progressBar;
        init();
    }

    private void init(){
        this.code = in.getText();
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
        for (String line : lines) {

            String ret = ExecuteCommand.send(line, false);
            if (!PrimusUtils.isSuppressed(ret) && !PrimusUtils.isBlank(ret)) {
                out.appendText(ret + "\n");
            }

            lineNum++;
        }
    }
}
