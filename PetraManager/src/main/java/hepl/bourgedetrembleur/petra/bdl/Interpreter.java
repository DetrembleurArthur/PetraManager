package hepl.bourgedetrembleur.petra.bdl;

import hepl.bourgedetrembleur.petra.PetraController;
import javafx.concurrent.Task;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

public class Interpreter extends Task<Void>
{
    private Stack<Context> stack;
    private String code;

    public Interpreter(String code)
    {
        stack = new Stack<>();
        this.code = code;
    }


    @Override
    protected Void call() throws Exception
    {
        updateMessage("begin script");
        try
        {
            interpret(code);
        } catch (Exception e)
        {
            updateMessage(e.getMessage());
            throw e;
        }
        return null;
    }

    public static String load(String filename)
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String buffer = "";
            String content = "";
            while((buffer = reader.readLine()) != null)
                content += buffer+";";
            System.err.println(content);
            return content;
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public boolean test_device(String deviceId)
    {
        boolean not = deviceId.charAt(0) == '!';
        deviceId = deviceId.replace("!", "");
        if(!PetraController.controller.actuatorState(deviceId))
        {
            boolean ret = PetraController.controller.captorState(deviceId);
            if(not) return !ret;
            return ret;
        }
        System.out.println(!not);
        return !not;
    }

    public void check_param(String cmd, int goal, int current) throws Exception
    {
        if(goal != current) throw new Exception("number of param of " + cmd + " must be " + goal);
    }

    public void interpret(String code) throws Exception
    {
        stack.clear();
        stack.push(new Context());

        String[] tokens = code.replace("\n", ";").replace("\t", "").split(";");

        for(int i = 0; i < tokens.length; i++)
        {
            if(isCancelled())
            {
                updateMessage("script canceled");
                return;
            }
            if(!tokens[i].isBlank())
            {
                updateMessage(tokens[i]);
                var line = tokens[i].split(" ");
                if(!stack.peek().stop_execution)
                {
                    //System.err.println("> " + line[0]);
                    switch (line[0])
                    {
                        case "alert":
                            check_param("alert", 1, line.length-1);
                            JOptionPane.showMessageDialog(null, line[1]);
                            break;
                        case "exit":
                            return;
                        case "trace":
                            System.err.println(stack.peek().i + " / " + stack.peek().condition);
                            break;
                        case "beep":
                            Toolkit.getDefaultToolkit().beep();
                            break;
                        case "wait":
                            try
                            {
                                check_param("alert", 1, line.length-1);
                                Thread.sleep(Integer.parseInt(line[1]));
                            } catch (InterruptedException e)
                            {

                            }
                            break;

                        case "active":
                        case "+":
                            check_param("alert", 1, line.length-1);
                            if(!PetraController.controller.actuatorState(line[1]))
                            {
                                PetraController.controller.actuatorActivate(line[1]);
                            }
                            break;

                        case "stop":
                        case "-":
                            check_param("alert", 1, line.length-1);
                            if(PetraController.controller.actuatorState(line[1]))
                            {
                                PetraController.controller.actuatorActivate(line[1]);
                            }
                            break;

                        case "switch":
                        case "*":
                            check_param("alert", 1, line.length-1);
                            PetraController.controller.actuatorActivate(line[1]);
                            break;


                        case "print":
                            check_param("alert", 1, line.length-1);
                            System.err.println(line[1]);
                            break;

                        case "if":
                            check_param("alert", 1, line.length-1);
                            Context before = stack.peek();
                            stack.push(new Context());



                            try
                            {
                                if(!before.evaluate(Integer.parseInt(line[1])))
                                {
                                    stack.peek().stop_execution = true;
                                    stack.peek().else_enable = true;
                                }
                            }
                            catch (NumberFormatException e)
                            {
                                if(!test_device(line[1]))
                                {
                                    stack.peek().stop_execution = true;
                                    stack.peek().else_enable = true;
                                }
                            }

                            break;

                        case "endif":
                            stack.pop();
                            break;

                        case "else":
                            stack.peek().stop_execution = true;
                            break;

                        case "loop":
                            check_param("alert", 1, line.length-1);
                            stack.push(new Context());
                            stack.peek().condition = line[1];
                            try
                            {
                                if (!stack.peek().evaluate())
                                {
                                    stack.peek().stop_execution = true;
                                    stack.peek().else_enable = true;
                                } else
                                {
                                    stack.peek().begloop = i;
                                }
                            }
                            catch (NumberFormatException e)
                            {
                                if(!test_device(line[1]))
                                {
                                    stack.peek().stop_execution = true;
                                    stack.peek().else_enable = true;
                                } else
                                {
                                    stack.peek().begloop = i;
                                }
                            }
                            break;

                        case "endloop":
                            stack.peek().i++;
                            try
                            {
                                if (stack.peek().evaluate())
                                {
                                    i = stack.peek().begloop;
                                } else
                                {
                                    stack.pop();
                                }
                            }
                            catch (NumberFormatException e)
                            {
                                if(test_device(stack.peek().condition))
                                {
                                    i = stack.peek().begloop;
                                }
                                else
                                {
                                    stack.pop();
                                }
                            }
                    }
                }
                else
                {
                    //System.err.println("< " + line[0]);
                    switch (line[0])
                    {
                        case "if":
                        case "loop":
                            stack.push(new Context());
                            stack.peek().stop_execution = true;
                            break;

                        case "else":
                            if(stack.peek().else_enable)
                                stack.peek().stop_execution = false;
                            break;

                        case "endif":
                        case "endloop":
                            stack.pop();
                    }
                }
            }
        }
        updateMessage("script finished");
    }
}
