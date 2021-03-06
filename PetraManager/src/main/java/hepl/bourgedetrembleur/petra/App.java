package hepl.bourgedetrembleur.petra;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    public static Scene scene;
    public static Stage stage;
    public static PetraDriver driver;
    public static PetraConnectionService petraConnectionService = new PetraConnectionService();
    public static PetraSensorsWatcherService petraSensorsWatcherService = null;
    public static PetraActuatorsAckService petraActuatorsAckService = null;
    public static ThesaurusService thesaurusService = new ThesaurusService();


    @Override
    public void start(Stage stage) throws IOException {

        App.stage = stage;
        stage.setResizable(false);
        setScene("main");
    }

    static void setScene(String fxml) throws IOException {
        scene = new Scene(loadFXML(fxml));
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}