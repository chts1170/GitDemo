package resources;

public class JiraJsonPayload {
	
	public static final String addComment() {
		return "{\r\n"
				+ "    \"body\": \"This is the first comment to be posted with Rest Assured.\"\r\n"
				+ "}";
	}
	
	public static final String login() {
		return "{ \"username\": \"lokendra2007\", \"password\": \"Heena1403#\" }";
	}

}
