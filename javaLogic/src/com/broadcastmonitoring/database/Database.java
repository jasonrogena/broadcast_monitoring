package com.broadcastmonitoring.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.broadcastmonitoring.utils.Int;

public class Database 
{
	private final String url;
	private final String database;
	private final String driver;
	private Connection connection;
	private final String user;
	private final String password;
	private PreparedStatement preparedStatement;
	private int insertColumnCount;
	
	public Database(String database, String user, String password)
	{
		this.url="jdbc:mysql://localhost/";
		this.database=database;
		this.driver="com.mysql.jdbc.Driver";
		this.user=user;
		this.password=password;
		
		connection=null;
		preparedStatement=null;
		insertColumnCount=1;
		
		try
		{
			Class.forName(driver);
			this.connection=DriverManager.getConnection(url+database+"?user="+user+"&password="+password);
		}
		catch (SQLException e)
		{
			System.err.println("****SQLEXception thrown****");
			e.printStackTrace();
		}
		catch (ClassNotFoundException  e) 
		{
			System.err.println("****ClassNotFoundException thrown****");
			e.printStackTrace();
		}
	}
	
	public boolean initInsertStatement(String query)
	{
		boolean result=false;
		if(connection!=null)
		{
			try
			{
				insertColumnCount=1;
				preparedStatement=connection.prepareStatement(query);
				result=true;
			} 
			catch (SQLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			System.err.println("****Connection to database is null****\n# Database.java #");
		}
		return result;
	}
	
	public void addColumnValue(String value)
	{
		if(preparedStatement!=null)
		{
			try 
			{
				preparedStatement.setString(insertColumnCount, value);
				insertColumnCount++;
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.err.println("****Unable to add column value to statement because preparedStatement is null****\n# Database.java #");
		}
	}
	
	public void addColumnValue(int value)
	{
		if(preparedStatement!=null)
		{
			try 
			{
				preparedStatement.setInt(insertColumnCount, value);
				insertColumnCount++;
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.err.println("****Unable to add column value to statement because preparedStatement is null****\n# Database.java #");
		}
	}
	
	public void addColumnValue(long value)
	{
		if(preparedStatement!=null)
		{
			try 
			{
				preparedStatement.setInt(insertColumnCount, Int.safeLongToInt(value));
				insertColumnCount++;
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.err.println("****Unable to add column value to statement because preparedStatement is null****\n# Database.java #");
		}
	}
	
	public void addColumnValue(double value)
	{
		if(preparedStatement!=null)
		{
			try 
			{
				preparedStatement.setDouble(insertColumnCount, value);
				insertColumnCount++;
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.err.println("****Unable to add column value to statement because preparedStatement is null****\n# Database.java #");
		}
	}
	
	public boolean executeInsert()
	{
		boolean result=false;
		if(preparedStatement!=null)
		{
			try 
			{
				preparedStatement.executeUpdate();
			} 
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			System.err.println("****Could not execute insert because preparedStatement is null****\n# Database.java #");
		}
		return result;
	}
	
	public ResultSet runSelectQuery(String query)
	{
		if(connection!=null)
		{
			try 
			{
				preparedStatement=connection.prepareStatement(query);
				return preparedStatement.executeQuery();
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public int runUpdateQuery(String query)
	{
		if(connection!=null)
		{
			try 
			{
				preparedStatement=connection.prepareStatement(query);
				return preparedStatement.executeUpdate();
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
		return -1;
	}
	
	public void close()
	{
		if(preparedStatement!=null)
		{
			try 
			{
				preparedStatement.close();
			}
			catch (SQLException e)
			{
				System.err.println("****SQLEXception thrown****");
				e.printStackTrace();
			}
		}
	}

}
