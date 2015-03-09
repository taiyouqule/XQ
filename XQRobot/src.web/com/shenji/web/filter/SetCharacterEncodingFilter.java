package com.shenji.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.shenji.common.log.Log;

/**
 * 编码过滤器
 * @author zhq
 * 相关网页的编码过滤器
 */
public class SetCharacterEncodingFilter implements Filter {
	class Request extends HttpServletRequestWrapper
	{
		public Request(HttpServletRequest request) {
			super(request);
	    }
	    public String toCh(String input) {
	        try {
	            byte[] bytes = input.getBytes("ISO8859-1");
	            return new String(bytes, "utf-8");
	        } catch (Exception ex) {
	        	Log.getLogger(this.getClass()).error(ex);
	        }
	            return null;
	    }
	    private HttpServletRequest getHttpServletRequest()
	    {
	        return (HttpServletRequest) super.getRequest();
	    }
	    public String getParameter(String name)
	    {
	        return toCh(getHttpServletRequest().getParameter(name));
	    }
	    public String[] getParameterValues(String name)
	    {
	        String values[] =getHttpServletRequest().getParameterValues(name);
	        if (values != null) {
	            for (int i = 0; i < values.length; i++) {
	                values[i] = toCh(values[i]);
	            }
	        }
	        return values;
	        }
	    }
	
	public void destroy() {
		// TODO Auto-generated method stub

	}

	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		// TODO Auto-generated method stub
		 HttpServletRequest httpreq = (HttpServletRequest)arg0;
	        if (httpreq.getMethod().equals("POST")) {
	        	arg0.setCharacterEncoding("utf-8");
	        } else {
	        	arg0 = new Request(httpreq);
	        }
	        arg2.doFilter(arg0, arg1);
	}

	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
