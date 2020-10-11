package hepl.bourgedetrembleur.petra;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.IOException;
import java.io.InputStream;

public class PetraSensorsWatcherService extends Service<Integer>
{
    private InputStream watcher;

    public PetraSensorsWatcherService(InputStream watcher)
    {
        this.watcher = watcher;
        setOnSucceeded(workerStateEvent -> {
            try
            {
                App.driver.getSensorsSocket().close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected Task<Integer> createTask()
    {
        return new Task<Integer>()
        {
            @Override
            protected Integer call() throws Exception
            {
                int sensors = 0;
                updateMessage("established");
                do
                {
                    sensors = watcher.read();
                    updateValue(sensors);
                }while(sensors != -1);
                updateMessage("closed");
                return sensors;
            }
        };
    }


}
