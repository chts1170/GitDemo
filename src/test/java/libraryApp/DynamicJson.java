package libraryApp;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import resources.JsonPayload;
import utilities.Utils;

import static io.restassured.RestAssured.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DynamicJson {
	private static final String BASE_URI = "http://216.10.245.166";

	@Test(dataProvider = "getData")
	public void addBookWithDataProvider(String isbn, int aisle) throws IOException {
		RestAssured.baseURI = BASE_URI;

		String postResponse = given().log().all().header("Content-Type", "application/json")
				.body(JsonPayload.addBook(isbn, aisle)).when().post("Library/Addbook.php").then().log().all()
				.assertThat().statusCode(200).extract().asString();

		JsonPath js = Utils.rawToJson(postResponse);
		String id = js.getString("ID");
		Assert.assertEquals(id, isbn + String.valueOf(aisle));
	}
	
	@Test(dataProvider = "getData")
	public void getBook(String isbn, int aisle) throws IOException {
		RestAssured.baseURI = BASE_URI;

		String getResponse = given().log().all().queryParam("ID", isbn + String.valueOf(aisle)).when()
				.post("/Library/GetBook.php").then().log().all().assertThat().statusCode(200).extract().asString();

		System.out.println("GetResponse : " + getResponse);
		JsonPath js = Utils.rawToJson(getResponse);
		Assert.assertEquals(js.getString("isbn[0]"), isbn);
	}

	@Test()
	public void addBook() throws IOException {
		RestAssured.baseURI = BASE_URI;

		System.out
				.println(System.getProperty("user.dir") + "\\src\\main\\java\\resources\\libraryApiAddBook.json");
		String postResponse = given().log().all().header("Content-Type", "application/json")
				.body(new String(Files.readAllBytes(Paths
						.get(System.getProperty("user.dir") + "\\src\\main\\java\\resources\\libraryApiAddBook.json"))))
				.when().post("Library/Addbook.php").then().log().all().assertThat().statusCode(200).extract()
				.asString();

		System.out.println("Post Response : " + postResponse);
		JsonPath js = Utils.rawToJson(postResponse);
		Assert.assertEquals(js.getString("Msg"), "successfully added");
	}

	@Test(dataProvider = "getData")
	public void deleteBook(String isbn, int aisle) {
		RestAssured.baseURI = BASE_URI;
		String deleteResponse = given().log().all().body(JsonPayload.deleteBook(isbn + String.valueOf(aisle))).when()
				.delete("/Library/DeleteBook.php").then().log().all().assertThat().statusCode(200).extract().asString();

		System.out.println("Response : " + deleteResponse);
	}

	@DataProvider
	private Object[][] getData() {
		Object[][] testData = new Object[3][2];

		testData[0][0] = "qwerty";
		testData[0][1] = 83;

		testData[1][0] = "asdfg";
		testData[1][1] = 3556;

		testData[2][0] = "zxcvb";
		testData[2][1] = 9990;

		return testData;
	}

}
