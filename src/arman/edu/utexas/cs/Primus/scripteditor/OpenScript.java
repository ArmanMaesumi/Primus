package arman.edu.utexas.cs.Primus.scripteditor;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Prompts the user with an open file dialog. Used for opening
 * ".prm" Primus script files.
 */
public class OpenScript {

    public static String loadScript(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("PRM files (*.prm)", "*.prm");
        fileChooser.setTitle("Choose Primus script...");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                return new String(Files.readAllBytes(file.toPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}