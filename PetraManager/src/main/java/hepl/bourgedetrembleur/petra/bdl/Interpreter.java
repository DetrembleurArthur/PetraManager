package hepl.bourgedetrembleur.petra.bdl;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Stack;

public class Interpreter
{
    private Stack<Context> stack;

    public Interpreter()
    {
        stack = new Stack<>();
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

    public void interpret(String code)
    {
        stack.clear();
        stack.push(new Context());

        String[] tokens = code.replace("\n", ";").replace("\t", "").split(";");

        for(int i = 0; i < tokens.length; i++)
        {
            if(!tokens[i].isBlank())
            {
                var line = tokens[i].split(" ");
                if(!stack.peek().stop_execution)
                {
                    //System.err.println("> " + line[0]);
                    switch (line[0])
                    {
                        case "alert":
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
                                Thread.sleep(Integer.parseInt(line[1]));
                            } catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                            break;


                        case "print":
                            System.err.println(line[1]);
                            break;

                        case "if":
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
                                if(line[1].equals("false"))
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
                            stack.push(new Context());
                            stack.peek().condition = line[1];
                            if(!stack.peek().evaluate())
                            {
                                stack.peek().stop_execution = true;
                                stack.peek().else_enable = true;
                            }
                            else
                            {
                                stack.peek().begloop = i;
                            }
                            break;

                        case "endloop":
                            stack.peek().i++;
                            if(stack.peek().evaluate())
                            {
                                i = stack.peek().begloop;
                            }
                            else
                            {
                                stack.pop();
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
    }

    public static void main(String[] args)
    {
        Interpreter interpreter = new Interpreter();
        interpreter.interpret(Objects.requireNonNull(load("src/main/java/hepl/bourgedetrembleur/petra/code.txt")));
    }
}
