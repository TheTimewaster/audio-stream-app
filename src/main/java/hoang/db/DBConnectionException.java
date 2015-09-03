package hoang.db;

public class DBConnectionException extends Throwable
{
	/**
	 * 
	 */
    private static final long serialVersionUID = 7002821286641744394L;
	String message;
	
	DBConnectionException(String _message)
	{
		message = _message;
	}
	
	public String getMessage()
	{
		return message;
	}
}
