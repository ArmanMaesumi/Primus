package console;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import logic.ExecuteCommand;
import scripteditor.ScriptProcessor;
import utils.PrimusUtils;

import java.io.IOException;

public class Controller {
    @FXML
    public TextArea textArea;
    @FXML
    public TextField textField;
    @FXML
    public MenuItem menuScriptEditor;
    @FXML
    public Button Enter;
    @FXML
    public CheckMenuItem menuDegrees;

    public void enterButtonClicked() {
        boolean testing = false;
        if (testing) {
            send("defFunction f(a,b,c) = a");
            send("eval f(f(1,0,0),0,0)");
        } else {
            String command = textField.getText();
            if (!PrimusUtils.isBlank(command)) {
                String ret = ExecuteCommand.send(command, false);
                textArea.appendText(">" + textField.getText() + "\n");
                textField.setText("");
                if (!PrimusUtils.isBlank(ret)) {
                    textArea.appendText(ret + "\n");
                    //textArea.setStyle("-fx-control-inner-background:#000000; -fx-font-family: Consolas; -fx-highlight-fill: #00ff00; -fx-highlight-text-fill: #000000; -fx-text-fill: #00ff00;");
                }
            }
        }
    }


    public void send(String input){
        String ret = ExecuteCommand.send(input, false);
        textArea.appendText(">" + input + "\n");
        textField.setText("");
        if (!PrimusUtils.isBlank(ret))
            textArea.appendText(ret + "\n");
    }


    @FXML
    private void scriptEditorClicked() {
        ScreenController sc = ScreenController.getScreenController();
        try {
            sc.addScreen("ScriptEditor",
                    FXMLLoader.load(ScriptProcessor.class.getResource("ScriptEditor.fxml")), 645,700);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sc.activate("ScriptEditor");
    }

    @FXML
    private void setDegreeMode(){
        Settings.getSettings().setDegrees(menuDegrees.isSelected());
    }
}
