package Task4.CharStackExceptions;

public class CharStackEmptyException extends Exception
{
    public CharStackEmptyException()
    {
        super ("Char Stack is empty.");
    }
}