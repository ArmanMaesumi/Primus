package scripteditor;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;

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
    public MenuItem newFile, openFile, closeFile, saveFile, saveAs, preferences, quit;
    @FXML
    public TextArea textArea;

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
