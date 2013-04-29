package example.fuzzer;

import example.fuzzer.PageProperties;

public class DvwaProperties extends PageProperties {

	public DvwaProperties(){
		super(
			"http://127.0.0.1", 
			"http://127.0.0.1/dvwa/",
			"registerPage", 
			"http://127.0.0.1/dvwa/login.php", 
			
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
