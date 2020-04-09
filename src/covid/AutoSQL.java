package covid;

import java.sql.Connection;
import java.sql.DriverManager;

public class AutoSQL {
	public static void main(String [] args) throws Exception {
		getConnection();
	}

	public static Connection getConnection() throws Exception {
		try {
			String driver = "com.mysql.cj.jdbc.Driver";
			String url = "jdbc:mysql://us-cdbr-iron-east-01.cleardb.net:3306/heroku_19b03b0809f13fb";
			String username = "b866a37b86209d";
			String password = "e9ebe2e9";
			Class.forName(driver);

			Connection conn = DriverManager.getConnection(url, username, password);
			System.out.println("Connected");
			return conn;
		} catch (Exception e) {
			System.out.println(e);
		}

		return null;
	}

}
