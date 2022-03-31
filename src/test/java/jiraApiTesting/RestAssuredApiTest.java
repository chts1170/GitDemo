package jiraApiTesting;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;
import io.restassured.path.json.JsonPath;
import resources.JiraJsonPayload;
import utilities.Utils;

import static io.restassured.RestAssured.*;

import java.io.File;
import java.util.Arrays;

public class RestAssuredApiTest {
	
	private static final String BASE_URI = "http://localhost:8080";
	private String cookie;
	private SessionFilter session;
	private static final String issueKey = "REST-5";
	
	@BeforeTest
	public void createSession() {
		RestAssured.baseURI = BASE_URI;
		session = new SessionFilter();
		
		String response = given().log().all().header("Content-Type", "application/json").body(JiraJsonPayload.login()).filter(session)
				//.relaxedHTTPSValidation() // to handle https validation
				.when().post("/rest/auth/1/session").then().log().all().assertThat().statusCode(200).extract()
				.asString();

		JsonPath js = Utils.rawToJson(response);
		cookie = js.getString("session.name") + "=" + js.getString("session.value");
		System.out.println("Cookie : " + cookie);
		
	}

	@Test
	public void addComment() {
		RestAssured.baseURI = BASE_URI;

		String addCommentResponse = given().log().all().header("Content-Type", "application/json")
				//.header("Cookie", "JSESSIONID=0C87A5887C093DA642CB41B61C261674")
				//.header("Cookie", cookie)
				.pathParam("key", issueKey) // resource path parameter
				.body(JiraJsonPayload.addComment())
				.filter(session)
				.when().post("/rest/api/2/issue/{key}/comment")
				.then().log().all().assertThat().statusCode(201).extract().asPrettyString();
		
		JsonPath js = Utils.rawToJson(addCommentResponse);
		Assert.assertEquals(js.getString("body"), "This is the first comment to be posted with Rest Assured.");
	
	}
	
	@Test
	public void addAttachment() {
		RestAssured.baseURI = BASE_URI;
		/*
		 * /rest/api/2/issue/{issueIdOrKey}/attachments
		 */
		
		String attachment = given().log().all()
				.header("X-Atlassian-Token", "no-check")
				.pathParam("key", issueKey) // resource path parameter
				.filter(session)
				.multiPart("file", new File("D:\\RestApi\\ApiLearning\\src\\main\\java\\resources\\attachment.txt"))
				.header("Content-Type", "multipart/form-data")
				.when().post("/rest/api/2/issue/{key}/attachments")
				.then().log().all().assertThat().statusCode(200).extract().asString();

		JsonPath js = Utils.rawToJson(attachment);
		Assert.assertEquals(js.getString("filename[0]"), "attachment.txt"); 
	}
	
	@Test
	public void getIssue() {
		RestAssured.baseURI = BASE_URI;

		/*
		 * GET /rest/api/2/issue/{issueIdOrKey}
		 */
		
		String response = given().log().all().filter(session).pathParam("key", issueKey)
				//.queryParam("fields", "comment") // limit the response to get only comment field information
				.queryParam("fields", Arrays.asList("comment", "priority")) // limit the response to few fields
				.when().get("/rest/api/2/issue/{key}")
				.then().log().all().statusCode(200).extract().asPrettyString();

		System.out.println(response);
		
		JsonPath js = Utils.rawToJson(response);
		int commentsBlockSize = js.getInt("fields.comment.comments.size()");
		
		for (int i = 0; i < commentsBlockSize; i++) {
			System.out.println("Comment : " + (i + 1));
			System.out.println(js.getString("fields.comment.comments[" + i + "].body"));
		}
	}
}
