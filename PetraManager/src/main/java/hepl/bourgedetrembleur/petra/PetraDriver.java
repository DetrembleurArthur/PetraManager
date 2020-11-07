package hepl.bourgedetrembleur.petra;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;

public class PetraDriver
{
    public static final int NO_PA = 0;
    public static final int ROLLER1 = 1;
    public static final int ROLLER2 = 2;
    public static final int SUCKER = 3;
    public static final int TUB = 4;
    public static final int ARM = 5;
    public static final int BLOCKER = 6;
    public static final int ARM_TUB = 7;
    public static final int ARM_R1 = 8;
    public static final int ARM_R2 = 9;
    public static final int ARM_R1R2 = 10;
    public static final int AUTO_COMMIT = 11;
    public static final int COMMIT = 12;
    public static final int EXIT = 13;

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

    public Socket getActuatorsSocket()
    {
        return actuatorsSocket;
    }

    public Socket getSensorsSocket()
    {
        return sensorsSocket;
    }


    public void action(int petraAction)
    {
        try
        {
            actuatorsSocket.getOutputStream().write(petraAction);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
