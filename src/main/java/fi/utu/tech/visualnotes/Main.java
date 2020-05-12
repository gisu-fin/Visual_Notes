package fi.utu.tech.visualnotes;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;


public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/visualnotes.fxml"));
        stage.setTitle("Visual notes");
        stage.setScene(new Scene(root));
        stage.show();

    }

    public static void main (String[] args){
        launch(args);
    }
}
