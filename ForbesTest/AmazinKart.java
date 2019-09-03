//import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import java.net.HttpCookie;
import java.net.CookieManager;
//import java.net.Optional;

import java.util.List;
//import java.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

//import.java.util.*;

public class AmazinKart {
	static void getProductDetails(){
		try {
			// making a https request
			URL url = new URL("https://api.jsonbin.io/b/5d31a1c4536bb970455172ca/latest");
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			
			// setting request headers
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			con.setRequestProperty("Content-Type", "application/json");
			String contentType = con.getHeaderField("Content-Type");
			
			// configuring session timeouts to 3 seconds 
			con.setConnectTimeout(3000);
			con.setReadTimeout(3000);
			
			// Handling cookies 
			String cookiesHeader = con.getHeaderField("Set-Cookie");
			List<HttpCookie> cookies = HttpCookie.parse(cookiesHeader);
			CookieManager cookieManager = new CookieManager();
			cookies.forEach(cookie -> cookieManager.getCookieStore().add(null, cookie));

			/*
			// Adding cookies to request 
			con.disconnect();
			con = (HttpsURLConnection) url.openConnection();
			con.setRequestProperty("Cookie", StringUtils.join(cookieManager.getCookieStore().getCookies(), ";"));
			*/

			// Fetching product details and append it to StringBuffer.
			int status = con.getResponseCode();
			System.out.println(" Status Code: " + status);
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			//System.out.println("");
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
    			content.append(inputLine);
			}
			in.close();

			System.out.println("Product Details : ");
			System.out.println(content);

			// parsing the string type product details into json object. 
			JSONParser parser = new JSONParser(); 
			JSONObject jobj = (JSONObject)parser.parse(content);

			//Writing the product details into a JSON File. 
        	/*
        	try (FileWriter file = new FileWriter("output.json")) {
 
            	file.write(content.toJSONString());
       			file.flush();
            }
            */	
			con.disconnect();	
		}
		catch(Exception e){
			System.out.println("Unable to fetch product details");
		}
			
	}
	public static void main(String[] args){
		System.out.println("Fetching product details");
		getProductDetails();
	}
}