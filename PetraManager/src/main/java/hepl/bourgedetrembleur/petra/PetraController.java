package hepl.bourgedetrembleur.petra;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PetraController implements Initializable
{
    @FXML
    private ToggleButton roller1ToggleButton;
    @FXML
    private ToggleButton roller2ToggleButton;
    @FXML
    private ToggleButton suckerButton;
    @FXML
    private ToggleButton tubToggleButton;
    @FXML
    private ToggleButton armToggleButton;
    @FXML
    private ToggleButton blockerToggleButton;
    @FXML
    private ToggleButton rollerArmTubPositionToggleButton;
    @FXML
    private ToggleButton rollerArmRoller1PositionToggleButton;
    @FXML
    private ToggleButton rollerArmRoller2PositionToggleButton;
    @FXML
    private ToggleButton rollerArmRoller12PositionToggleButton;
    @FXML
    private ToggleButton autocommitToggleButton;
    @FXML
    private Button commitButton;
    @FXML
    private Button disconnectButton;

    @FXML
    private TitledPane sensorsTitledPane;

    @FXML
    private Label petraIpLabel;
    @FXML
    private Label actuatorsPortLabel;
    @FXML
    private Label sensorsPortLabel;
    @FXML
    private Label connectionLabel;

    @FXML
    private Label sensor1Label;
    @FXML
    private Label sensor2Label;
    @FXML
    private Label tLabel;
    @FXML
    private Label slotLabel;
    @FXML
    private Label chariotLabel;
    @FXML
    private Label armLabel;
    @FXML
    private Label diverLabel;
    @FXML
    private Label tubLabel;

    @FXML
    private Label actuatorsStatusLabel;
    @FXML
    private Label sensorsStatusLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        try
        {
            App.stage.setOnCloseRequest(windowEvent -> {
                disconnect();
            });
            commitButton.disableProperty().bind(autocommitToggleButton.selectedProperty());

            App.petraActuatorsAckService = new PetraActuatorsAckService(App.driver.getActuatorsSocket().getInputStream());
            App.petraActuatorsAckService.start();
            actuatorsStatusLabel.textProperty().bind(App.petraActuatorsAckService.valueProperty().asString());


            App.petraSensorsWatcherService = new PetraSensorsWatcherService(App.driver.getSensorsSocket().getInputStream());
            App.petraSensorsWatcherService.setOnSucceeded(workerStateEvent -> {
                try
                {
                    App.driver.getSensorsSocket().close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            });
            sensorsStatusLabel.textProperty().bind(App.petraSensorsWatcherService.valueProperty().asString());
            petraIpLabel.setText(App.driver.getActuatorsSocket().getInetAddress().getHostAddress());
            actuatorsPortLabel.setText(Integer.toString(App.driver.getActuatorsSocket().getPort()));
            sensorsPortLabel.setText(Integer.toString(App.driver.getSensorsSocket().getPort()));
            connectionLabel.textProperty().bind(App.petraSensorsWatcherService.messageProperty());
            App.petraSensorsWatcherService.valueProperty().addListener((observableValue, integer, t1) -> {
                sensor1Label.setText(Integer.toString(t1 & 1));
                sensor2Label.setText(Integer.toString((t1 >> 1) & 1));
                tLabel.setText(Integer.toString((t1 >> 2) & 1));
                slotLabel.setText(Integer.toString((t1 >> 3) & 1));
                chariotLabel.setText(Integer.toString((t1 >> 4) & 1));
                armLabel.setText(Integer.toString((t1 >> 5) & 1));
                diverLabel.setText(Integer.toString((t1 >> 6) & 1));
                tubLabel.setText(Integer.toString((t1 >> 7) & 1));
            });
            App.petraSensorsWatcherService.start();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    public void actuatorsRoller1_action()
    {
        App.driver.action(PetraDriver.ROLLER1);
    }

    @FXML
    public void actuatorsRoller2_action()
    {
        App.driver.action(PetraDriver.ROLLER2);
    }

    @FXML
    public void actuatorsTub_action()
    {
        App.driver.action(PetraDriver.TUB);
    }

    @FXML
    public void actuatorsArm_action()
    {
        App.driver.action(PetraDriver.ARM);
    }

    @FXML
    public void actuatorsBlocker_action()
    {
        App.driver.action(PetraDriver.BLOCKER);
    }

    @FXML
    public void actuatorsSucker_action()
    {
        App.driver.action(PetraDriver.SUCKER);
    }

    @FXML
    public void actuatorsRollerArmTub_action()
    {
        App.driver.action(PetraDriver.ARM_TUB);
    }

    @FXML
    public void actuatorsRollerArmR1_action()
    {
        App.driver.action(PetraDriver.ARM_R1);
    }

    @FXML
    public void actuatorsRollerArmR2_action()
    {
        App.driver.action(PetraDriver.ARM_R2);
    }

    @FXML
    public void actuatorsRollerArmR1R2_action()
    {
        App.driver.action(PetraDriver.ARM_R1R2);
    }

    @FXML
    public void actuatorsAutocommit_action()
    {
        App.driver.action(PetraDriver.AUTO_COMMIT);
    }

    @FXML
    public void actuatorsCommit_action()
    {
        App.driver.action(PetraDriver.COMMIT);
    }

    @FXML
    public void disconnect()
    {
        App.driver.action(PetraDriver.EXIT);
        try
        {
            App.driver.getActuatorsSocket().close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        try
        {
            App.setScene("main");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
