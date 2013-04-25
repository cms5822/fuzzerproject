package example.fuzzer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.util.Cookie;

public class PageInput {

	private final String name;
	private Map<String, Set<String>> queryInputs = new HashMap<String, Set<String>>();
	private Set<HtmlInput> formInputs = new HashSet<HtmlInput>();
	private Set<String> formInputIds = new HashSet<String>();
	
	private Set<Cookie> cookies = new HashSet<Cookie>();
	
	public PageInput(String page){
		name = page;
	}
	
	public boolean addQueryInput(String i, String v){
		if(!queryInputs.containsKey(i)){
			queryInputs.put(i, new HashSet<String>());
		}
		return queryInputs.get(i).add(v);
	}
	
	public boolean addFormInput(HtmlInput i){
		formInputIds.add(i.getId());
		return formInputs.add(i);
	}

	public boolean addAllFormInput(List<HtmlInput> formInputs) {
		for (HtmlInput hi : formInputs){
			addFormInput(hi);
		}
		return this.formInputs.addAll(formInputs);
	}
	
	public void addCookies(Set<Cookie> cookies2) {
		cookies.addAll(cookies2);
	}
	
	public String toString(){
		return name + "\n" + 
				"\tQueries: " + queryInputs.keySet().toString() + "\n" +
				"\tValues: " + queryInputs.values().toString() + "\n" +
				"\tForms: " + formInputs.toString() + "\n" +
				"\tCookies: " + cookies.toString();
	}
	
	
	public Set<String> getQueryParams(){
		return queryInputs.keySet();
	}
	
	public Set<String> getFormInputIds(){
		return formInputIds;
	}

	
}
