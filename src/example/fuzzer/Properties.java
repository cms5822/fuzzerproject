package example.fuzzer;

public class Properties {
	//private static final PageProperties pp = new BodgeitProperties();
	private static final PageProperties pp = new JpetStoreProperties();
	
	public final String urlBase;
	public final String currentPage;
	public final String registerPage;
	public final String loginPage;

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
	
	public static final boolean passwordGuess = true;

	public final String username;
	public final String password;
	public final String loginUserFormField;
	public final String loginPasswordFormField;
	public final String registerUserFormField;
	public final String registerPasswordFormField;
	public final String confirmPasswordFormField;

	public static final String[] easyPasswords = new String[]{
		"password", "god", "admin", "1234", "12345"
	};
	
	public static final String[] fuzzInputs = new String[]{
		"-1", "0", "1", "2", "test", "admin", 
		Integer.toString(Integer.MAX_VALUE), Integer.toString(Integer.MIN_VALUE),
		Long.toString(Long.MAX_VALUE), Long.toString(Long.MIN_VALUE),
		"<script>alert(\"XXS\")</script>"
	};
	
	public Properties(){
		urlBase = pp.baseUrl;
		currentPage = pp.pageUrl;
		registerPage = pp.registerPage;
		loginPage = pp.loginPage;
		username = pp.validUsername;
		password = pp.validPassword;
		loginUserFormField = pp.loginUsernameFormField;
		loginPasswordFormField = pp.loginPasswordFormField;
		registerUserFormField = pp.registerUsernameFormField;
		registerPasswordFormField = pp.registerPasswordFormField1;
		confirmPasswordFormField = pp.registerPasswordFormField2;
		
	}
}
