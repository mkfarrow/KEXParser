import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.Scanner;
import com.json.parsers.JSONParser;
import com.json.parsers.JsonParserFactory;

/**
 * Dumb little script to convert from the JSON version of the output to the tab-separated version.
 * This only exists because I ran the Runner/Crawler with the JSON output format initially and
 * doing this conversion is much faster than re-running the crawlers with a new output form. Having
 * the JSON data is nice too because it is more flexible and share-able.
 */
public class JsonToTabs {
	
	public static final String PLAYS_FILE = "playLog.txt";
	public static final String OUT_FILE = "tabSeparated.txt";
	
	public static void main(String[] args) throws FileNotFoundException {
		JsonParserFactory factory = JsonParserFactory.getInstance();
		JSONParser parser = factory.newJsonParser();
		
		Scanner s = new Scanner(new File(PLAYS_FILE));
		PrintStream out = new PrintStream(new FileOutputStream(OUT_FILE));
		while (s.hasNextLine()) {
			StringBuilder sb = new StringBuilder();
			sb.append(s.nextLine()).append("\n"). //brace
			   append(s.nextLine()).append("\n"). //artist
			   append(s.nextLine()).append("\n"). //track
   		       append(s.nextLine()).append("\n"). //album
	   		   append(s.nextLine()).append("\n"). //year
			   append(s.nextLine()).append("\n"). //label
		 	   append(s.nextLine()).append("\n"). //showName
			   append(s.nextLine()).append("\n"). //tagline
			   append(s.nextLine()).append("\n"). //dj
			   append(s.nextLine()).append("\n"). //playtime
			   append(s.nextLine()); //brace
			s.nextLine(); // blank line		
			
			String jsonString = escapeAllQuotes(sb.toString());
			@SuppressWarnings("rawtypes") // ahhhhh noooo raw types!!!
			Map jsonData = null;
			try {
				jsonData = parser.parseJson(jsonString);
			} catch (Exception e) {
				System.err.println(jsonString);
				continue;
			}
			StringBuilder outputSb = new StringBuilder();
			outputSb.append(tabify((String) jsonData.get("artist"))).  append("\t").
			         append(tabify((String) jsonData.get("track"))).   append("\t").
			         append(tabify((String) jsonData.get("album"))).   append("\t").
			         append(tabify((String) jsonData.get("year"))).    append("\t").
			         append(tabify((String) jsonData.get("label"))).   append("\t").
			         append(tabify((String) jsonData.get("showName"))).append("\t").
			         append(tabify((String) jsonData.get("tagline"))). append("\t").
			         append(tabify((String) jsonData.get("dj"))).      append("\t").
			         append(tabify((String) jsonData.get("playTime")));
			
			String output = outputSb.toString();
			out.println(output);
		}
	}
	
	/**
	 * assumes that this is not a valid JSON object insofar as the " character is
	 * not escaped as \"
	 * 
	 * @param json the object to escape
	 * @return an escaped version of json
	 */
	public static String escapeAllQuotes(String json) {
		String[] parts = json.split("\n");
		StringBuilder sb = new StringBuilder();
		for (String s : parts) {
			int qCount = 0;
			int lastQ = s.lastIndexOf('\"');
			for (int i = 0; i < s.length(); i++) {
				if (s.charAt(i) == '\"')
					qCount++;
				if (qCount > 3 && i != lastQ && s.charAt(i) == '\"') {
					sb.append("'");
				} else {
					sb.append(s.charAt(i));
				}	
			}
			sb.append("\n");
		}

		return sb.toString();
	}
	
	public static String tabify(String s) {
		return s == null || s.equals("null") ? "\\N" : s;
	}
}
