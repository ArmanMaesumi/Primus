package scripteditor;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import logic.ExecuteCommand;
import utils.PrimusUtils;

public class ScriptProcessor {

    public static void runScript(TextArea in, TextArea out, ProgressBar progressBar){
        new Thread(() -> {
            String code = in.getText();
            String[] lines = code.split("\n");
            int lineNum = 0;
            for (String line : lines){
                String ret = ExecuteCommand.send(line, false);
                if (!PrimusUtils.isSuppressed(ret) && !PrimusUtils.isBlank(ret)){
                    out.appendText(ret + "\n");
                }
                System.out.println("prog:"+((1.0 * lineNum) / (lines.length - 1)));
                progressBar.setProgress((1.0 * lineNum) / (lines.length - 1));
                lineNum++;
            }
        }).start();
    }

}
