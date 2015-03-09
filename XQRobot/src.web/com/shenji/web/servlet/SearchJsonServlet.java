package com.shenji.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shenji.common.log.Log;
import com.shenji.search.IEnumSearch.SearchRelationType;
import com.shenji.search.SearchControl;
import com.shenji.web.bean.OneResultBean;

public class SearchJsonServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public SearchJsonServlet() {
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
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String search = new String(request.getParameter("txtSearch").getBytes(
				"ISO8859_1"), "UTF-8");
		System.out.println("搜索内容----->" + search);
		// System.err.println(tagType);
		List<OneResultBean> list = null;

		try {
			list = new SearchControl().searchBasicJson(search,
					SearchRelationType.OR_SEARCH);
			Gson gson = new GsonBuilder().disableHtmlEscaping().create();
			String line = gson.toJson(list);
			out.println(line);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			out.write("null");
			Log.getLogger(this.getClass()).error(e.getMessage(), e);
		}

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
