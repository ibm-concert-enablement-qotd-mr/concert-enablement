package com.ibm.coc.qotd.qrcode;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/health")
public class HealthServlet extends HttpServlet {
       
	private static final long serialVersionUID = 6157374547335794603L;
	
	public static boolean Healthy = true;

	/**
     * @see HttpServlet#HttpServlet()
     */
    public HealthServlet() {
        super();
    }

	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		if( Healthy ) {
			response.setStatus(200);
	        response.setContentType("text/plain");
	        response.getWriter().print("I'm feeling good.");
		} else {
			response.setStatus(500);
	        response.setContentType("text/plain");
	        response.getWriter().print("I'm not feeling that good right now.");
		}
	}

}
