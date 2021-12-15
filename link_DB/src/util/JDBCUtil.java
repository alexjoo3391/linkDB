package util;

import java.sql.Connection;
import java.sql.DriverManager;

public class JDBCUtil {
	public Connection getConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception e) {
			e.printStackTrace();
		}

//		String connectionString = "jdbc:mysql://www.yydhsoft.com/skills09";
		String connectionString = "jdbc:mysql://localhost/link_db";
//		String userId = "skills09";
		String userId = "root";
//		String password = "0702";
		String password = "";

		Connection con = null;
		try {
			con = DriverManager.getConnection(connectionString, userId, password);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return con;

	}
}
