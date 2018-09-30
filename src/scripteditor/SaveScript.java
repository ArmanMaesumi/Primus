package scripteditor;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Formatter;

/**
 * Prompts the user with an save file dialog. Used for saving
 * ".prm" Primus script files.
 */
class SaveScript {

    public static void saveScript(Stage stage, String code) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("PRM files (*.prm)", "*.prm");
        fileChooser.getExtensionFilters().addAll(extFilter);

        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            saveFile(code, file);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error when saving Primus file");
            alert.setHeaderText("Primus IO error");
            alert.setContentText("Primus could not save file.");
            alert.showAndWait();
        }
    }

    private static void saveFile(String content, File file) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException ex) {

        }

    }
}
