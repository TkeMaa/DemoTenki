package base;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

	private static final String host = "sql7.freesqldatabase.com";
	private static final String dbName = "sql7730432";
	
	private static final String url = "jdbc:mysql://" + host + ":3306/" + dbName;
	private static final String user = "sql7730432";
	private static final String password = "bzKrAFEIsh";
	
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, user, password);
	}
}
