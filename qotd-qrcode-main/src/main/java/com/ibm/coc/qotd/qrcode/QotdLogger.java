package com.ibm.coc.qotd.qrcode;

import java.util.logging.Logger;

public class QotdLogger implements Runnable {
	/**
	 * A private, static logger for this class to use. We could also use the empty
	 * string to log to the "root" logger.
	 */
	private static final Logger log = Logger.getLogger(ErrServlet.class.getName());

	private String id;
	private String code = null;
	private String template;
	private int mean;
	private int stdev;
	private int min;
	private int max;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public static Logger getLog() {
		return log;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public int getMean() {
		return mean;
	}
	public void setMean(int mean) {
		this.mean = mean;
	}
	public int getStdev() {
		return stdev;
	}
	public void setStdev(int stdev) {
		this.stdev = stdev;
	}
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	@Override
	public void run() {
		
		String errorString = "******* Misconfigured Log Anomaly *******";
		
		if( code != null ) {
			errorString = WasErrorCodes.getError(code);
		} else if( template != null ) {
			errorString = template;
		}
		
		if( errorString != null ) {
			while( true ) {
				log.severe(errorString);
				try {
					Thread.sleep(mean);
				} catch (InterruptedException e) {
					return;
				}
				
			}
		}
	}

}
