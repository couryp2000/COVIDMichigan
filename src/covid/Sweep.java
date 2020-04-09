package covid;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Sweep {

	public static void main(String args[]) throws IOException {
		Document doc = Jsoup.connect("https://www.michigan.gov/coronavirus/0,9753,7-406-98163_98173---,00.html").get();
		Element table = doc.select("table").get(0);
		Elements rows = table.select("tr");
		Elements cols = rows.select("td");
		String[] input = cols.toString().split("</td>");
		ArrayList<String> data = new ArrayList<String>();
		for (int i = 3; i < input.length; i++) {
			data.add(input[i].substring(input[i].indexOf(">") + 1));
		}
		ArrayList<String> county = new ArrayList<String>();
		ArrayList<Integer> infected = new ArrayList<Integer>();
		ArrayList<Integer> deaths = new ArrayList<Integer>();
		int count = 1;
		for (int i = 0; i < data.size(); i++) {
			if (count == 1) {
				county.add(data.get(i).substring(data.get(i).indexOf(";") + 1));
				count++;
			} else if (count == 2) {
				try {
					infected.add(Integer.parseInt(data.get(i)));
				} catch (NumberFormatException e) {
					infected.add(new Integer(0));
				}
				count++;
			} else if (count == 3) {
				try {
					deaths.add(Integer.parseInt(data.get(i)));
				} catch (NumberFormatException e) {
					deaths.add(new Integer(0));
				}
				count = 1;
			}
		}
		String dateFormatted = java.time.LocalDate.now().toString();
		boolean isNewFile = false;
		try {
			File myObj = new File("COVID_" + dateFormatted + ".txt");
			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
				isNewFile = true;
			} else {
				System.out.println("File already exists.");
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		
		insertToDatabase(county, infected, deaths, dateFormatted, isNewFile); 
		
		try {
			FileWriter myWriter = new FileWriter("COVID_" + dateFormatted + ".csv");
			for (int i = 0; i < county.size() - 1; i++) {
				myWriter.write(
						dateFormatted + ", " + county.get(i) + ", " + infected.get(i) + ", " + deaths.get(i) + "\n");
			}
			myWriter.close();
			System.out.println("Successfully wrote to the file.");
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	public static void insertToDatabase(ArrayList<String> county, ArrayList<Integer> infected,
			ArrayList<Integer> deaths, String dateFormatted, boolean isNewFile) {
		if (isNewFile) {
			Connection myConn = null;
			PreparedStatement myStmt = null;

			try {
				myConn = getConnection();
				String sql = "INSERT INTO covid2020 " + " (thedate, county, infections, deaths)" + " VALUES (?,?,?,?)";
				myStmt = myConn.prepareStatement(sql);
				for (int i = 0; i < county.size() - 1; i++) {
					myStmt.setString(1, dateFormatted);
					myStmt.setString(2, county.get(i));
					myStmt.setInt(3, infected.get(i));
					myStmt.setInt(4, deaths.get(i));
					myStmt.addBatch();

					if (i % 100 == 0 || i == (county.size() - 1))
						myStmt.executeBatch();
				}

			} catch (Exception e) {
				System.out.println(e);
			}
		}
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