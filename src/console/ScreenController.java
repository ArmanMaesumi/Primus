package console;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import utils.Pair;

import java.util.HashMap;
import java.util.Map;

public class ScreenController {

    private static ScreenController sc;

    private HashMap<String, Pane> screenMap = new HashMap<>();
    private HashMap<String, Pair> sizes = new HashMap<>();
    private Scene main;
    private Stage stage;
    private String activeName;

    private ScreenController() {

    }

    public static ScreenController getScreenController() {
        if (sc == null)
            sc = new ScreenController();
        return sc;
    }

    public void init(Scene main, Stage stage) {
        this.main = main;
        this.stage = stage;
        this.activeName = "Console";
    }

    public void addScreen(String name, Pane pane, int width, int height) {
        pane.setPrefSize(width, height);
        screenMap.put(name, pane);
        sizes.put(name, new Pair(width, height));
    }

    public void removeScreen(String name) {
        screenMap.remove(name);
    }

    public void activate(String name) {
        main.setRoot(screenMap.get(name));
        stage.setWidth(sizes.get(name).getL());
        stage.setHeight(sizes.get(name).getR());
        stage.setTitle("Primus - " + name);
        this.activeName = name;
    }

    public Stage getActiveStage(){
        return this.stage;
    }

    public Scene getActiveScene(){
        return this.main;
    }

    public String getActiveName() {
        return activeName;
    }

    public void setActiveName(String activeName) {
        this.activeName = activeName;
    }
}