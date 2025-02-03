package com.ibm.coc.qotd.qrcode;

import java.util.HashMap;
import java.util.Map;

public class WasErrorCodes {
	
	static private Map<String,String> codes = null;

	
	static public String getError( String code ) {
		
		if( codes == null ) {
			codes = new HashMap<String,String>();
			codes.put("SSLC0008E", SSLC0008E);
		}
		
		return codes.get(code);
		
	}
	
	static private String SSLC0008E = "SSLC0008E: Unable to initialize SSL connection.  Unauthorized access was denied or security settings have expired.  Exception is javax.net.ssl.SSLException: Received fatal alert: certificate_unknown\n"
			+ "    at com.ibm.jsse2.o.a(o.java:9)\n" + "    at com.ibm.jsse2.SSLEngineImpl.a(SSLEngineImpl.java:294)\n"
			+ "    at com.ibm.jsse2.SSLEngineImpl.a(SSLEngineImpl.java:172)\n"
			+ "    at com.ibm.jsse2.SSLEngineImpl.j(SSLEngineImpl.java:12)\n"
			+ "    at com.ibm.jsse2.SSLEngineImpl.b(SSLEngineImpl.java:113)\n"
			+ "    at com.ibm.jsse2.SSLEngineImpl.a(SSLEngineImpl.java:476)\n"
			+ "    at com.ibm.jsse2.SSLEngineImpl.unwrap(SSLEngineImpl.java:95)\n"
			+ "    at javax.net.ssl.SSLEngine.unwrap(SSLEngine.java:14)\n"
			+ "    at com.ibm.ws.ssl.channel.impl.SSLUtils.handleHandshake(SSLUtils.java:1016)\n"
			+ "    at com.ibm.ws.ssl.channel.impl.SSLConnectionLink.readyInbound(SSLConnectionLink.java:566)\n"
			+ "    at com.ibm.ws.ssl.channel.impl.SSLConnectionLink.ready(SSLConnectionLink.java:295)\n"
			+ "    at com.ibm.ws.tcp.channel.impl.NewConnectionInitialReadCallback.sendToDiscriminators(NewConnectionInitialReadCallback.java:214)\n"
			+ "    at com.ibm.ws.tcp.channel.impl.NewConnectionInitialReadCallback.complete(NewConnectionInitialReadCallback.java:113)\n"
			+ "    at com.ibm.ws.tcp.channel.impl.AioReadCompletionListener.futureCompleted(AioReadCompletionListener.java:165)\n"
			+ "    at com.ibm.io.async.AbstractAsyncFuture.invokeCallback(AbstractAsyncFuture.java:217)\n"
			+ "    at com.ibm.io.async.AsyncChannelFuture.fireCompletionActions(AsyncChannelFuture.java:161)\n"
			+ "    at com.ibm.io.async.AsyncFuture.completed(AsyncFuture.java:138)\n"
			+ "    at com.ibm.io.async.ResultHandler.complete(ResultHandler.java:204)\n"
			+ "    at com.ibm.io.async.ResultHandler.runEventProcessingLoop(ResultHandler.java:775)\n"
			+ "    at com.ibm.io.async.ResultHandler$2.run(ResultHandler.java:905)\n"
			+ "    at com.ibm.ws.util.ThreadPool$Worker.run(ThreadPool.java:1646)";

	

}
