package console;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import objects.Function;
import objects.PrimusObject;
import objects.Variable;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Database.getDatabase().populateDefaultDefinitions();
        Parent root = FXMLLoader.load(getClass().getResource("Console.fxml"));
        primaryStage.setTitle("Primus");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/Logo2.png")));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
