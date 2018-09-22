package scripteditor;

import javax.swing.*;
import java.util.Formatter;

/**
 * Created by Arman on 6/4/2015.
 */
class saveScript {

    private static Formatter code;

    public static void openFile(String path) {
        try {
            code = new Formatter(path);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred, script could not be saved.");
        }
    }

    public static void addRecords() {
        code.format("%s", "");
    }

    public static void closeFile() {
        code.close();
    }
}
