<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>今天你神计一下了吗</title>
<style>
a:visited {
	color: #0000FF;
}
</style>

<!-- <style type="text/css">
.align-center {
	position: fixed;
	left: 35%;
	top: 30%;
	margin-left: width/2;
	margin-top: height/2;
}
</style> -->
</head>
<script type="text/javascript" src="js/search.js"></script>
<%
	String tagType = (String) session.getAttribute("tagType");
	//int iTagType = Integer.parseInt(tagType);
	System.err.println(tagType);
%>
<body onload="load(<%=(tagType !=null) ? tagType : 2%>);">
	<form id="frmSearch" action="SearchServlet" method="post">
		<table style="line-height:1px;" align="center">
			<tr>
				<td width="1278" height="20" align="right"><font size="1"><a
						href="#">搜索设置</a>|<a href="#">登录</a>&nbsp;<a href="../jsp/register.jsp">注册</a>
				</font>
				</td>
			</tr>
			<tr height="120">
				<td>
					<p align="center">
						<img src="images/21logo.gif" align="bottom"
							style="width:240px;height:90px;" />
					</p></td>

			</tr>
			<tr>
				<td valign="top"><br></td>
			</tr>

			<tr>
				<td>

					<p align="center" style="font-size:15px;">
						<input type="hidden" id="tag0" name="tag0" value="0"></input> 
						<label id="tag1" onclick="foo(1);">基础搜索 </label>&nbsp; <label
							id="tag2" onclick="foo(2);">普通搜索 </label>&nbsp; <label id="tag3"
							onclick="foo(3);">过滤搜索</label>&nbsp; <label id="tag4"
							onclick="foo(4);">分词</label>&nbsp; <label id="tag5"
							onclick="foo(5);">同义词</label>&nbsp;
							<label id="tag6" onclick="foo(6);">本体分类</label>&nbsp;
						<!-- <a href="#" id="tag1" onclick="foo();">基础搜索</a>&nbsp; <strong>普通搜索</strong>&nbsp;
					<a href="#" id="tag2" onclick="bar();">过滤搜索</a>&nbsp; <a href="#">分词</a>&nbsp;
					<a href="#">同义词</a>&nbsp; <a href="#">图谱分类</a>&nbsp; <a href="#">命名实体识别</a>&nbsp;-->
						<!-- <a href="http://map.baidu.com/">地&nbsp;图</a>&nbsp; -->
					</p>
				</td>
			</tr>
			<tr>
				<td>

					<div align="center">
						<input type="text"
							style="width:500px;height:40px;line-height:30px;font-size:15px;color:#808080;"
							align="middle" maxlength="100" id="txtSearch" name="txtSearch"
							alt="Search Criteria" onkeyup="searchSuggest();"
							value="如：网上认证怎么安装？"
							onfocus="if(this.value=='如：网上认证怎么安装？'){this.value=''};this.style.color='black';"
							onblur="if(this.value==''||this.value=='如：网上认证怎么安装？'){this.value='如：网上认证怎么安装？';this.style.color='#808080';}" />
						<input type="submit"
							style="width:100px;height:45px;font-size:15px;" value="神计一下"
							alt="Run Search" />
					</div>
				</td>
			</tr>

			<tr valign="top">
				<td style="height:80px;">
					<p align="center" style="font-size:14px;">
						<a href="#">词库</a>&nbsp; <a href="#">本体库</a>&nbsp; <a href="#">FAQ库</a>&nbsp;
						<a href="../log/">日志库</a>&nbsp;
						<font color="#666666">|</font>&nbsp; <a href="#">更多</a>

					</p></td>

			</tr>
			<tr>
				<%
					String reStr = (String) session.getAttribute("reStr");
					//System.err.println(s);
				%>
				<td style="height:200px;">
					<p id="resultShow" align="center" style="font-size:18px;">
					  <%-- <label id="resultShow"><%=(reStr != null) ? reStr : ""%> </label> --%>
						<font color="#00EE00" ><%=(reStr != null) ? reStr : ""%></font> 
					</p>
				</td>
			</tr>


			<tr>
				<td>
					<p style="font-size:5px;" align="center">
						<a href="#">搜索风云榜</a> <font color="#0000FF">|</font> <a href="#">关于神技搜索</a>
						<font color="#0000FF">|</font> <a href="#">About Shenji</a>
					</p></td>
			</tr>
			<tr>
				<td>
					<p align="center">
						<font color="#666666" size="2">&copy;2014&nbsp;Shenji&nbsp;<u>使用神计一下前必读</u>&nbsp;沪XXXXXX&nbsp;<img
							src="http://www.baidu.com/cache/global/img/gs.gif" /> </font>
					</p></td>
			</tr>
		</table>
	</form>

</body>

</html>


