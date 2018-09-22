package console;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import logic.ExecuteCommand;
import utils.PrimusUtils;

import java.io.IOException;
import java.util.Arrays;

public class Controller {
    @FXML
    public TextArea textArea;
    @FXML
    public TextField textField;
    @FXML
    public MenuItem menuScriptEditor;
    public Button Enter;

    public void enterButtonClicked() {
        boolean testing = false;
        if (testing) {
            //System.out.println(Arrays.toString(PrimusUtils.getFunctionArgs2("f(1,2,3,4)")));
            //System.out.println(Arrays.toString(PrimusUtils.getFunctionArgs2("f(f(1,2,3),4,5)")));
            //System.out.println(Arrays.toString(PrimusUtils.getFunctionArgs2("f(f(f(2),2,3),g(1,2),5)")));
            send("defFunction f(a,b,c) = a");
            send("eval f(f(1,0,0),0,0)");
        } else {
            String command = textField.getText();
            if (!PrimusUtils.isBlank(command)) {
                String ret = ExecuteCommand.send(command, false);
                textArea.appendText(">" + textField.getText() + "\n");
                textField.setText("");
                if (!PrimusUtils.isBlank(ret))
                    textArea.appendText(ret + "\n");
            }
        }
    }


    private void send(String input){
        String ret =  ExecuteCommand.send(input, false);
        textArea.appendText(">" + input + "\n");
        textField.setText("");
        if (!PrimusUtils.isBlank(ret))
            textArea.appendText(ret + "\n");
    }


    @FXML
    private void scriptEditorClicked() throws IOException {
        Stage stage = (Stage) textArea.getScene().getWindow();
        stage.close();
        System.out.println("aaaaaaaa");
        Parent root = FXMLLoader.load(Controller.class.getResource("../scripteditor/ScriptEditor.fxml"));
        stage = new Stage();
        stage.setScene(new Scene(root, 645, 700));
        stage.setTitle("Primus - Script Editor");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/Logo2.png")));
        stage.show();
    }
}
