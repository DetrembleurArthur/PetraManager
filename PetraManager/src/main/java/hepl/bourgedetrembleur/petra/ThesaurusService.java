package hepl.bourgedetrembleur.petra;

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
        return new Task<Void>()
        {
            public boolean state = false;
            public boolean calculate(String device)
            {
                state = false;
                boolean not = false;
                for(var c : device.toCharArray())
                {
                    if(c == '!') not = !not;
                    else break;
                }
                device = device.replace("!", "");
                state = PetraController.controller.actuatorState(device);
                if(!state)
                    state = PetraController.controller.captorState(device);
                System.err.println("STATE: " + state);
                System.err.println("NOT  : " + not);
                if(not) return !state;
                return state;
            }

            public boolean loop(int i, String cond)
            {
                try
                {
                    int n = Integer.parseInt(cond);
                    return i < n;
                }
                catch(NumberFormatException e)
                {
                    return calculate(cond);
                }
            }

            public void traitement(String text) throws InterruptedException
            {
                text = text.replace("\n", ";").replace("\t", "");
                String[] tokens = text.split(";");
                int skip = 0;
                int i = 0;
                int remember = -1;
                boolean myState = false;
                System.err.println("TRAITEMENT: " + text);
                for(int j = 0; j < tokens.length; j++)
                {
                    if(isCancelled())
                    {
                        System.err.println("stop");
                        return;
                    }
                    if(!tokens[j].isBlank())
                    {
                        System.err.println("TOKEN: " + tokens[j]);
                        String[] expr = tokens[j].split(" ");
                        System.err.println("SKIP: " + skip);
                        if(skip == 0)
                        {
                            System.err.println("COMMAND: " + expr[0]);
                            switch (expr[0].toLowerCase())
                            {
                                case "wait":
                                    if(expr.length < 2) continue;
                                    Thread.sleep(Integer.parseInt(expr[1]));
                                    break;
                                case "active":
                                    if(expr.length < 2) continue;
                                    System.err.println("ACTIVE: " + expr[1]);
                                    if(!PetraController.controller.actuatorState(expr[1]))
                                    {
                                        PetraController.controller.actuatorActivate(expr[1]);
                                    }
                                    break;
                                case "stop":
                                    if(expr.length < 2) continue;
                                    System.err.println("STOP: " + expr[1]);
                                    if(PetraController.controller.actuatorState(expr[1]))
                                    {
                                        PetraController.controller.actuatorActivate(expr[1]);
                                    }
                                    break;
                                case "switch":
                                    if(expr.length < 2) continue;
                                    System.err.println("SWITCH: " + expr[1]);
                                    PetraController.controller.actuatorActivate(expr[1]);
                                    break;
                                case "if":
                                    if(expr.length < 2) continue;
                                    if(!calculate(expr[1]))
                                    {
                                        skip = 1;
                                    }
                                    break;
                                case "loop":
                                    if(expr.length < 2) continue;
                                    if(!calculate(expr[1]))
                                    {
                                        i = 0;
                                        skip = 1;
                                    }
                                    else
                                    {
                                        remember = j;
                                    }
                                    break;
                                case "endloop":
                                    j = remember - 1;
                                    i++;
                                    break;
                            }
                        }
                        else
                        {
                            switch(expr[0].toLowerCase())
                            {
                                case "endif":
                                case "endloop":
                                    skip--;
                                    break;
                                case "if":
                                case "loop":
                                    skip++;
                                    break;
                            }
                            if(skip < 0) skip = 0;
                        }
                    }
                }
            }

            @Override
            protected Void call() throws Exception
            {
                traitement(code);
                return null;
            }
        };
    }
}
