package hoang.db;


public class DatabaseConnectionWrapper 
{
	private static DatabaseConnectionWrapper INSTANCE;
	
	private DatabaseConnectionWrapper()
	{
		//empty constructor
	}
	
	public static DatabaseConnectionWrapper getInstance()
	{
		if(INSTANCE == null)
		{
			INSTANCE = new DatabaseConnectionWrapper();
		}
		
		return INSTANCE;
	}

	public SQLDatabaseConnection getSQLDatabaseConnection() throws DBConnectionException
	{
		return new SQLDatabaseConnection();
	}

}
