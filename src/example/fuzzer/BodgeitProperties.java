package example.fuzzer;

public class BodgeitProperties extends PageProperties {
	
	public BodgeitProperties(){
		super(
				"http://127.0.0.1:8080",
				"http://127.0.0.1:8080/bodgeit/",
				"http://127.0.0.1:8080/bodgeit/register.jsp",
				"http://127.0.0.1:8080/bodgeit/login.jsp",
				
				"username",
				"password",
				
				"username",
				"password",
				"submit",
				
				"username",
				"password1",
				"password2",
				
				false,
				true
		);
	}
}
