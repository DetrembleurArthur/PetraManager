package hepl.bourgedetrembleur.petra;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;

public class PetraDriver
{
    private Socket actuatorsSocket;
    private Socket sensorsSocket;

    public PetraDriver()
    {

    }

    public void connect(Inet4Address petraIp, int port) throws IOException
    {
        actuatorsSocket = new Socket(petraIp.getHostAddress(), port);
        sensorsSocket = new Socket(petraIp.getHostAddress(), port + 1);
    }
}
