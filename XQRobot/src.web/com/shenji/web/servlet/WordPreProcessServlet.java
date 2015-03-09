package com.shenji.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shenji.common.util.MD5Util;
import com.shenji.web.bean.UserBean;
import com.shenji.wordclassification.WordPreProcess;
import com.shenji.wordclassification.WordPrepertyBean;


public class WordPreProcessServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public WordPreProcessServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the GET method");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		//PrintWriter out = response.getWriter();
		
	    String name=request.getParameter("sentence");  
	 	WordPreProcess wordPreProcess=new WordPreProcess();
	 	ArrayList<WordPrepertyBean> arrayList=wordPreProcess.wordPreProcess(name,true);
	 	wordPreProcess.sopArrayList(arrayList);
	    //out.write(arrayList.get(4).getWord()+":"+arrayList.get(4).getSpeech());
	 	
	 	ServletContext application=getServletContext();
	 	String wordParticiple=wordPreProcess.wordParticiple(name);
	 	application.setAttribute("WordPrepertyList", arrayList);
	 	application.setAttribute("sentence", name);
	 	application.setAttribute("wordParticiple", wordParticiple);

	 	response.sendRedirect("./myWordProProcess.jsp"); 

		
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}
}
