<%-- <%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="application/octet-stream; charset=utf-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>添加人员信息</title>
<script type="text/javascript">
	function check(form){
		with(form){		
			if(sentence.value == ""){
				alert("不能为空！");
				return false;
			}				
		}
	}
	function txtChange(){
		alert("不能为空！");
	}
	
</script>


</head>
<body>
	<div align="right">
		<input type="submit" value="" onclick="txtChange()"> 
		<a href="http://172.30.3.56:8080/OntologyWebServer/test123.xls">点击下载所有FAQ分词词性表</a>
	</div>
	<form action="WordPreProcessServlet" method="post" onsubmit="return check(this);">
		<table align="center" width="700">
			<tr>
				<td align="center" colspan="3">
					<h2>神计信息知识图谱-词汇预处理</h2>
					<hr>
				</td>
			<tr>
				<td align="center">句子：</td>
				<td>
					<textarea rows="3" cols="70" name="sentence" ></textarea>
				</td>
			</tr>
			<tr>
				<td align="center" colspan="3">
					<input type="submit" value="词法分析">
					<input type="reset" value="重   置">
				</td>
				
			</tr>
		</table>
	</form>
	
	<%
		ArrayList<WordPrepertyBean> arrayList=(ArrayList<WordPrepertyBean>)application.getAttribute("WordPrepertyList");
		application.removeAttribute("WordPrepertyList");	
		if(arrayList!=null){ 		
	%>
		<table align="center" width="600" border=1 style="width:800px;">
			<tr>
				<td align="center">原句子：</td>
				<td align="center" colspan="4"><%=application.getAttribute("sentence")%></td>
			</tr>
			<tr>
				<td align="center">分词结果：</td>
				<td align="center" colspan="4"><%=application.getAttribute("wordParticiple")%></td>
			</tr>		
			<tr>
				<td align="center" colspan="4"><h3>结果：</h3></td>
			</tr>
			<tr align="center" style="font-weight:bold;">
				<td>词汇</td>
				<td>来源</td>
				<td>词性(简)</td>
				<td>词性</td>
			</tr>
			<%for(int i=0;i<arrayList.size();i++){ %>
			<tr align="center">		
				<td><%=arrayList.get(i).getWord()%></td>
				<td><%=arrayList.get(i).getSource()%></td>
				<td><%=arrayList.get(i).getSpeech_en()%></td>
				<td><%=arrayList.get(i).getSpeech_ch()%></td>
				<td><input type="radio" name="check" value="manager"></td>
			</tr>
			<%
			application.removeAttribute("sentence");
			} %>
		</table>
	<%}%>
</body>
</html> --%>