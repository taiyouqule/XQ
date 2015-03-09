package com.shenji.common.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.shenji.common.exception.ConnectionPoolException;
import com.shenji.common.log.Log;
import com.shenji.common.log.LogPathServer;
import com.shenji.onto.OntoApplication;

public class TomcatApplicationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void init() throws ServletException {
		// 上帝保佑，永无bug
		BuddhaBless.buddhaBless();
		// Log.getLogger().fatal("报警邮件测试");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		Log.getLogger().info("XQRoot服务启动:" + df.format(new Date()));
		Log.getLogger().info("===============配置文件开始加载================");
		com.shenji.search.Configuration.load();
		com.shenji.onto.Configuration.load();
		try {
			// 初始化数据库连接池
			ConnectionPool.getInstance();
		} catch (ConnectionPoolException e) {
			// TODO Auto-generated catch block
			Log.getLogger(this.getClass()).error("初始化数据库连接池失败!", e);
		}
		OntoApplication.load();
		LogPathServer.start();
		Log.getLogger().info("===============配置文件结束加载================");
	}

}
