package com.ibm.coc.qotd.qrcode;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/version")
public class VersionServlet extends HttpServlet {

	private static final long serialVersionUID = 6157374547335794603L;

	public static String VERSION = "qotd-order-service v2.6.0, build: ";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public VersionServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {

			ClassLoader classLoader = getClass().getClassLoader();
			URL resource = classLoader.getResource("build.txt");
			if (resource == null) {
				
				throw new IllegalArgumentException("build file not found! ");
			
			} else {
				
				File file = new File(resource.toURI());
				StringBuffer sb = new StringBuffer();
				Scanner myReader = new Scanner(file);
				while (myReader.hasNextLine()) {
					sb.append(myReader.nextLine());
				}
				myReader.close();
				String build = sb.toString();
				response.setStatus(200);
				response.setContentType("text/plain");
				response.getWriter().print(VERSION + build );
			}

		} catch (Exception e) {
			Utils.log(e, null);
		}


	}

}
