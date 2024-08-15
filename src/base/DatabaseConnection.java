package base;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

	private static final String host = "192.168.100.2";
	private static final String dbName = "sakila";
	
	private static final String url = "jdbc:mysql://" + host + ":3306/" + dbName;
	private static final String user = "remote_user";
	private static final String password = "password";
	
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, user, password);
	}
}
