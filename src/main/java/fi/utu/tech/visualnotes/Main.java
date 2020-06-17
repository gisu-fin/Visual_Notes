package fi.utu.tech.visualnotes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/visualnotes.fxml"));

        Scene scene = new Scene(root);

        /*
        Icons made by "http://www.freepik.com/"
         */

        stage.getIcons().add(new Image("paint-brush-16.png"));
        stage.getIcons().add(new Image("paint-brush-24.png"));
        stage.getIcons().add(new Image("paint-brush-32.png"));
        stage.setTitle("Visual notes");
        stage.setScene(scene);
        stage.show();

    }

    public static void main (String[] args){
        launch(args);
    }

}
