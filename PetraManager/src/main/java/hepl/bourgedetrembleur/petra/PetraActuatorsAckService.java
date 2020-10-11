package hepl.bourgedetrembleur.petra;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.IOException;
import java.io.InputStream;

public class PetraActuatorsAckService extends Service<Integer>
{
    private InputStream ack;

    public PetraActuatorsAckService(InputStream ack)
    {
        this.ack = ack;
    }

    @Override
    protected Task<Integer> createTask()
    {
        return new Task<Integer>()
        {
            @Override
            protected Integer call() throws Exception
            {
                Integer actuators = -1;
                do
                {
                    actuators = ack.read();
                    updateValue(actuators);
                }while(actuators != -1);
                return actuators;
            }
        };
    }
}
