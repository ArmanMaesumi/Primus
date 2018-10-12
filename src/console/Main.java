package console;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        ScreenController sc = ScreenController.getScreenController();
        Database.getDatabase().populateDefaultDefinitions();
        Parent root = FXMLLoader.load(getClass().getResource("Console.fxml"));
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/Logo2.png")));
        primaryStage.show();

        sc.init(scene, primaryStage);
        sc.addScreen("Console",
                FXMLLoader.load(getClass().getResource("Console.fxml")), 600, 400);
        sc.activate("Console");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
