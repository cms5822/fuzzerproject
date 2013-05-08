package example.fuzzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Properties {
	//private static final PageProperties pp = new BodgeitProperties();
	//private static final PageProperties pp = new JpetStoreProperties();
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
	public final String loginSubmitFormField;
	public final String registerUserFormField;
	public final String registerPasswordFormField;
	public final String confirmPasswordFormField;
	public final boolean authBeforeFuzz;
	public final String sensitiveDataFile;
	public final String sanitizeDataFile;
	public final String externalFuzzInputs;
	public final boolean hasRegsiterPage;
	
	public static final String[] easyPasswords = new String[]{
		"password", "god", "admin", "1234", "12345"
	};
	
	public static final List<String> fuzzInputs = new ArrayList<String>();

	
	public Properties(){
		urlBase = pp.baseUrl;
		currentPage = pp.pageUrl;
		registerPage = pp.registerPage;
		loginPage = pp.loginPage;
		username = pp.validUsername;
		password = pp.validPassword;
		loginUserFormField = pp.loginUsernameFormField;
		loginPasswordFormField = pp.loginPasswordFormField;
		loginSubmitFormField = pp.loginSubmitFormFieldName;
		registerUserFormField = pp.registerUsernameFormField;
		registerPasswordFormField = pp.registerPasswordFormField1;
		confirmPasswordFormField = pp.registerPasswordFormField2;
		authBeforeFuzz = pp.authBeforeFuzz;
		sensitiveDataFile = "sensitivedata.txt";
		sanitizeDataFile = "sanitizeddata.txt";
		externalFuzzInputs = "externalfuzzinputs.txt";
		hasRegsiterPage = pp.hasRegisterPage;		
	}
	
	public void appenExternalFuzzInputs(){
		try {
			Scanner input = new Scanner(new File(externalFuzzInputs));
			while (input.hasNext()) {
				fuzzInputs.add(input.nextLine());
			}
		} catch (FileNotFoundException e) {
			System.err.println("Properties: " + e.getMessage());
		}
	}
	
	public List<String> getSanitizedData() {
		List<String> data = new ArrayList<String>();
		
		try {
			Scanner input = new Scanner(new File(sanitizeDataFile));
			while (input.hasNext()) {
				data.add(input.nextLine());
			}
		} catch (FileNotFoundException e) {
			System.err.println("Properties: " + e.getMessage());
		}
		return data;
	}
	
	public List<String> getSensitiveData() {
		List<String> data = new ArrayList<String>();
		
		try {
			Scanner input = new Scanner(new File(sensitiveDataFile));
			while (input.hasNext()) {
				data.add(input.nextLine());
			}
		} catch (FileNotFoundException e) {
			System.err.println("Properties: " + e.getMessage());
		}
		return data;		
	}
}
