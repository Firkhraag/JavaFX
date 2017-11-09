package persons;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Start extends Application {
    
    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass()
                    .getResource("/XML/Persons.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Person Application");
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
