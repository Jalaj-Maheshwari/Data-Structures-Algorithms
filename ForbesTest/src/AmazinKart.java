package amazinkart;

import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import java.net.MalformedURLException;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileWriter;

import java.text.ParseException;
import java.text.DecimalFormat;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class AmazinKart {
	
	static URL productURL;
	static URL ratesURL;

	static void initializeThirdPartyUrls() {
		try {
			productURL = new URL("https://api.jsonbin.io/b/5d31a1c4536bb970455172ca/latest");
			ratesURL = new URL("https://api.exchangeratesapi.io/latest");	
		} catch (MalformedURLException ex){
			System.out.println(ex);
		}
		
	}	

	static StringBuffer readDataStream(HttpsURLConnection con) {
		StringBuffer content = new StringBuffer();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;			
			while((inputLine = in.readLine()) != null) {
    			content.append(inputLine);
   			}
			in.close();	
		} catch (IOException ex){
			System.out.println(ex);
		}
		return content;
	}

	static HttpsURLConnection establishConnection(URL url) {
		HttpsURLConnection con = null;
		try {
			con = (HttpsURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			// setting request headers			
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			con.setRequestProperty("Content-Type", "application/json");
			String contentType = con.getHeaderField("Content-Type");			
			// configuring session timeouts to 3 seconds 
			con.setConnectTimeout(3000);
			con.setReadTimeout(3000);			
		} catch(IOException ex){
			System.out.println(ex);
		}	
		// returning the connection object post connection setup.
		return con;	
	}

	static void convertToINR(JSONObject currProductObj, JSONObject ratesObj){
		Object productCurrency = currProductObj.get("currency");
		Object euroBasedRateOfProductCurrency = ((JSONObject)ratesObj.get("rates")).get(productCurrency);
		Object euroBasedRateOfINR = ((JSONObject)ratesObj.get("rates")).get("INR");

		double nativeCurrencyRate = (double) euroBasedRateOfProductCurrency;
		double indianRupeesRate = (double) euroBasedRateOfINR; 
		String tempProductPriceInNativeCurrency = currProductObj.get("price").toString(); 
		double productPriceInNativeCurrency = Double.valueOf(tempProductPriceInNativeCurrency);

		double currencyConversionFactor =  indianRupeesRate / nativeCurrencyRate;
		double productPriceInINR =  productPriceInNativeCurrency * currencyConversionFactor;

		//System.out.println("INR Price for product :" + currProductObj.get("product").toString() + " is " + String.valueOf(productPriceInINR));
		currProductObj.put("currency", "INR");
		
		// Rounding double value 'price' to 2 decimals. 
		DecimalFormat df = new DecimalFormat("#.##");
		productPriceInINR = Double.valueOf(df.format(productPriceInINR));

		currProductObj.put("price", productPriceInINR);
		//System.out.println(currProductObj.toString());
	}

	static JSONArray runPromotionSetA(JSONArray products){
		for(int i = 0; i < products.size(); i++){
			JSONObject currProductObj = (JSONObject)products.get(i);
			double productPrice = Double.valueOf(currProductObj.get("price").toString());
			// Keeping the price to double as standard datatype (as mentioned in FAQ's)
			currProductObj.put("price", productPrice);	
			
			JSONObject discount = new JSONObject();
			double discountAmount = 0.0;
			String discountTag = null;
			double discountfromCase1 = 0.0, discountfromCase2 = 0.0, discountfromCase3 = 0.0;
		
			// Applying relevant discounts

			// Case 1
			if(currProductObj.get("origin").equals("Africa")){
				discountfromCase1 = 0.07 * productPrice;
			} 

			// Case 2
			double rating = Double.valueOf(currProductObj.get("rating").toString());
			if(rating == 2.0) {
				discountfromCase2 = 0.04 * productPrice;	
			} else if(rating < 2.0){
				discountfromCase2 = 0.08 * productPrice;
			}	

			// Case 3
			if(currProductObj.get("category").equals("electronics") || currProductObj.get("category").equals("furnishing")){
				if(productPrice >= 500.0){
					discountfromCase3 = 100.0;
				}
			}	

			// Applying Max of 3 discounts
			if(discountfromCase1 > discountfromCase2 && discountfromCase1 > discountfromCase3){
				discountAmount = discountfromCase1;
				discountTag = "Get 7% off";
			} else if(discountfromCase2 > discountfromCase1 && discountfromCase2 > discountfromCase3){
				discountAmount = discountfromCase2;
				if(rating == 2.0) {
					discountTag = "Get 4% off";	
				} else if(rating < 2.0){
					discountTag = "Get 8% off";
				}	
			} else if(discountfromCase3 > discountfromCase2 && discountfromCase3 > discountfromCase2) {
				discountAmount = discountfromCase3;
				discountTag = "Get flat Rs 100 off";
			}

			// Applying default case discount (if applicable) 
			if(discountfromCase1 == 0.0 && discountfromCase2 == 0.0 && discountfromCase3 == 0.0){
				if(productPrice > 1000.0){
					discountAmount = 0.02 * productPrice;
					discountTag = "Get 2% off";
				}
			}
			
			// Attaching discount node if applicable
			if(discountAmount > 0.0 && discountTag != null){
				// Rounding double value 'price' to 2 decimals. 
				DecimalFormat df = new DecimalFormat("#.##");
				discountAmount = Double.valueOf(df.format(discountAmount));
				discount.put("amount", discountAmount);
				discount.put("discountTag", discountTag);
				currProductObj.put("discount", discount);	
			}
			System.out.println("Details of Product: " + i+1);
			System.out.println(currProductObj);
		}
		return products;
	}

	static JSONArray runPromotionSetB(JSONArray products){
		for(int i = 0; i < products.size(); i++){
			JSONObject currProductObj = (JSONObject)products.get(i);
			double productPrice = Double.valueOf(currProductObj.get("price").toString());
			// Keeping the price to double as standard datatype (as mentioned in FAQ's)
			currProductObj.put("price", productPrice);	
			
			JSONObject discount = new JSONObject();
			double discountAmount = 0.0;
			String discountTagB = null;
			double discountfromCase1 = 0.0, discountfromCase2 = 0.0;
		
			// Applying relevant discounts

			// Case 1
			double inventory = Double.valueOf(currProductObj.get("inventory").toString());
			if(inventory > 20.0){
				discountfromCase1 = 0.12 * productPrice;
			} 

			// Case 2
			if(currProductObj.containsKey("arrival") && currProductObj.get("arrival").equals("NEW")){
				discountfromCase2 = 0.07 * productPrice;
			}	

			// Applying Max of 2 discounts
			if(discountfromCase1 > discountfromCase2){
				discountAmount = discountfromCase1;
				discountTagB = "Get 12% off";
			} else {
				discountAmount = discountfromCase2;
				discountTagB = "Get 7% off";
			}

			// Applying default case discount (if applicable) 
			if(discountfromCase1 == 0.0 && discountfromCase2 == 0.0){
				if(productPrice > 1000.0){
					discountAmount = 0.02 * productPrice;
					discountTagB = "Get 2% off";
				}
			}
			
			// Attaching discount node if applicable
			if(discountAmount > 0.0 && discountTagB != null){
				// Rounding double value 'price' to 2 decimals. 
				DecimalFormat df = new DecimalFormat("#.##");
				discountAmount = Double.valueOf(df.format(discountAmount));
				discount.put("amount", discountAmount);
				discount.put("discountTag", discountTagB);
				currProductObj.put("discount", discount);	
			}
			System.out.println("Details of Product: " + i+1);
			System.out.println(currProductObj);
		}
		return products; 
	}

	static void createJSONOutputFile(JSONArray productObj, String setName){
		try (FileWriter file = new FileWriter("outputOfPromotion"+setName+".json")) {
       		file.write(productObj.toJSONString());
       		file.flush();
       	} catch (Exception ex){
       		System.out.println("Unable to create JSON file");
       		System.out.println(ex);
       	}
	}

	static JSONArray getProductDetails(){
		JSONArray products = null;
		try {
			// making a https request for fetching product details
			HttpsURLConnection productConObj  = establishConnection(productURL);
			int statusCode = productConObj.getResponseCode();	
			if(statusCode != 200){
				throw new RuntimeException("Unable to get product data. ResponseCode "+ statusCode);
			} else {
				// Get product details and append it to StringBuffer.
				StringBuffer productContent = readDataStream(productConObj);				
				// TODO: Replace all Sop's with file logging and printing the stack trace for Exceptions. 
				// System.out.println("Product Details : ");
				// System.out.println(productContent);
				// disconnecting the product connection object
				productConObj.disconnect();

				// parsing the product details into JSON array.
				try {
					JSONParser parser = new JSONParser(); 
					products = (JSONArray)(parser.parse(productContent.toString()));
				} catch(Exception ex){
					System.out.println(ex);
					System.out.println("Unable to parse product JSON");
				}	
				
				// Persisting the original JSON response in a file before making any alterations.  
        		try (FileWriter file = new FileWriter("originalProductDetails.json")) {
            		file.write(products.toJSONString());
       				file.flush();
           		} catch (Exception ex){
           			System.out.println("Unable to create JSON file");
           			System.out.println(ex);
           		}

           		// Fetching rates details
           		StringBuffer ratesContent = null;
           		HttpsURLConnection ratesConObj = establishConnection(ratesURL);
				statusCode = ratesConObj.getResponseCode();
				if(statusCode != 200){
					throw new RuntimeException("Unable to get rates data. ResponseCode "+ statusCode);
				} else {
					// Get product details and append it to StringBuffer.
					ratesContent = readDataStream(ratesConObj);				
					// System.out.println("Rate Details : ");
					// System.out.println(ratesContent);
					// disconnecting the product connection object
					ratesConObj.disconnect();	
				}

				// parsing rates data to JSON Object 
				JSONObject ratesObj = null;
				try {
					JSONParser parser = new JSONParser(); 
					ratesObj = (JSONObject) (parser.parse(ratesContent.toString()));
					// System.out.println("Rates Array: " + ratesObj);
				} catch(Exception ex){
					System.out.println(ex);
					System.out.println("Unable to parse rates JSON");
				}

            	// Converting the currencies to INR
            	JSONObject productObj = new JSONObject();
				for(int i = 0; i < products.size(); i++){
					productObj = (JSONObject)(products.get(i));
					// System.out.println("Currency for product " + i);
					// System.out.println(productObj.get("currency"));
					if(!productObj.get("currency").equals("INR")){
						// System.out.println("Converting currency to INR for product " + productObj.get("product"));
						convertToINR(productObj, ratesObj);	
					}
				}
			}	
		} catch(IOException ex) {
				System.out.println("Unable to fetch product details");
				System.out.println(ex);
		}
		// System.out.println("Final Product details: ");
		// System.out.println(products.toString());
		return products;
	}

	public static void main(String[] args){
		System.out.println("Initializing Third Party URLS");
		initializeThirdPartyUrls();

		System.out.println("Fetching product details");
		JSONArray productDetails = getProductDetails();

		System.out.println("Running PromotionSetA");
		JSONArray productsA = runPromotionSetA(productDetails);

		System.out.println("Running PromotionSetB");
		JSONArray productsB = runPromotionSetB(productDetails);

		System.out.println("Creating output JSON File for both Promotion Sets ");
		createJSONOutputFile(productsA, "SetA");
		createJSONOutputFile(productsB, "SetB");
		System.out.println("Output files created");
	}
}