package scripteditor;

import console.Database;
import console.ScreenController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

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
    public VBox mainVBox;

    @FXML
    public void runButton() {
        run.setDisable(true);
        outputTextArea.clear();
        progressBar.setProgress(0);
        Database.getDatabase().resetDatabase();
        ScriptProcessor sc = new ScriptProcessor(textArea, outputTextArea, progressBar);
        Runnable task = () -> {
            sc.runScript();
            run.setDisable(false);
        };
        Thread backgroundThread = new Thread(task);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
//        new Thread(() -> {
//            ScriptProcessor sc = new ScriptProcessor(textArea, outputTextArea, progressBar);
//            sc.runScript();
//            run.setDisable(false);
//        }).start();
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            textArea.setWrapText(true);
            textArea.requestFocus();
        });
    }

    @FXML
    public void newFileButton() {
        textArea.setText("");
    }

    @FXML
    public void openFileButton() {
        String readIn = OpenScript.loadScript(((Stage) textArea.getScene().getWindow()));
        if (readIn != null) {
            code = readIn;
            textArea.setText(code);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error when loading Primus file");
            alert.setHeaderText("Primus IO error");
            alert.setContentText("Primus could not load file.");
            alert.showAndWait();
        }
//        JFileChooser chooser = new JFileChooser();
//        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Doc (.txt)", "txt");
//        chooser.setFileFilter(filter);
//        chooser.setCurrentDirectory(new File("C:"));
//        int returnVal = chooser.showOpenDialog(null);
//        if (returnVal == JFileChooser.APPROVE_OPTION) {
//            path = chooser.getSelectedFile().getAbsolutePath();
//            openScript.readFile();
//            textArea.setText(code);
//        }
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
    public void closeFileButton() {

    }

    @FXML
    public void saveFileButton() {
        code = textArea.getText();
        SaveScript.saveScript(((Stage) textArea.getScene().getWindow()), code);
    }

    @FXML
    public void saveAsButton() {

    }

    @FXML
    public void preferencesButton() {

    }

    @FXML
    public void quitButton() {

    }

}
