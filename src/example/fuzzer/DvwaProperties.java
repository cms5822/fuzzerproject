package example.fuzzer;

import example.fuzzer.PageProperties;

public class DvwaProperties extends PageProperties {

	public DvwaProperties(){
		super(
			"http://129.21.12.221", 
			"http://129.21.12.221/dvwa/",
			"registerPage", 
			"http://129.21.12.221/dvwa/login.php", 
			
			"admin", 
			"password",
			
			"username", 
			"password",
			"Login", 
			
			"registerUsernameFormField",
			"registerPasswordFormField1", 
			"registerPasswordFormField2",
			
			true
		);
	}

}
