package com.ibm.coc.qotd.qrcode;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/err")
public class ErrServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2684929325748063226L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ErrServlet() {

	}

	/**
	 * A private, static logger for this class to use. We could also use the empty
	 * string to log to the "root" logger.
	 */
	private static final Logger log = Logger.getLogger(ErrServlet.class.getName());

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String errCode = request.getParameter("code");

		response.setContentType("text/plain");
		if ("SSLC0008E".equals(errCode)) {
			log.severe(SSLC0008E);
//			Utils.log(SSLC0008E);
			response.setStatus(200);
			response.getWriter().print(SSLC0008E);
		} else {
			Utils.log("Unknown error code: " + errCode);
			response.setStatus(400);
			response.getWriter().print("Unknown error code: " + errCode);
		}

	}

	static private String SSLC0008E = "SSLC0008E: Unable to initialize SSL connection.  Unauthorized access was denied or security settings have expired.  Exception is javax.net.ssl.SSLException: Received fatal alert: certificate_unknown\n"
			+ "    at com.ibm.jsse2.o.a(o.java:9)\n" 
			+ "    at com.ibm.jsse2.SSLEngineImpl.a(SSLEngineImpl.java:294)\n"
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
