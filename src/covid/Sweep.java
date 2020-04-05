package covid;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Sweep {

	public static void main(String args[]) throws IOException {
			Document doc = Jsoup.connect("https://www.michigan.gov/coronavirus/0,9753,7-406-98163_98173---,00.html").get();
			Element table = doc.select("table").get(0);
			Elements rows = table.select("tr");
			Elements cols = rows.select("td");
			String[] input = cols.toString().split("</td>");
			ArrayList<String> data = new ArrayList<String>();
			for(int i = 3; i<input.length;i++) {
				data.add(input[i].substring(input[i].indexOf(">")+1));
			}
			ArrayList<String> county = new ArrayList<String>();
			ArrayList<Integer> infected = new ArrayList<Integer>();
			ArrayList<Integer> deaths = new ArrayList<Integer>();
			int count = 1;
			for(int i = 0; i<data.size();i++) {	
			if(count==1) {
				county.add(data.get(i).substring(data.get(i).indexOf(";")+1));
				count++;
			}
			else if(count==2) {
				try {
					infected.add(Integer.parseInt(data.get(i)));
				}
				catch(NumberFormatException e) {
					infected.add(new Integer(0));
				}
				count++;
			}
			else if(count==3) {
				try {
					deaths.add(Integer.parseInt(data.get(i)));
				}
				catch(NumberFormatException e) {
					deaths.add(new Integer(0));
				}
				count = 1;
			}
			}
			 String dateFormatted = java.time.LocalDate.now().toString();
			 try {
			      File myObj = new File("COVID_"+dateFormatted+".txt");
			      if (myObj.createNewFile()) {
			        System.out.println("File created: " + myObj.getName());
			      } else {
			        System.out.println("File already exists.");
			      }
			    } catch (IOException e) {
			      System.out.println("An error occurred.");
			      e.printStackTrace();
			    }
			 try {
			      FileWriter myWriter = new FileWriter("COVID_"+dateFormatted+".txt");
			      for(int i = 0; i<county.size()-1;i++) {
			    	  myWriter.write(county.get(i)+", "+infected.get(i)+", "+deaths.get(i)+"\n");
			      }
			      myWriter.close();
			      System.out.println("Successfully wrote to the file.");
			    } catch (IOException e) {
			      System.out.println("An error occurred.");
			      e.printStackTrace();
			    }
	}
}