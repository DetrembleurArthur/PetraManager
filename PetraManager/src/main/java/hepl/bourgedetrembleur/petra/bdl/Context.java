package hepl.bourgedetrembleur.petra.bdl;

public class Context
{
    public boolean stop_execution = false;
    public boolean else_enable = false;
    public int i = 0;
    public int begloop = -1;
    public String condition = "false";

    public boolean evaluate(int i)
    {
        return i == this.i;
    }

    public boolean evaluate()
    {
        return i < Integer.parseInt(condition);
    }
}
