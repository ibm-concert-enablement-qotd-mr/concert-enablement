package com.ibm.coc.qotd.qrcode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {

	static Random rnd = new Random();

	static int genNormalInt(int mean, int stdev, int min, int max) {

		double n = rnd.nextGaussian();

		double v = n * stdev + mean;

		int val = (int) v;

		while (val < min || val > max) {
			val = genNormalInt(mean, stdev, min, max);
		}

		return val;

	}

	static int genNormalDouble(double mean, double stdev, double min, double max) {

		double n = rnd.nextGaussian();

		double v = n * stdev + mean;

		int val = (int) v;

		while (val < min || val > max) {
			val = genNormalDouble(mean, stdev, min, max);
		}

		return val;

	}
	
	static void log(String msg ) {
		System.out.println( msg );
	}
	
	static void log(String msg, String token ) {
		String prefix = "";
		if( token != null ) {
			prefix = "[" + token + "] ";
		}
		
		String lines[] = msg.split("\\r?\\n");
		for (String line : lines) {
			System.out.println( prefix + line );
		}
	}
	
	static void log(Exception ex, String token ) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		
		String prefix = "";
		if( token != null ) {
			prefix = "[" + token + "] ";
		}
		
		String lines[] = sw.toString().split("\\r?\\n");
		for (String line : lines) {
			System.out.println( prefix + line );
		}
	}

	static void GetServiceConditions() {
		try {

			URL url = new URL("http://localhost:3012/services/qrcode");
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
	        System.out.println(map.toString());
	        
	        Map loggers = (Map) map.get("loggers");
	        if( loggers.size() > 0  ) {
	        	Set<String> keys = (Set<String>) loggers.keySet();
	            for(String key : keys) {
	                System.out.print(key);
	                Map loggerMap = (Map) loggers.get(key);
	                String code = (String) loggerMap.get("template");
	                
	                Map repeat = (Map) loggerMap.get("repeat");
	                
	                int mean = (int) repeat.get("mean");
	                int stdev = (int) repeat.get("stdev");
	                int min = (int) repeat.get("min");
	                int max = (int) repeat.get("max");
	                
	                QotdLogger logger = new QotdLogger();
	                logger.setId(key);
	                logger.setCode(code);
	                logger.setMean(mean);
	                logger.setStdev(stdev);
	                logger.setMin(min);
	                logger.setMax(max);
	                
	                QotdLoggerEngine.addLogger(logger);
	                
	            }
	        }
	        
//	        System.out.println( loggers );
	        

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

}
