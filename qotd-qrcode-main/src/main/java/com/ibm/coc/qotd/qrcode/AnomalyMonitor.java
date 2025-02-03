package com.ibm.coc.qotd.qrcode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AnomalyMonitor implements Runnable {
	
	static private Thread monitorThread = null;
	
	static public void activate() {
		if( monitorThread == null ) {
			monitorThread = new Thread( new AnomalyMonitor() );
			monitorThread.start();
		}
	}
	
	private static final Logger log = Logger.getLogger(QrServlet.class.getName());


	@Override
	public void run() {
		while( true ) {
			try {
				Thread.sleep(5000);
				GetServiceConditions();
			} catch (InterruptedException e) {
				log.warning("Unable to get service conditions from anomaly generator");
				return;
			}
		}
	}
	
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	static void GetServiceConditions() {
		try {
			
			String anomalyUrlStr = System.getenv("ANOMALY_GENERATOR_URL");
			
			if( anomalyUrlStr == null ) {
				log.severe("ANOMALY_GENERATOR_URL environment variable not set.");
				return;
			}

			URL url = new URL(anomalyUrlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			
			String jsonStr = br.lines().collect(Collectors.joining());

			conn.disconnect();
			
	        ObjectMapper objectMapper = new ObjectMapper();
	        Map map = objectMapper.readValue(jsonStr, Map.class);
	        // System.out.println(map.toString());
	        
	        Map loggers = (Map) map.get("loggers");
	        if( loggers.size() > 0  ) {
	        	Set<String> keys = (Set<String>) loggers.keySet();
	            for(String key : keys) {
	                // System.out.print(key);
	                Map loggerMap = (Map) loggers.get(key);
	                
	                String code = (String) loggerMap.get("code");
	                String template = (String) loggerMap.get("template");
	                
	                Map repeat = (Map) loggerMap.get("repeat");	                
	                int mean = (int) repeat.get("mean");
	                int stdev = (int) repeat.get("stdev");
	                int min = (int) repeat.get("min");
	                int max = (int) repeat.get("max");
	                
	                QotdLogger logger = new QotdLogger();
	                logger.setId(key);
	                logger.setCode(code);
	                logger.setTemplate(template);
	                logger.setMean(mean);
	                logger.setStdev(stdev);
	                logger.setMin(min);
	                logger.setMax(max);
	                
	                QotdLoggerEngine.addLogger(logger);
	                
		            // now see if any need to be removed
		            Set<String> activeLoggerIds = QotdLoggerEngine.getLoggerIds();
		            for( String loggerId : activeLoggerIds ) {
		            	if( !keys.contains(loggerId) ) {
		            		QotdLoggerEngine.removeLogger(loggerId);
		            	}
		            }
	                
	            }
	        } else {
	        	// remove them all
	        	QotdLoggerEngine.removeAllLoggers();
	        }
	            

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

}
