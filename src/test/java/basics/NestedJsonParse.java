package basics;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import resources.JsonPayload;
import utilities.Utils;

public class NestedJsonParse {

	
	private JsonPath jsonApiResponse;
	private int numOfCourses;
	private int purchaseAmount;

	@BeforeTest
	public void mockJsonResponse() {
		jsonApiResponse = Utils.rawToJson(JsonPayload.coursePrice());
	}

	@Test(priority = 1)
	public void numOfCourses() {
		numOfCourses = jsonApiResponse.getInt("courses.size()");
		System.out.println("Num of courses : " + numOfCourses);
		Assert.assertEquals(numOfCourses, 3);
	}
	
	@Test(priority = 2)
	public void validatePurchaseAmount() {
		purchaseAmount = jsonApiResponse.getInt("dashboard.purchaseAmount");
		System.out.println("Purchase amount : " + purchaseAmount);
		Assert.assertEquals(purchaseAmount, 910);
	}

	@Test
	public void titleOfFirstCourse() {
		String firstCourseTitle = jsonApiResponse.getString("courses[0].title");
		System.out.println("firstCourseTitle : " + firstCourseTitle);
		Assert.assertEquals(firstCourseTitle, "Selenium Python");
	}
	
	@Test(dependsOnMethods = "numOfCourses")
	public void allCourseTitles() {
		for (int i = 0; i < numOfCourses; i++) {
			String courseTitle = jsonApiResponse.getString("courses[" + i + "].title");
			System.out.println(courseTitle);
		}
	}
	
	@Test(dependsOnMethods = "numOfCourses")
	public void NumOfCopiesSoldForRpaCourse() {
		for (int i = 0; i < numOfCourses; i++) {
			String courseTitle = jsonApiResponse.getString("courses[" + i + "].title");
			if (courseTitle.equals("RPA")) {
				int numOfCopies = jsonApiResponse.getInt("courses[" + i + "].copies"); 
				System.out.println("Number of copies for RPA : " +  numOfCopies);
			}
		}

	}

	@Test(dependsOnMethods = {"numOfCourses", "validatePurchaseAmount"})
	public void sumOfAllCourses() {
		//6. Verify if Sum of all Course prices matches with Purchase Amount
	List<Integer> sumOfCourses = new ArrayList<>();
	for (int i = 0; i < numOfCourses; i++) {
		int price = jsonApiResponse.getInt("courses[" + i + "].price");
		int copies = jsonApiResponse.getInt("courses[" + i + "].copies");

		sumOfCourses.add(price*copies);
	}
	
	int totalSum = sumOfCourses.stream().mapToInt(Integer::valueOf).sum();
	System.out.println("Total sum is : " +  totalSum);
	Assert.assertEquals(totalSum, purchaseAmount);

	}

}
