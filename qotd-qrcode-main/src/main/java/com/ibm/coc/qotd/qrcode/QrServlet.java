package com.ibm.coc.qotd.qrcode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.zxing.WriterException;

@WebServlet("/qr")
public class QrServlet extends HttpServlet {

	private static final long serialVersionUID = -165949374478708281L;


	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public QrServlet() {
		AnomalyMonitor.activate();
	}
	
	/**
	 * A private, static logger for this class to use. We could also use the empty
	 * string to log to the "root" logger.
	 */
	private static final Logger log = Logger.getLogger(QrServlet.class.getName());

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String token = request.getParameter("requestToken");
		
		log.info("QOTDQ0001I: QR service POST request.");

		StringBuffer sb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null)
				sb.append(line);
		} catch (Exception e) {
			Utils.log(e, token);
			response.setStatus(500);
			response.setContentType("text/plain");
			response.getWriter().print(e.getMessage());
		}
		
		String quote = sb.toString();
		int len = quote.length();	
		log.info("QOTDQ0002I: Obtained quote string, size: "+len);
		
		
		try {

			Thread.sleep(80);

			log.info("QOTDQ0003I: Accessing QR builder, target png 320x320 ");

			response.setContentType("image/png");
			response.setStatus(200);

            byte[] qrImage = QrGenerator.createQR(quote);
			
			OutputStream out = response.getOutputStream();
			out.write(qrImage);

			log.info("QOTDQ0004I: QR Code Request completed.");
			
		} catch (WriterException e) {
			log.severe("QOTDQ0001E: "+e.getMessage());
		} catch (IOException e) {
			log.severe("QOTDQ0002E: "+e.getMessage());
		} catch (InterruptedException e) {
			log.severe("QOTDQ0003E: "+e.getMessage());
		}

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

//		String token = request.getParameter("requestToken");
		
		log.info("QOTDQ0005I: QR service GET request.");
		
		
//		Utils.getServiceConditions();

		String quote = "https://gitlab.com/quote-of-the-day/qotd-qrcode";
		log.info("QOTDQ0006I: Building QR for quote: "+quote);
		
		try {
            response.setContentType("image/png");
			response.setStatus(200);

            byte[] qrImage = QrGenerator.createQR(quote);
			
			OutputStream out = response.getOutputStream();
			out.write(qrImage);

			log.info("QOTDQ0007I: QR Code Request completed.");
			
			
		} catch (WriterException e) {
			log.severe("QOTDQ0004E: "+e.getMessage());
		} catch (IOException e) {
			log.severe("QOTDQ0005E: "+e.getMessage());
		}
	}

}
