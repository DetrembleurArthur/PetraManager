package hepl.bourgedetrembleur.petra;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;

public class PetraConnectionService extends Service<PetraDriver>
{
    private StringProperty ip = new SimpleStringProperty();
    private StringProperty port = new SimpleStringProperty();
    private StringProperty error = new SimpleStringProperty();

    public PetraConnectionService()
    {

    }

    public void bind(StringProperty formularIp, StringProperty formularPort, StringProperty errorLabel)
    {
        ip.bind(formularIp);
        port.bind(formularPort);
        errorLabel.bind(messageProperty());
    }

    @Override
    protected Task<PetraDriver> createTask()
    {
        return new Task<>() {
            @Override
            protected PetraDriver call() throws Exception {
                PetraDriver driver = new PetraDriver();
                if(!ip.get().isBlank())
                {
                    try
                    {
                        Inet4Address address = (Inet4Address) Inet4Address.getByName(ip.get());
                        if (!port.get().isBlank()) {
                            try {
                                int port_i = Integer.parseInt(port.get());
                                if (port_i <= 1024 || port_i > 65535)
                                    throw new NumberFormatException();
                                try
                                {
                                    updateMessage("connection... please wait...");
                                    driver.connect(address, port_i);
                                    return driver;
                                }
                                catch(Exception e)
                                {
                                    updateMessage("connection failed");
                                }
                            } catch (NumberFormatException e) {
                                updateMessage("port must be an integer between 1024 and 65536");
                            }
                        } else {
                            updateMessage("port field is empty");
                        }
                    }
                    catch(UnknownHostException e)
                    {
                        updateMessage("bad ip format");
                    }
                }
                else
                {
                    updateMessage("ip field is empty");
                }
                throw new Exception();
            }
        };
    }
}
