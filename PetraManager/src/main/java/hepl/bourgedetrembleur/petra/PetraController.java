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

    @FXML
    private ComboBox<String> testsComboBox;

    @FXML
    private Label scriptLabel;

    public static PetraController controller;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        controller = this;
        try
        {
            testsComboBox.getItems().addAll("no", "test 1", "test 2", "test 3", "test 4", "test 5");

            App.stage.setOnCloseRequest(windowEvent -> {
                disconnect();
            });

            scriptLabel.textProperty().bind(App.thesaurusService.messageProperty());

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
            App.thesaurusService.setOnFailed(workerStateEvent -> {
                validateScriptButton.setText("Validate");
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
                switchLabelStyleClass(sensor1Label);
                sensor2Label.setText(Integer.toString((t1 >> 1) & 1));
                switchLabelStyleClass(sensor2Label);
                tLabel.setText(Integer.toString((t1 >> 2) & 1));
                switchLabelStyleClass(tLabel);
                slotLabel.setText(Integer.toString((t1 >> 3) & 1));
                switchLabelStyleClass(slotLabel);
                chariotLabel.setText(Integer.toString((t1 >> 4) & 1));
                switchLabelStyleClass(chariotLabel);
                armLabel.setText(Integer.toString((t1 >> 5) & 1));
                switchLabelStyleClass(armLabel);
                diverLabel.setText(Integer.toString((t1 >> 6) & 1));
                switchLabelStyleClass(diverLabel);
                tubLabel.setText(Integer.toString((t1 >> 7) & 1));
                switchLabelStyleClass(tubLabel);
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

    private void switchLabelStyleClass(Label label)
    {
        if(label.getText().contains("0"))
        {
            label.setStyle("-fx-text-fill: red;-fx-font-size: 20");
        }
        else
        {
            label.setStyle("-fx-text-fill: #00ff00;-fx-font-size: 20");
        }
    }

    @FXML
    public void change_test_action()
    {
        String test = testsComboBox.getSelectionModel().getSelectedItem();
        if(test.equals("test 1"))
        {
            scriptTextArea.setText(
                    "active roller1\n" +
                    "active roller2\n" +
                    "wait 1000\n" +
                    "stop roller1\n" +
                    "stop roller2\n" +
                    "wait 500\n" +
                    "active arm\n" +
                    "wait 2000\n" +
                    "stop arm\n" +
                    "wait 500\n" +
                    "active blocker\n" +
                    "wait 1000\n" +
                    "stop blocker\n" +
                    "wait 500\n" +
                    "active tub\n" +
                    "wait 2500\n" +
                    "stop tub");
        }
        else if(test.equals("test 2"))
        {
            scriptTextArea.setText(
                    "loop !bac\n" +
                    "\tactive postub\n" +
                    "\twait 3500\n"+
                    "\tactive tub\n" +
                    "\twait 3000\n" +
                    "\tactive sucker\n" +
                    "\twait 500\n" +
                    "\tstop tub\n" +
                    "\twait 2000\n" +
                    "\tactive posr1\n" +
                    "\twait 3500\n" +
                    "\tactive roller1\n" +
                    "\twait 2000\n" +
                    "\tstop sucker\n" +
                    "endloop\n" +
                    "active postub\n" +
                    "stop roller1");
        }
        else if(test.equals("test 3"))
        {
            scriptTextArea.setText(
                    "loop !bac\n" +
                    "\tactive postub\n" +
                    "\twait 3500\n"+
                    "\tactive tub\n" +
                    "\twait 3000\n" +
                    "\tactive sucker\n" +
                    "\twait 500\n" +
                    "\tstop tub\n" +
                    "\twait 2000\n" +
                    "\tactive posr1\n" +
                    "\twait 3500\n" +
                    "\tactive roller1\n" +
                    "\twait 2000\n" +
                    "\tstop sucker\n" +
                    "\tloop !sensor2\n" +
                    "\tendloop\n" +
                    "\twait 500\n" +
                    "\tstop roller1\n" +
                    "endloop\n" +
                    "active postub\n");
        }
        else if(test.equals("test 4"))
        {
            scriptTextArea.setText(
                    "if !bac\n" +
                    "\tif roller2\n" +
                    "\t\tswitch arm\n" +
                    "\tendif\n" +
                    "endif"
            );
        }
        else if(test.equals("test 5"))
        {
            scriptTextArea.setText(
                    "loop 5\n" +
                    "\tif 2\n" +
                    "\t\tswitch roller2\n" +
                    "\telse\n" +
                    "\t\tswitch roller1\n" +
                    "\tendif\n" +
                    "\tswitch tub\n" +
                    "\twait 500\n" +
                    "endloop\n" +
                    "alert hello-world!"
            );
        }
        else
        {
            scriptTextArea.setText("");
        }
    }

    public boolean captorState(String name)
    {
        name = name.toLowerCase();
        if(name.equals("sensor1")) return Integer.parseInt(sensor1Label.getText()) == 1;
        if(name.equals("sensor2")) return Integer.parseInt(sensor2Label.getText()) == 1;
        if(name.equals("t")) return Integer.parseInt(tLabel.getText()) == 1;
        if(name.equals("slot")) return Integer.parseInt(slotLabel.getText()) == 1;
        if(name.equals("chariot")) return Integer.parseInt(chariotLabel.getText()) == 1;
        if(name.equals("armpos")) return Integer.parseInt(armLabel.getText()) == 1;
        if(name.equals("diver")) return Integer.parseInt(diverLabel.getText()) == 1;
        if(name.equals("bac")) return Integer.parseInt(tubLabel.getText()) == 1;
        return false;
    }

    public boolean actuatorState(String name)
    {
        name = name.toLowerCase();
        if(name.equals("roller1")) return roller1ToggleButton.isSelected();
        if(name.equals("roller2")) return roller2ToggleButton.isSelected();
        if(name.equals("tub")) return tubToggleButton.isSelected();
        if(name.equals("arm")) return armToggleButton.isSelected();
        if(name.equals("blocker")) return blockerToggleButton.isSelected();
        if(name.equals("sucker")) return suckerButton.isSelected();
        if(name.equals("postub")) return rollerArmTubPositionToggleButton.isSelected();
        if(name.equals("posr1")) return rollerArmRoller1PositionToggleButton.isSelected();
        if(name.equals("posr2")) return rollerArmRoller2PositionToggleButton.isSelected();
        if(name.equals("posr1r2")) return rollerArmRoller12PositionToggleButton.isSelected();
        return false;
    }

    public void actuatorActivate(String name)
    {
        name = name.toLowerCase();
        if(name.equals("roller1")) roller1ToggleButton.fire();
        if(name.equals("roller2")) roller2ToggleButton.fire();
        if(name.equals("tub")) tubToggleButton.fire();
        if(name.equals("arm")) armToggleButton.fire();
        if(name.equals("blocker")) blockerToggleButton.fire();
        if(name.equals("sucker")) suckerButton.fire();
        if(name.equals("postub")) rollerArmTubPositionToggleButton.fire();
        if(name.equals("posr1")) rollerArmRoller1PositionToggleButton.fire();
        if(name.equals("posr2")) rollerArmRoller2PositionToggleButton.fire();
        if(name.equals("posr1r2")) rollerArmRoller12PositionToggleButton.fire();
        try
        {
            Thread.sleep(50);
        } catch (InterruptedException e)
        {

        }
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
