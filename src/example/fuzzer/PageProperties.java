package example.fuzzer;

public abstract class PageProperties {
	public final String baseUrl;
	public final String pageUrl;
	public final String registerPage;
	public final String loginPage;
	
	public final String validUsername;
	public final String validPassword;
	
	public final String loginUsernameFormField;
	public final String loginPasswordFormField;
	public final String loginSubmitFormFieldName;
	
	public final String registerUsernameFormField;
	public final String registerPasswordFormField1;
	public final String registerPasswordFormField2; 
	public final boolean authBeforeFuzz;
	public final boolean hasRegisterPage;
	

	/**
	 * 
	 * @param baseUrl
	 * @param pageUrl
	 * @param registerPage
	 * @param loginPage
	 * @param validUsername
	 * @param validPassword
	 * @param loginUsernameFormField
	 * @param loginUsernamePasssword
	 * @param registerUsernameFormField
	 * @param registerPasswordFormField1
	 * @param registerPasswordFormField2
	 */
	public PageProperties(String baseUrl, String pageUrl, String registerPage, String loginPage,  
			String validUsername, String validPassword, String loginUsernameFormField, String loginUsernamePasssword, String loginSubmitFormFieldName,
			String registerUsernameFormField, String registerPasswordFormField1, String registerPasswordFormField2,
			boolean authBeforeFuzz, boolean hasRegisterPage){
		this.baseUrl = baseUrl;
		this.pageUrl = pageUrl;
		this.registerPage = registerPage;
		this.loginPage = loginPage;
		this.validUsername = validUsername;
		this.validPassword = validPassword;
		this.loginUsernameFormField = loginUsernameFormField;
		this.loginPasswordFormField = loginUsernamePasssword;
		this.loginSubmitFormFieldName = loginSubmitFormFieldName;
		this.registerUsernameFormField = registerUsernameFormField;
		this.registerPasswordFormField1 = registerPasswordFormField1;
		this.registerPasswordFormField2 = registerPasswordFormField2;
		this.authBeforeFuzz = authBeforeFuzz;
		this.hasRegisterPage = hasRegisterPage;
	}
}
