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
<script src="js/tagChange.js" type="text/javascript"></script>
<link type="text/css" rel="stylesheet" href="css/style.css" />

<body onload="javascript:bodyLoad();">
	<table style="line-height:1px;" align="center">
		<tr>
			<td width="1278" height="20" align="right"><font size="1"><a
					href="#">搜索设置</a>|<a href="#">登录</a>&nbsp;<a
					href="../jsp/register.jsp">注册</a> </font></td>
		</tr>
		<tr height="120">
			<td>
				<p align="center">
					<img src="images/21logo.gif" align="bottom"
						style="width:240px;height:90px;" />
				</p>
			</td>

		</tr>
		<tr>
			<td valign="top"><br>
			</td>
		</tr>

		<tr valign="middle">
			<td>
				<form id="searchForm" action="SearchServlet2" method="post">
					<div id="dww-menu" class="mod-tab" style="margin: 0 auto;">
						<div class="mod-hd">
							<input type="hidden" id="tagType" name="tagType" value="1"></input>
							<ul class="tab-nav">
								<li class="nav_current" id="nav1"
									onclick="javascript:doClick(this)">搜索</li>
								<li class="nav_link" id="nav2"
									onclick="javascript:doClick(this)">词汇</li>
								<li class="nav_link" id="nav3"
									onclick="javascript:doClick(this)">本体分类</li>
								<li class="nav_link" id="nav4"
									onclick="javascript:doClick(this)">常用语</li>
								<li class="nav_link" id="nav5"
									onclick="javascript:doClick(this)">自然语言处理</li>
								<li class="nav_link" id="nav6"
									onclick="javascript:doClick(this)">其他</li>
							</ul>
						</div>

						<div class="mod-bd" align="center">
							<div class="undis" id="sub1" style="display:block;">
								<input type="radio" name="radioType_search" value="1" checked />基础搜索
								<input type="radio" name="radioType_search" value="2" /> 普通搜索 <input
									type="radio" name="radioType_search" value="3" /> 本体搜索
							</div>
							<div class="undis" id="sub2">
								<input type="radio" name="radioType_word" value="1" checked
									onclick="display('similaryInput',false);" />中文分词(最大粒度) <input
									type="radio" name="radioType_word" value="2"
									onclick="display('similaryInput',false);" />中文分词(最细粒度) <input
									type="radio" name="radioType_word" value="3"
									onclick="display('similaryInput',false);" /> 同义词 <input
									type="radio" name="radioType_word" value="4"
									onclick="display('similaryInput',false);" /> 相关词 <input
									type="radio" name="radioType_word" value="5"
									onclick="display('similaryInput',true);" /> 词汇相似度 <input
									type="text" id="similaryInput" name="similaryInputTxt"
									style="width:500px;height:40px;line-height:30px;font-size:15px;color:#808080;display:none;"
									value="如：上海" />
							</div>
							<div class="undis" id="sub3"></div>
							<div class="undis" id="sub4"></div>
							<div class="undis" id="sub5"></div>
							<div class="undis" id="sub6">
								<input type="radio" name="radioType_other" value="1" checked />重建索引库
							</div>
							<div class="undis" id="sub7">标签内容7</div>
						</div>

					</div>
					<div class="searchDiv" id="searchDiv" align="center"
						style=" padding-top:1%;">
						<input type="text"
							style="width:500px;height:40px;line-height:30px;font-size:15px;color:#808080;"
							align="middle" maxlength="100" id="searchTxt" name="searchTxt"
							alt="Search Criteria" onkeyup="searchSuggest();"
							value="如：网上认证怎么安装？" onfocus="javascript:inputOnfocus(this);"
							/> <input type="submit"
							style="width:100px;height:45px;font-size:15px;" value="神计一下"
							alt="Run Search" />
					</div>
				</form>
			</td>
		</tr>


		<tr valign="middle">
			<td style="height:80px;">
				<p align="center" style="font-size:14px;">
					<a href="#">词库</a>&nbsp; <a href="#">FAQ库</a>&nbsp; <a href="#">本体库</a>&nbsp;
					<a href="#">NLP库</a>&nbsp; <a href="../log/">日志库</a>&nbsp; <font
						color="#666666">|</font>&nbsp; <a href="#">更多</a>

				</p>
			</td>

		</tr>
		<tr style="height:100px;">
			<td></td>
		</tr>
		<tr>
			<%
				String reStr = (String) session.getAttribute("reStr");
				//System.err.println(reStr);
				session.removeAttribute("reStr");
				if (reStr != null) {
					StringBuilder sb = new StringBuilder(reStr);
					int len = sb.length();
					int count = len / 50;
					System.out.println(len + "");
					int i = 0;
					for (i = 0; i <= count; i++) {
						sb.insert(i * 50, "</br></br></br></br>");
					}
					reStr = sb.toString();
				}
			%>
			<td valign="bottom">
				<!-- 	<div style="word-wrap:break-word;word-break:normal;"> -->
				<p id="reStr" align="center" style="font-size:18px;">
					<font color="#9932CD"><%=(reStr != null) ? reStr : ""%></font>
				</p> <!-- </div> -->
			</td>
		</tr>

		<tr style="height:150px;">
			<td></td>
		</tr>
		<tr>
			<td>
				<p style="font-size:5px;" align="center">
					<a href="#">搜索风云榜</a> <font color="#0000FF">|</font> <a href="#">关于神技搜索</a>
					<font color="#0000FF">|</font> <a href="#">About Shenji</a> &nbsp;
				</p>
			</td>
		</tr>
		<tr>
			<td>
				<p align="center">
					<font color="#666666" size="2">&copy;2014&nbsp;Shenji&nbsp;<u>使用神计一下前必读</u>&nbsp;沪XXXXXX&nbsp;<img
						src="http://www.baidu.com/cache/global/img/gs.gif" /> </font>
				</p>
			</td>
		</tr>
	</table>

</body>

</html>


