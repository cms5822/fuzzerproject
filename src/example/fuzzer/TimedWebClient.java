package example.fuzzer;

import java.io.IOException;
import java.net.MalformedURLException;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TimedWebClient extends WebClient{

	private boolean requestAllowed;
	private long timeGap;

	public TimedWebClient() {
		super();
		requestAllowed = true;
		timeGap = Properties.timeGap;
	}

	// override getPage
	public HtmlPage getPage(String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		while(!getRequestAllowed());
		startTimer();
		return super.getPage(url);
	}

	public synchronized void setRequestAllowed(boolean requestAllowed) {
		this.requestAllowed = requestAllowed;
	}
	
	public synchronized boolean getRequestAllowed() {
		return requestAllowed;
	}
	// start for timer
	private void startTimer() {
		requestAllowed = false;
		requestTimer timer = new requestTimer(this, timeGap);
		Thread timerThread = new Thread(timer);
		timerThread.start();
	}

	public class requestTimer implements Runnable {
		
		private TimedWebClient webClient;
		private long timeGap;
		public requestTimer(TimedWebClient webClient, long timeGap) {
			this.webClient = webClient;
			this.timeGap = timeGap;
		}

		@Override
		public void run() {
			try{
				if(timeGap > 0.0)
					Thread.sleep(timeGap);
			}catch(InterruptedException e) {
				System.err.println("TimedWebClient has encountered an error : " + e.getMessage());
			}
			webClient.setRequestAllowed(true);
		}
	}
}