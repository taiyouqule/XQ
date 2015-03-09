/**
 * 
 */
package com.shenji.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.shenji.common.log.Log;
import com.shenji.search.DicControl;
import com.shenji.webservices.port.CommonServer;
import com.shenji.webservices.port.OntoReasoning;
import com.shenji.webservices.port.Search;

/**
 * @author zhq
 * 
 */
public class SearchServlet2 extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public SearchServlet2() {
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
		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String tagType = request.getParameter("tagType");
		String searchTxt = request.getParameter("searchTxt");		
		int tagTypeInt = Integer.parseInt(tagType);
		Log.getLogger().debug(tagType);
		String html = null;
		Cookie cookie = new Cookie("tagType", tagType); 
		response.addCookie(cookie);
		
		if(searchTxt.startsWith("如："))
			searchTxt=searchTxt.replace("如：", "");
		if(tagTypeInt==1){
			int radoiTypeInt = Integer.parseInt(request.getParameter("radioType_search"));
			html = new Search().search(searchTxt, 1, radoiTypeInt);
			out.write(html); 		
		}
		if(tagTypeInt==2){
			int radoiTypeInt = Integer.parseInt(request.getParameter("radioType_word"));
			switch (radoiTypeInt) {
			case 1:
				html = new Search().fenCi(searchTxt, 1);
				break;
			case 2:
				html = new Search().fenCi(searchTxt, 2);
				break;
			case 3:
				html = new Search().getSynonmWords(searchTxt);
				break;
			case 4:
				html = new Search().getAboutWords(searchTxt);
				break;			
			case 5:{
				String similaryInputTxt = request.getParameter("similaryInputTxt");	
				if(similaryInputTxt.startsWith("如："))
					similaryInputTxt=similaryInputTxt.replace("如：", "");
				double similary= new DicControl().getWordsSimilary(similaryInputTxt, searchTxt);	
				html="\""+similaryInputTxt +"\""+" 与 " +"\""+searchTxt+"\""+" 的相似度为："+similary;
				break;}
			default:
				break;
			}	
			createSession(request,response,html);
		}
		if(tagTypeInt==3){		
			html = new OntoReasoning().getUserReasonerTreeXmlBySentence(searchTxt);
			createSession(request,response,html);
		}
		
		if(tagTypeInt==4){		
			html = new Search().getPhrase(searchTxt);
			createSession(request,response,html);
			
		}
		
		if(tagTypeInt==6){
			int radoiTypeInt = Integer.parseInt(request.getParameter("radioType_other"));
			html="没有结果！！";
			switch (radoiTypeInt) {
			case 1:
				int flag = new CommonServer().reBuildAllIndex(searchTxt);
				if(flag==-2){
					html="密码错误！！";
				}
				else if(flag==-1)
					html="重建失败！！";
				else if(flag==1)
					html="重建成功！！";
				break;
			default:
				break;
			}	
			createSession(request,response,html);
		}
		out.close();
	}

	private void createSession(HttpServletRequest request,
			HttpServletResponse response, String message)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		if (message == null)
			message = "没有结果！！";
		session.setAttribute("reStr", message);
		RequestDispatcher rd = request.getRequestDispatcher("/search2.jsp");
		rd.forward(request, response);
	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
