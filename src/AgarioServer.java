import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class AgarioServer {
	
	public static final String NorthAmerica = "US-Atlanta";
	public static final String SouthAmerica = "BR-Brazil";
	public static final String Europe = "EU-London";
	public static final String Russia = "RU-Russia";
	public static final String Turkey = "TK-Turkey";
	public static final String EastAsia = "JP-Tokyo";
	public static final String China = "CN-China";
	public static final String Oceania = "SG-Singapore";
	
	public String uri;
	public String token;

	public AgarioServer(String region) throws IOException, ParseException {
		// Construct request
		String parameters = region + "\n2200049715";
		byte[] data = parameters.getBytes(StandardCharsets.UTF_8);
		URL url = new URL("http://m.agar.io/findServer");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("charset", "utf-8");
		conn.setRequestProperty("Content-Length", Integer.toString(data.length));
		try(DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
			out.write(data);
		}
		
		// Read response
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
		String response = "";
		for (int c = in.read(); c != -1; c = in.read())
			response += (char)c;
		
		// Parse data from response
		JSONObject server = (JSONObject)(new JSONParser()).parse(response);
		uri = "ws://" + (String) server.get("ip");
		token = (String) server.get("token");
	}
}
