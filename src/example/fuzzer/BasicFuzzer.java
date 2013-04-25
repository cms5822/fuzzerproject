package example.fuzzer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

public class BasicFuzzer {

	// Map of page url to inputs (query params and form fields)
	private static final Map<String, PageInput> pagesParams = new HashMap<String, PageInput>();
	private static final String currentPage = Properties.bodgeit;
	private static final String registerPage = currentPage + "register.jsp";
	private static final String loginPage = currentPage + "login.jsp";
	
	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		TimedWebClient webClient = new TimedWebClient();
		webClient.setJavaScriptEnabled(true);
		discoverLinks(webClient, currentPage);
		System.out.println("Done finding links");
		discoverPages(webClient, currentPage);
		System.out.println("Done finding secret pages");
		testAuthentication(webClient, loginPage);
		if(Properties.passwordGuess){
			boolean easyPasswords = allowsEasyPasswords(webClient, registerPage);
			System.out.println("Easy to guess passowrds are" + (easyPasswords ? " " : " not ") + "allowed.");
		}
		
		fuzzQueryInputs(webClient, Properties.urlBase);
		//fuzzFormInputs(webClient, Properties.urlBase);
		
		webClient.closeAllWindows();

		for(String s : pagesParams.keySet()){
			System.out.println(pagesParams.get(s));
		}
	}


	/**
	 * This code is for showing how you can get all the links on a given page, and visit a given URL
	 * @param webClient
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private static void discoverLinks(WebClient webClient, String webPage) throws IOException, MalformedURLException{
		HtmlPage page = webClient.getPage(webPage);
		List<HtmlAnchor> links = page.getAnchors();
		
		for (HtmlAnchor link : links) {
			boolean newPage = false;
			//System.out.println("Link discovered: " + link.asText() + " @URL=" + link.getHrefAttribute());
			
			URL uri = new URL(currentPage + link.getHrefAttribute());
			
			if (!pagesParams.containsKey(uri.getPath())){
				pagesParams.put(uri.getPath(), new PageInput(uri.getPath()));
			}
			
			if(uri.getQuery() != null){
				String param = uri.getQuery().split("=")[0];
				String val = uri.getQuery().split("=")[1];
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
			page.getElementByName(Properties.userFormField).setAttribute("value", Properties.username);
			page.getElementByName(Properties.passwordFormField).setAttribute("value", Properties.password);
			page.getElementById("submit").click();
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
    			page.getElementByName(Properties.userFormField).setAttribute("value", Properties.username);
    			page.getElementByName(Properties.registerPasswordFormField).setAttribute("value", p);
    			page.getElementByName(Properties.confirmPasswordFormField).setAttribute("value", p);
    			
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
						containsSensativeData(page);
					} catch(Exception e){
						System.out.println("Failed to get page " + newUrl);
					}
				}
				
			}
			
			
			
		}
	}
	
	private static void fuzzFormInputs(WebClient webClient, String baseUrl) throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		for (String path : pagesParams.keySet()){
			String url = baseUrl + path;
			HtmlPage page = webClient.getPage(url);
			PageInput pi = pagesParams.get(path);
			if (pi.getFormInputIds().isEmpty()) continue;
			for(String fi : Properties.fuzzInputs){
				HtmlSubmitInput submitInput = null;
    			for(String id : pi.getFormInputIds()){
    				HtmlInput curInput = (HtmlInput) page.getElementById(id);
					if (curInput != null && submitInput == null && curInput.getTypeAttribute().equalsIgnoreCase("submit")){
						submitInput = (HtmlSubmitInput) curInput;
						System.out.println("Submit set on page "+ path);
					}
					else if (curInput != null){
						curInput.setAttribute("value", fi);
					}else{
						System.out.println("Reached random else statement");
					}
    			}
				if(submitInput != null){
					try{
						HtmlPage pageResult = submitInput.click();
						containsSensativeData(pageResult);
						System.out.println("Form submited successfully");
					} catch(Exception e){
						System.out.println("Form submit exception: " + e.getMessage());
					}
				}else{
					System.out.println("No submit on page " + path);
				}
			}
			
		}
	}
	
	private static boolean containsSensativeData(HtmlPage page){
		// Todo: check junk
		return false;
	}
}
