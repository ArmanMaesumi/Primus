package arman.edu.utexas.cs.Primus.console;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("test");
        ScreenController sc = ScreenController.getScreenController();
        Database.getDatabase().populateDefaultDefinitions();
        System.out.println("test");
        Parent root = FXMLLoader.load(getClass().getResource("Console.fxml"));
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/arman/edu/utexas/cs/Primus/images/Logo2.png")));
        primaryStage.show();

        System.out.println("test");
        sc.init(scene, primaryStage);
        sc.addScreen("Console",
                FXMLLoader.load(getClass().getResource("Console.fxml")), 600, 400);
        sc.activate("Console");
        System.out.println("test");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
