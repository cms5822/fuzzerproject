package example.fuzzer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PageInput {

	private Map<String, Set<String>> queryInputs = new HashMap<String, Set<String>>();
	private Set<String> formInputs = new HashSet<String>();
	
	public boolean addQueryInput(String i, String v){
		if(!queryInputs.containsKey(i)){
			queryInputs.put(i, new HashSet<String>());
		}
		return queryInputs.get(i).add(v);
	}
	
	public boolean addFormInput(String i){
		return formInputs.add(i);
	}
}
