<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>注册用户</title>
<link rel='stylesheet' href='css/register.css' type='text/css'>
</head>
<body class="body">
	<div class="logo">
		<div class="logo_1"></div>
		<div class="logo_2"></div>
		<div class="logo_3">
			<div class="logo_3_1">
				<a href="javascript:opener.location.reload()" title="新用户注册">新用户注册</a>
			</div>
			<div class="logo_3_2">
				<a href="#" title="立刻登陆">立刻登陆</a>
			</div>
		</div>
	</div>

	<div class="main"  style="margin: 0pt auto; width: 1000px; top: 172px;">
		<div class="main_1"></div>
		<div class="main_2">
			<script type="text/javascript" src="js/jquery-1.9.js"></script>
			<script type="text/javascript" src="js/register.js"></script>
			<form action="" method="post" id="register" onsubmit="">
				<table class="main_2_1" id="main1">
					<tr>
						<td class="main_2_1_1"><label>用户名：</label>
						</td>
						<td class="main_2_1_2">
							<div class="main_2_1_2_1">
								<input type="text" maxlength="20" onblur="txtName()"
									name="userName" id="UserName" />
							</div>
							<div class="main_2_3_1" id="txtUserName"></div></td>
					</tr>
					<tr>
						<td class="main_2_1_1"><label>邮箱：</label>
						</td>
						<td class="main_2_1_2">
							<div class="main_2_1_2_1">
								<input type="text" maxlength="25" onblur="txtEmail()"
									name="userEmail" id="UserEmail" />
							</div>
							<div class="main_2_3_1" id="txtUserEmail"></div></td>
					</tr>
					<tr>
						<td class="main_2_1_1"><label>密码：</label>
						</td>
						<td class="main_2_1_2">
							<div class="main_2_1_2_1">
								<input type="password" maxlength="16" onblur="txtPwd()"
									name="userPassword" id="UserPwd" />
							</div>
							<div class="main_2_3_1" id="txtUserPwd"></div></td>
					</tr>
					<tr>
						<td class="main_2_1_1"><label>确认密码：</label>
						</td>
						<td class="main_2_1_2">
							<div class="main_2_1_2_1">
								<input type="password" maxlength="16" onblur="txtPass()"
									name="userPass" id="UserPass" />
							</div>
							<div class="main_2_3_1" id="txtUserPass"></div></td>
					</tr>
					<tr>
						<td class="main_2_1_1"><label>验证码：</label>
						</td>
						<td class="main_2_1_2">
							<div class="main_2_2_3">
								<input type="text" maxlength="4" id="inputCode"
									onblur="txtCode()" />
							</div>
							<div class="main_2_3_3" onclick="createCode()" id="checkCode"></div>
							<div class="main_2_3_4">
								<a onclick="createCode()" id="qq" title="看不清楚了吧，点击这里换一张验证码">换一张</a>
							</div>
							<div class="main_2_3_1" id="txtCode"></div></td>
					</tr>
					<tr>
						<td class="main_2_2_2" colspan="2">
							<div class="main_2_2_2_1" style="top: 517px; left: 392px;">
								<!-- <input type="checkbox" checked="false" id="check" /> -->
								我已认真阅读并同意<a href="#">《神计知识服务条款》</a>
							</div>

							<div class="main_2_2_2_2" id="txtCheck"></div></td>
					</tr>
					<tr>
						<td class="main_2_1_1" colspan="2">
							<div class="main_2_2_1">
								<input id="subRegister" type="submit" value="确定" />
							</div></td>
					</tr>
				</table>
			</form>
		</div>
	</div>
</body>
</html>