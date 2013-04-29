package example.fuzzer;

public class JpetStoreProperties extends PageProperties {	
	public JpetStoreProperties(){
		super(
				"http://127.0.0.1:8080",
				"http://127.0.0.1:8080/jpetstore/",
				"http://127.0.0.1:8080/jpetstore/actions/Acount.action?newAccountForm=",
				"http://127.0.0.1:8080/jpetstore/actions/Acount.action",
				
				"j2ee",
				"j2ee",
				
				"username",
				"password",
				"signon",
				
				"username",
				"password",
				"repeatedPassword",
				
				false
		);
	}
}
