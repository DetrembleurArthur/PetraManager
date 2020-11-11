package hepl.bourgedetrembleur.petra;

import hepl.bourgedetrembleur.petra.bdl.Interpreter;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.ArrayList;

/*
* active <actuator>
* stop <actuator>
* switch <actuator>
* wait <mill>
* loop
* endloop
* if
* endif
* else
* */

public class ThesaurusService extends Service<Void>
{
    private String code;

    public void setCode(String code)
    {
        this.code = code;
    }


    @Override
    protected Task<Void> createTask()
    {
        return new Interpreter(code);
    }
}
