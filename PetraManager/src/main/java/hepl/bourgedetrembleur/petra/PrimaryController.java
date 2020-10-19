package hepl.bourgedetrembleur.petra;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PrimaryController implements Initializable
{
    @FXML
    public TextField petraPortTextfield;
    @FXML
    private TextField petraIpTextfield;
    @FXML
    public Label errorLabel;
    @FXML
    public Button connectButton;
    @FXML
    public ProgressIndicator connectionIndicator;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        errorLabel.setText("");
        connectionIndicator.setDisable(true);
        connectionIndicator.setVisible(false);
        App.petraConnectionService.bind(petraIpTextfield.textProperty(), petraPortTextfield.textProperty(), errorLabel.textProperty());
    }

    @FXML
    public void connectAction(ActionEvent actionEvent)
    {
        connectionIndicator.setDisable(false);
        connectionIndicator.setVisible(true);
        connectButton.setDisable(true);
        App.petraConnectionService.reset();
        App.petraConnectionService.setOnSucceeded(workerStateEvent -> {
            try
            {
                App.driver = (PetraDriver) workerStateEvent.getSource().getValue();
                App.setScene("petra");
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            connectButton.setDisable(false);
        });
        App.petraConnectionService.setOnFailed(workerStateEvent -> {
            connectButton.setDisable(false);
            connectionIndicator.setDisable(true);
            connectionIndicator.setVisible(false);
        });
        App.petraConnectionService.start();
    }
}
