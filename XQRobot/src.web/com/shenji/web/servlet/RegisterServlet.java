package com.shenji.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shenji.common.action.DBUserManager;
import com.shenji.common.exception.ConnectionPoolException;
import com.shenji.common.log.Log;
import com.shenji.common.util.MD5Util;
import com.shenji.web.bean.UserBean;


public class RegisterServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public RegisterServlet() {
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
		PrintWriter out = response.getWriter();
		UserBean userBean=new UserBean();
	    String name=request.getParameter("name");
	    String passWord=request.getParameter("password");
	    //密码MD5加密
	    String md5PassWord=MD5Util.md5(passWord);
	    String level=request.getParameter("level");
	    String email=request.getParameter("email");
	    userBean.setName(name);
	    userBean.setPassWord(md5PassWord);
	    userBean.setEmail(email);
	    DBUserManager baseManage=null;
		try {
			baseManage = new DBUserManager();
		} catch (ConnectionPoolException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error(e.getMessage(),e);
			out.write("注册发生异常,请联系管理员！");
			return;
		}
	    int reFlag=-1;
	    System.err.println(name);
	    boolean isExist=baseManage.isExistUserName(name);
	    System.err.println(isExist);
	    if(!isExist){	
	    	reFlag=baseManage.register(userBean);
	    }
	    else{
	    	out.write("用户已存在！");
	    	out.close();
	    	return;
	    }
	    baseManage.close();
		out.flush();
		if(reFlag==1)
			out.write("注册成功！");
		else
			out.write("注册失败！");
		out.close();
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
