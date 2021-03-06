package example.fuzzer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

public class BasicFuzzer {

	// Map of page url to inputs (query params and form fields)
	private static final Map<String, PageInput> pagesParams = new HashMap<String, PageInput>();
	private static final Properties properties = new Properties();
	private static final String currentPage = properties.currentPage;
	
	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException, URISyntaxException{
		TimedWebClient webClient = new TimedWebClient();
		webClient.setJavaScriptEnabled(true);

		boolean authenticated = false;

		if(properties.authBeforeFuzz){
			testAuthentication(webClient, properties.loginPage);
			authenticated = true;
		}
		
		discoverLinks(webClient, currentPage);
		System.out.println("Done finding links");
		discoverPages(webClient, currentPage);
		System.out.println("Done finding secret pages");
		if(!authenticated){
			testAuthentication(webClient, properties.loginPage);
		}
		if(properties.hasRegsiterPage && properties.passwordGuess){
			boolean easyPasswords = allowsEasyPasswords(webClient, properties.registerPage);
			System.out.println("Easy to guess passowrds are" + (easyPasswords ? " " : " not ") + "allowed.");
		}
		
		System.out.println("Attack Surface:");
		for(String s : pagesParams.keySet()){
			System.out.println(pagesParams.get(s));
		}
		
		//fuzzQueryInputs(webClient, Properties.urlBase);
		System.out.println("Fuzzing form inputs");
		fuzzFormInputs(webClient, properties.urlBase);
		
		webClient.closeAllWindows();
		System.out.println("Done");
		
	}


	/**
	 * This code is for showing how you can get all the links on a given page, and visit a given URL
	 * @param webClient
	 * @throws IOException
	 * @throws MalformedURLException
	 * @throws URISyntaxException 
	 */
	private static void discoverLinks(WebClient webClient, String webPage) throws IOException, MalformedURLException, URISyntaxException{
		HtmlPage page = null;
		try{
			page = webClient.getPage(webPage);
		} catch(FailingHttpStatusCodeException e){
			return;
		}
		List<HtmlAnchor> links = page.getAnchors();
		
		for (HtmlAnchor link : links) {
			boolean newPage = false;
			//System.out.println("Link discovered: " + link.asText() + " @URL=" + link.getHrefAttribute());
			
			URI uri = null;
			// check for absolute or relative path
			try{
				if(link.getHrefAttribute().startsWith("/")){	
					uri = new URI(properties.urlBase + link.getHrefAttribute()).normalize();
				}else{
					uri = new URI(currentPage + link.getHrefAttribute()).normalize();
				}
			}catch(Exception e){
				continue;
			}
			if(!uri.toString().startsWith(currentPage)){
				continue;
			}
			if (!pagesParams.containsKey(uri.getPath())){
				pagesParams.put(uri.getPath(), new PageInput(uri.getPath()));
			}
			
			if(uri.getQuery() != null){
				String[] queryAry = uri.getQuery().split("=");
				String param = queryAry[0];
				String val = "";
				if(queryAry.length >= 2){
					val = queryAry[1];
				}
				newPage = pagesParams.get(uri.getPath()).addQueryInput(param, val);
				if(newPage){
					//System.out.println("Adding page " + uri.getPath() + " with query " + param + " value " + val);
					discoverLinks(webClient, uri.toString());
					discoverForms(webClient, webPage);
					pagesParams.get(uri.getPath()).addCookies(webClient.getCookieManager().getCookies());
					
				}
			}
			else{
				newPage = pagesParams.get(uri.getPath()).addQueryInput(null, null);
				if(newPage){
					//System.out.println("Adding page " + uri.getPath() + " with query " + null);
					discoverLinks(webClient, uri.toString());
					discoverForms(webClient, webPage);
					pagesParams.get(uri.getPath()).addCookies(webClient.getCookieManager().getCookies());
					
				}
			}
			
		}
	}
	/**
	 * This code attempts to guess any secret pages a site may have.
	 * @param webClient
	 * @param webPage
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private static void discoverPages(WebClient webClient, String webPage) throws IOException, MalformedURLException{
		for (String secretURL : Properties.secretPages){
			for(String extension : Properties.pageEndings){
				try{
					HtmlPage page = webClient.getPage(webPage+secretURL+extension);
					System.out.println("URL-Discovery: Secret URL found " + webPage+secretURL+extension);
					if(!pagesParams.containsKey(new URL(webPage).getPath() + secretURL+extension)){
						pagesParams.put(new URL(webPage).getPath() + secretURL+extension, 
								new PageInput(new URL(webPage).getPath() + secretURL+extension));
					}
					// Some way of reporting improper data
				}
				catch (FailingHttpStatusCodeException e) {
					//Url does not work
					//System.out.println("URL-Discovery: Url not valid " + webPage + secretURL + extension);
				} catch (MalformedURLException e) {
					//Invalid url in file
					//System.err.println("URL-Discovery: Invalid url in secret page file " + secretURL + extension);
				} catch (IOException e) {
					//Error
					//System.err.println("URL-Discovery: " + e.getMessage());
				}
			}
		}
	}

	private static void discoverForms(WebClient webClient, String webPage) throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		HtmlPage page = webClient.getPage(webPage);
		String basePage = new URL(webPage).getPath();

		List<HtmlInput> formInputs = new ArrayList<HtmlInput>();
		for (HtmlForm form : page.getForms()) {
			for(DomNode n : form.getChildren()){
				formInputs.addAll(getInputFields(n, webPage));
			}
		}
		
		if(!pagesParams.containsKey(basePage)){
			pagesParams.put(basePage, new PageInput(basePage));
		}
		pagesParams.get(basePage).addAllFormInput(formInputs);
	}
	
	private static List<HtmlInput> getInputFields(DomNode n, String page){
		List<HtmlInput> htmlInput = new ArrayList<HtmlInput>();
		if(n instanceof HtmlInput){
			htmlInput.add((HtmlInput) n);
			//System.out.println("Adding form element from page " + page);
		}
		if (n.hasChildNodes()){
			for(DomNode n2 : n.getChildren()){
				htmlInput.addAll(getInputFields(n2, page));
			}
		}
		
		return htmlInput;
	}

	// username form field = username
	// pass form field = password
	private static void testAuthentication(WebClient webClient, String webPage) throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		try{
			HtmlPage page = webClient.getPage(webPage);
			page.getElementByName(properties.loginUserFormField).setAttribute("value", properties.username);
			page.getElementByName(properties.loginPasswordFormField).setAttribute("value", properties.password);
			HtmlElement submit = null;
			try{
				submit = page.getElementByName(properties.loginSubmitFormField);
			}catch(ElementNotFoundException e){ }
			if(submit == null){
				submit = page.getElementById(properties.loginSubmitFormField);
			}
			
			submit.click();
			System.out.println("Authentication sucessful");
			// Some way of reporting improper data
		}
		catch (FailingHttpStatusCodeException e) {
			//Url does not work
			System.out.println("Authentication-Test: Failed");
		} catch (MalformedURLException e) {
			//Invalid url in file
			//System.err.println("URL-Discovery: Invalid url in secret page file " + secretURL + extension);
		} catch (IOException e) {
			//Error
			//System.err.println("URL-Discovery: " + e.getMessage());
		}
	}
	
	private static boolean allowsEasyPasswords(WebClient webClient, String registerUrl) throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		HtmlPage page = webClient.getPage(registerUrl);

		for(String p : Properties.easyPasswords){
			try{
    			page.getElementByName(properties.registerUserFormField).setAttribute("value", properties.username);
    			page.getElementByName(properties.registerPasswordFormField).setAttribute("value", p);
    			page.getElementByName(properties.confirmPasswordFormField).setAttribute("value", p);
    			
    			HtmlPage newPage = page.getElementById("submit").click();
    			return true;
			}catch(Exception e){
				//e.printStackTrace();
			}
		}
		return false;
	}
	
	private static void fuzzQueryInputs(WebClient webClient, String baseUrl) throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		for (String path : pagesParams.keySet()){
			String url = baseUrl + path;
			PageInput pi = pagesParams.get(path);
			if (pi.getQueryParams().isEmpty()) continue;
			
			String[] paramsArray = pi.getQueryParams().toArray(new String[pi.getQueryParams().size()]);
			
			
			for(String q : paramsArray){
				for(String i : Properties.fuzzInputs){
					String newUrl = url + "?" + i;
					try{
						HtmlPage page = webClient.getPage(newUrl);
						containsSensitiveData(page);
					} catch(Exception e){
						//System.out.println("Failed to get page " + newUrl);
					}
				}
				
			}
			
			
			
		}
	}
	
	private static void fuzzFormInputs(WebClient webClient, String baseUrl) throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		for (String path : pagesParams.keySet()){
			String url = baseUrl + path;
			HtmlPage page = null;
			try{
				page = webClient.getPage(url);
			} catch(Exception e){
				//System.out.println("Url: " + url + " could not be opened");
			}
			if(page == null){ 
				continue; 
			}
			PageInput pi = pagesParams.get(path);			
			if (pi.getFormInputs().isEmpty()) {
				continue;
			}
			
			List<HtmlInput> lhi = new ArrayList<HtmlInput>();
			for(HtmlForm hf : page.getForms()){
				lhi.addAll(getInputFields(hf, url));
				HtmlSubmitInput hsi =  getSubmitElement(hf);
				if(hsi == null){
					continue;
				}
				
				for(String data : properties.getSanitizedData()){
					for(HtmlInput hi : lhi){
						hi.setAttribute("value", data);
					}
					containsSanitizedData((HtmlPage)hsi.click());
				}
				
				for(String data : properties.fuzzInputs){
					for(HtmlInput hi : lhi){
						hi.setAttribute("value", data);
					}
					containsSensitiveData((HtmlPage)hsi.click());
				}
				
			}
			
		}
	}
	
	private static void containsSanitizedData(HtmlPage page) {		
		for (String data : properties.getSanitizedData()) {
			if (page.asXml().toLowerCase().contains(data.toLowerCase()) || 
					page.getUrl().toString().toLowerCase().contains(data.toLowerCase())) {
				System.out.println("	Improperly sanitized data found: " + data);
			}
		}
	}
	
	private static void containsSensitiveData(HtmlPage page){
		System.out.println("Sensitive Data: ");
		for (String data : properties.getSensitiveData()) {
			if (page.asXml().toLowerCase().contains(data.toLowerCase())) {
				System.out.println("	Sensitive data found: " + data);
			}
		}
	}
	
	private static HtmlSubmitInput getSubmitElement(DomNode n){
		HtmlSubmitInput submit = null;
		
		if(n instanceof HtmlSubmitInput){
			submit = (HtmlSubmitInput) n;
		}else{
			for (DomNode dn : n.getChildren()){
				return getSubmitElement(dn);
			}
		}
		
		return submit;
	}
	
}
