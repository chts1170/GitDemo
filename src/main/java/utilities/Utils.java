package utilities;

import io.restassured.path.json.JsonPath;


public class Utils {
	
	public static JsonPath rawToJson(String response) {
		return new JsonPath(response);
	}

}
