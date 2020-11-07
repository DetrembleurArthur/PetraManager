package hepl.bourgedetrembleur.petra;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.util.Duration;

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

    @FXML
    private Box myBox;

    @FXML
    private TextArea scriptTextArea;

    @FXML
    private Button validateScriptButton;

    @FXML
    private TitledPane actuatorPanel;

    public static PetraController controller;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        controller = this;
        try
        {
            App.stage.setOnCloseRequest(windowEvent -> {
                disconnect();
            });

            App.thesaurusService.setOnRunning(workerStateEvent -> {
                validateScriptButton.setText("Stop");
                //actuatorPanel.setDisable(true);
            });
            App.thesaurusService.setOnSucceeded(workerStateEvent ->{
                validateScriptButton.setText("Validate");
                //actuatorPanel.setDisable(false);
            });
            App.thesaurusService.setOnCancelled(workerStateEvent ->{
                validateScriptButton.setText("Validate");
                //actuatorPanel.setDisable(false);
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

            PhongMaterial material = new PhongMaterial();
            material.setDiffuseMap(new Image(getClass().getResourceAsStream("PETRA.png")));
            myBox.setMaterial(material);
            myBox.setRotationAxis(myBox.getRotationAxis().add(0.2, 1, 0.8));
            Timeline timeline = new Timeline(new KeyFrame(Duration.ONE, actionEvent -> {
                myBox.setRotate(myBox.getRotate()+0.075);;
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.playFromStart();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean captorState(String name)
    {
        if(name.equals("C-Sensor-1")) return Integer.parseInt(sensor1Label.getText()) == 1;
        if(name.equals("C-Sensor-2")) return Integer.parseInt(sensor2Label.getText()) == 1;
        if(name.equals("C-T")) return Integer.parseInt(tLabel.getText()) == 1;
        if(name.equals("C-Slot")) return Integer.parseInt(slotLabel.getText()) == 1;
        if(name.equals("C-Chariot")) return Integer.parseInt(chariotLabel.getText()) == 1;
        if(name.equals("C-Arm")) return Integer.parseInt(armLabel.getText()) == 1;
        if(name.equals("C-Diver")) return Integer.parseInt(diverLabel.getText()) == 1;
        if(name.equals("C-Tub")) return Integer.parseInt(tubLabel.getText()) == 1;
        return false;
    }

    public boolean actuatorState(String name)
    {
        if(name.equals("A-Roller-1")) return roller1ToggleButton.isSelected();
        if(name.equals("A-Roller-2")) return roller2ToggleButton.isSelected();
        if(name.equals("A-Tub")) return tubToggleButton.isSelected();
        if(name.equals("A-Arm")) return armToggleButton.isSelected();
        if(name.equals("A-Blocker")) return blockerToggleButton.isSelected();
        if(name.equals("A-Sucker")) return suckerButton.isSelected();
        if(name.equals("A-POS-Tub")) return rollerArmTubPositionToggleButton.isSelected();
        if(name.equals("A-POS-R1")) return rollerArmRoller1PositionToggleButton.isSelected();
        if(name.equals("A-POS-R2")) return rollerArmRoller2PositionToggleButton.isSelected();
        if(name.equals("A-POS-R1R2")) return rollerArmRoller12PositionToggleButton.isSelected();
        return false;
    }

    public void actuatorActivate(String name)
    {
        System.err.println("PUTE: " + name);
        if(name.equals("A-Roller-1")) roller1ToggleButton.fire();
        if(name.equals("A-Roller-2")) roller2ToggleButton.fire();
        if(name.equals("A-Tub")) tubToggleButton.fire();
        if(name.equals("A-Arm")) armToggleButton.fire();
        if(name.equals("A-Blocker")) blockerToggleButton.fire();
        if(name.equals("A-Sucker")) suckerButton.fire();
        if(name.equals("A-POS-Tub")) rollerArmTubPositionToggleButton.fire();
        if(name.equals("A-POS-R1")) rollerArmRoller1PositionToggleButton.fire();
        if(name.equals("A-POS-R2")) rollerArmRoller2PositionToggleButton.fire();
        if(name.equals("A-POS-R1R2")) rollerArmRoller12PositionToggleButton.fire();
    }

    private void flipAnimation(Node node)
    {
        RotateTransition tr = new RotateTransition();
        tr.setNode(node);
        tr.setAxis(new Point3D(0, 1, 0));
        tr.setFromAngle(0);
        tr.setToAngle(360);
        tr.setDuration(Duration.millis(250));
        tr.play();
    }

    @FXML
    public void actuatorsRoller1_action()
    {
        System.err.println("HZDIUGDGIDZUGZGDYZGDY");
        App.driver.action(PetraDriver.ROLLER1);
        flipAnimation(roller1ToggleButton);
    }

    @FXML
    public void actuatorsRoller2_action()
    {
        App.driver.action(PetraDriver.ROLLER2);
        flipAnimation(roller2ToggleButton);
    }

    @FXML
    public void actuatorsTub_action()
    {
        App.driver.action(PetraDriver.TUB);
        flipAnimation(tubToggleButton);
    }

    @FXML
    public void actuatorsArm_action()
    {
        App.driver.action(PetraDriver.ARM);
        flipAnimation(armToggleButton);
    }

    @FXML
    public void actuatorsBlocker_action()
    {
        App.driver.action(PetraDriver.BLOCKER);
        flipAnimation(blockerToggleButton);
    }

    @FXML
    public void actuatorsSucker_action()
    {
        App.driver.action(PetraDriver.SUCKER);
        flipAnimation(suckerButton);
    }

    @FXML
    public void actuatorsRollerArmTub_action()
    {
        App.driver.action(PetraDriver.ARM_TUB);
        flipAnimation(rollerArmTubPositionToggleButton);
    }

    @FXML
    public void actuatorsRollerArmR1_action()
    {
        App.driver.action(PetraDriver.ARM_R1);
        flipAnimation(rollerArmRoller1PositionToggleButton);
    }

    @FXML
    public void actuatorsRollerArmR2_action()
    {
        App.driver.action(PetraDriver.ARM_R2);
        flipAnimation(rollerArmRoller2PositionToggleButton);
    }

    @FXML
    public void actuatorsRollerArmR1R2_action()
    {
        App.driver.action(PetraDriver.ARM_R1R2);
        flipAnimation(rollerArmRoller12PositionToggleButton);
    }

    @FXML
    public void actuatorsAutocommit_action()
    {
        App.driver.action(PetraDriver.AUTO_COMMIT);
        flipAnimation(autocommitToggleButton);
    }

    @FXML
    public void actuatorsCommit_action()
    {
        App.driver.action(PetraDriver.COMMIT);
        flipAnimation(commitButton);
    }

    @FXML
    public void validateScript_action()
    {
        if(validateScriptButton.getText().equals("Validate"))
        {
            String code = scriptTextArea.getText();
            if(!code.isBlank() && !App.thesaurusService.isRunning())
            {
                App.thesaurusService.setCode(code);
                App.thesaurusService.reset();
                App.thesaurusService.start();
                flipAnimation(validateScriptButton);
            }
        }
        else
        {
            App.thesaurusService.cancel();
        }
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
