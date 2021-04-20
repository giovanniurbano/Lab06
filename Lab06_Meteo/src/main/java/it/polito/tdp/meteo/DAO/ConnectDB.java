package it.polito.tdp.meteo.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariDataSource;

public class ConnectDB {
	
	// check user e password
	static private final String jdbcUrl = "jdbc:mysql://localhost/meteo?user=root&password=giovanni";
	static private HikariDataSource ds = null;

	public static Connection getConnection() {
		if(ds == null) {
			ds = new HikariDataSource();
			ds.setJdbcUrl(jdbcUrl);
			//ds.setUsername("root");
			//ds.setPassword("giovanni");
		}

		try {
			Connection connection = DriverManager.getConnection(jdbcUrl);
			return connection;
		} 
		catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot get a connection " + jdbcUrl, e);
		}
	}

}
