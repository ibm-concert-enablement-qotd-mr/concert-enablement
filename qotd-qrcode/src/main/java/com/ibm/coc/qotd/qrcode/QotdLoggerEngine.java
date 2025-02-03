package com.ibm.coc.qotd.qrcode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class QotdLoggerEngine {
	
	static private Map<String,Thread> activeLoggers = new HashMap<String,Thread>();
	
	static public void addLogger(QotdLogger logger) {
		
		if( !activeLoggers.containsKey( logger.getId() ) ) {
			Thread thread = new Thread(logger);
			activeLoggers.put(logger.getId(), thread);
			thread.start();			
		}
		
	}
	
	static public void removeLogger(QotdLogger logger) {
		removeLogger(logger.getId());
		
	}

	static public void removeLogger(String loggerId) {
		if( activeLoggers.containsKey(loggerId) ) {
			activeLoggers.remove(loggerId);
			Thread thread = activeLoggers.get(loggerId);
			thread.interrupt();
		}
	}
	

	static public void removeAllLoggers() {
		Set<String> loggerIds = activeLoggers.keySet();
		for( String loggerId : loggerIds ) {
			Thread thread = activeLoggers.get(loggerId);
			thread.interrupt();
			activeLoggers.remove(loggerId);
		}
	}
	
	static public Set<String> getLoggerIds() {
		return activeLoggers.keySet();
	}
}
