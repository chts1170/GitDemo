package basics;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.testng.Assert;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import resources.JsonPayload;
import utilities.Utils;

public class E2E {
	public static void main(String[] args) {

		// Given all important data like query parm, body and content-type(header)
		// When -> it has the information related to resources and http method
		// Then -> validate the response

		RestAssured.baseURI = "https://rahulshettyacademy.com";
		String response = given().log().all().queryParam("key", "qaclick123").header("Content-Type", "application/json")
				.body(JsonPayload.addPlacePayload()).when().post("/maps/api/place/add/json").then().assertThat()
				.statusCode(200).body("scope", equalTo("APP")).header("Server", "Apache/2.4.18 (Ubuntu)").extract()
				.asString(); // extract the response.

		System.out.println("Response : " + response);

		JsonPath js = Utils.rawToJson(response);
		String place_id = js.getString("place_id");
		System.out.println("place_id " + place_id);
		System.out.println("status " + js.getString("status"));
		System.out.println("scope " + js.getString("scope"));
		System.out.println("reference " + js.getString("reference"));

		// PUT
		final String newAddress = "71 Summer walk, USA";
		String putResponse = given().log().all().queryParam("key", "qaclick123")
				.header("Content-Type", "application/json")
				.body("{\r\n" + "\"place_id\":\"" + place_id + "\",\r\n" + "\"address\":\"" + newAddress + "\",\r\n"
						+ "\"key\":\"qaclick123\"\r\n" + "}")
				.when().put("maps/api/place/update/json").then().log().all().assertThat().statusCode(200)
				.body("msg", equalTo("Address successfully updated")).extract().asString();

		System.out.println("put response : " + putResponse);

		// GET -> no need to add the header info as there is no body we sendout as request.
		String getResponse = given().log().all().queryParam("key", "qaclick123").queryParam("place_id", place_id)
				.when().get("maps/api/place/get/json")
				.then().log().all().assertThat().statusCode(200).body("address", equalTo(newAddress)).extract().asPrettyString();
		
		System.out.println("GET response : " + getResponse);
		
		// I want to print the latitude and longitude data
		JsonPath getJsonResponse =  Utils.rawToJson(getResponse);
		System.out.println("Latitude : " + getJsonResponse.getString("location.latitude"));
		System.out.println("accuracy : " + getJsonResponse.getString("accuracy"));
		Assert.assertEquals(getJsonResponse.getString("address"), newAddress);

	}
}
