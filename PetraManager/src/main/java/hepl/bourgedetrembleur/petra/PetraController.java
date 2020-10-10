package hepl.bourgedetrembleur.petra;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;

import java.net.URL;
import java.util.ResourceBundle;

public class PetraController implements Initializable
{
    @FXML
    private ToggleButton roller1ToggleButton;
    @FXML
    private ToggleButton roller2ToggleButton;
    @FXML
    private Button suckerButton;
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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {

    }

    @FXML
    public void actuatorsRoller1_action()
    {
        System.out.println(roller1ToggleButton.isSelected());
    }
}
