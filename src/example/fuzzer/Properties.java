package example.fuzzer;

public class Properties {
	//public static final String webPage = "http://192.168.1.131:8080/bodgeit/";
	public static final String webPage = "http://129.21.12.156:8080/bodgeit/";
	
	public static final String[] secretPages = new String[]{
		"admin", "secret"
	};
	
	public static final String[] pageEndsing = new String[]{
		".html", ".jsp", ".php", ".asp", "", "/"
	};
	
	public static final long timeGap = 2;
	
	//public static final String completeness = "random";
	public static final String completeness = "full";
	
	public static final boolean passwordGuess = false;
}
