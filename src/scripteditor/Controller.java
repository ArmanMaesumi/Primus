package scripteditor;

import console.Database;
import console.Main;
import console.ScreenController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;

/**
 * Created by Arman on 10/12/2015.
 */
public class Controller {

    public static String path;
    public static String code;

    @FXML
    public MenuItem newFile, openFile, closeFile, saveFile, saveAs, preferences, quit, run;
    @FXML
    public TextArea textArea;
    public TextArea outputTextArea;
    public ProgressBar progressBar;

    @FXML
    public void runButton(){
        run.setDisable(true);
        code = textArea.getText();
        outputTextArea.clear();
        progressBar.setProgress(0);
        Database.getDatabase().resetDatabase();
        ScriptProcessor.runScript(textArea, outputTextArea, progressBar);
        run.setDisable(false);
    }

    @FXML
    public void newFileButton(){
        textArea.setText("");
    }
    @FXML
    public void openFileButton(){
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Doc (.txt)", "txt");
        chooser.setFileFilter(filter);
        chooser.setCurrentDirectory(new File("C:"));
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            path = chooser.getSelectedFile().getAbsolutePath();
            openScript.readFile();
            textArea.setText(code);
        }
    }
    @FXML
    public void consoleButton() {
        ScreenController sc = ScreenController.getScreenController();
        sc.activate("Console");
//        try {
//            Stage stage = (Stage) textArea.getScene().getWindow();
//            stage.close();
//            Parent root = FXMLLoader.load(Main.class.getResource("Console.fxml"));
//            stage = new Stage();
//            stage.setScene(new Scene(root, 600, 400));
//            stage.setTitle("Primus");
//            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/Logo2.png")));
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @FXML
    public void closeFileButton(){

    }
    @FXML
    public void saveFileButton(){

    }
    @FXML
    public void saveAsButton(){

    }
    @FXML
    public void preferencesButton(){

    }
    @FXML
    public void quitButton(){

    }

}
