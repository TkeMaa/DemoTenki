package base;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

	private static final String host = "localhost";
	private static final String dbName = "sys";
	
	private static final String url = "jdbc:mysql://" + host + ":3306/" + dbName;
	private static final String user = "root";
	private static final String password = "Matijappro1328";
	
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, user, password);
	}
}
