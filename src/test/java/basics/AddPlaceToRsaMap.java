package basics;

import io.restassured.RestAssured;
import resources.JsonPayload;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class AddPlaceToRsaMap {

	public static void main(String[] args) {
		// Add place to RSA Map
		
		// Given  all important data like query parm, body and content-type(header)
		// When -> it has the information related to resources and http method
	    //Then -> validate the response
		
		RestAssured.baseURI = "https://rahulshettyacademy.com";
		given().log().all().queryParam("key", "qaclick123").header("Content-Type", "application/json")
		    .body(JsonPayload.addPlacePayload())
		    .when().post("/maps/api/place/add/json")
		    .then().log().all().assertThat()
		    .statusCode(200)
		    .body("scope", equalTo("APP"))
		    .header("Server", "Apache/2.4.18 (Ubuntu)");
		
		}
}
