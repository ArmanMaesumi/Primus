package scripteditor;

import javafx.fxml.FXML;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Arman on 6/4/2015.
 */
class openScript {
    public static void readFile() {
        BufferedReader br = null;
        String code = "";
        try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader(Controller.path));
            while ((sCurrentLine = br.readLine()) != null) {
                code = code + sCurrentLine + "\n";
                System.out.println(sCurrentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        Controller.code = code;
    }
}