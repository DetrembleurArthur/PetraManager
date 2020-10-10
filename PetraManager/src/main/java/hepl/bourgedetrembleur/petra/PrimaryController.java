package hepl.bourgedetrembleur.petra;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.util.Duration;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class PrimaryController implements Initializable
{
    @FXML
    public TextField petraPortTextfield;
    @FXML
    private TextField petraIpTextfield;
    @FXML
    public Label errorLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {

    }

    @FXML
    public void connectAction(ActionEvent actionEvent)
    {
        String ip = petraIpTextfield.getText();
        String port_s = petraPortTextfield.getText();
        if(!ip.isBlank())
        {
            try
            {
                Inet4Address address = (Inet4Address) Inet4Address.getByName(ip);
                if (!port_s.isBlank()) {
                    try {
                        int port = Integer.parseInt(port_s);
                        if (port <= 1024 || port > 65535)
                            throw new NumberFormatException();
                        App.petra.connect(address, port);
                        errorLabel.setText("Connected to " + address.getHostAddress());

                        FadeTransition tr = new FadeTransition();
                        tr.setFromValue(1);
                        tr.setToValue(0);
                        tr.setDuration(Duration.millis(2000));
                        tr.setOnFinished(actionEvent1 -> {
                            try {
                                App.setScene("petra");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        tr.setNode(App.scene.getRoot());
                        tr.play();

                    } catch (NumberFormatException e) {
                        errorLabel.setText("port must be an integer between 1024 and 65536");
                    } catch (IOException e) {
                        errorLabel.setText("Connection to " + address.getHostAddress() + " failed");
                    }
                } else {
                    errorLabel.setText("port field is empty");
                }
            }
            catch(UnknownHostException e)
            {
                errorLabel.setText("bad ip format");
            }
        }
        else
        {
            errorLabel.setText("ip field is empty");
        }
    }
}
