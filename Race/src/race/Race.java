package race;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Race extends Application {
    
    @Override
    public void start(Stage stage) {
        try {
            VBox root = FXMLLoader.load(getClass()
                    .getResource("/XML/RaceTrack.fxml"));
            Scene scene = new Scene(root);
            stage.getIcons().add(new Image(getClass()
                    .getResourceAsStream("/IMG/icon.jpg")));
            stage.setScene(scene);
            stage.setTitle("Race");
            stage.show();
        }
        catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
