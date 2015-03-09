<%@page import="com.shenji.common.log.HtmlLog"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<script src='dwr/engine.js'></script>
<script src='dwr/util.js'></script>
<script src="dwr/interface/LogHtmlWebServer.js"></script>
<!-- 这个将会自动生成,DwrTest.js 根据情况自定义！ -->
<script type="text/javascript">
	function loadFile(fileName) {
		LogHtmlWebServer.copyHtmlFile(fileName);
		if (confirm("Open Log File?")) {//如果是true ，那么就把页面转向thcjp.cnblogs.com 
		} else {//否则说明下了，赫赫 
		}
	}
</script>

<body>
	<%-- <%
		//String[] fileList = LogHtmlWebServer.getFileList();
	%>
	<%
		for (String str : fileList) {
	%>
	<%=str%>
	<%
		}
	%> --%>
	<br>
</body>
</html>