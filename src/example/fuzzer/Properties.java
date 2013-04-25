package example.fuzzer;

public class Properties {
	//public static final String webPage = "http://192.168.1.131:8080/bodgeit/";
	public static final String urlBase = "http://129.21.12.177:8080/";
	public static final String bodgeit = urlBase + "bodgeit/";
	public static final String dvwa = urlBase + "/";
	public static final String jpetstore = urlBase + "jpetstore/";
	
	public static final String[] secretPages = new String[]{
		"admin", "secret"
	};
	
	public static final String[] pageEndings = new String[]{
		".html", ".jsp", ".php", ".asp", "", "/"
	};
	
	// This value is in seconds
	public static final long timeGap = 0;
	
	//public static final String completeness = "random";
	public static final String completeness = "full";
	
	public static final boolean passwordGuess = false;

	public static final String username = "username";
	public static final String userFormField = "username";
	public static final String password = "password";
	public static final String passwordFormField = "password";

	public static final String testUserName = "test@test.com";
	public static final String[] easyPasswords = new String[]{
		"password", "god", "admin", "1234", "12345"
	};
}
