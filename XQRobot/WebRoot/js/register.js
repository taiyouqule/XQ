

$(document).ready(function(){
	createCode();
});


var code ; //在全局 定义验证码
/**
 * 生成验证码事件
 * @return
 */
function createCode()
{ 
	code = "";
	var codeLength = 4;//验证码的长度
	var checkCode = document.getElementById("checkCode");
	var selectChar = new Array(0,1,2,3,4,5,6,7,8,9,'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z');// 所有候选组成验证码的字符，当然也可以用中文的

	for(var i = 0;i < codeLength; i++ )
	{
		var charIndex = Math.floor(Math.random()*36);
		code +=selectChar[charIndex];
	}
		if(checkCode)
		{
			checkCode.className="code";
			checkCode.innerHTML = code;
		}
}



//$(document).ready(function(){
//	createCode();
//	txtName();
//	txtEmail();
//	txtPwd();
//	txtPass();
//	txtCode();
//});


$(document).ready(function(){
	var q = document.getElementById("txtUserName");
	$("#UserName").focus(function(){
		q.innerHTML = "请输入4到20为字符，首字母不能为数字！";
	});
});

$(document).ready(function(){
	var q = document.getElementById("txtUserPwd");
	$("#UserPwd").focus(function(){
		q.innerHTML = "请输入6-16位的字符、数字或符号！";
	});
});

$(document).ready(function(){
	var q = document.getElementById("txtUserEmail");
	$("#UserEmail").focus(function(){
		q.innerHTML = "请输入您常用的邮箱，将做为密保邮箱！";
	});
});


$(document).ready(function(){
	var q = document.getElementById("txtUserPass");
	$("#UserPass").focus(function(){
		q.innerHTML = "请输入6-16位的字符、数字或符号！";
	});
});

$(document).ready(function(){
	var q = document.getElementById("txtCode");
	$("#inputCode").focus(function(){
		q.innerHTML = "请输入验证码！";
	});
});



/**
 * 用户名验证
 */
function txtName(){
	var regCode = /^[A-Z,a-z]+\w{3,19}$/;
	var q = document.getElementById("txtUserName");
	var name = document.getElementById("UserName").value;
		if(regCode.test(name) == false){
			q.innerHTML = "<p class='code1'>请输入4到20位字符，首位不能为数字!</p>";
			return false;
		}else{
			$.ajax({
				dataType: "text",
				   type: "get",
				   url: "ProvingDo",
				   data: "name="+name,
				   success: function(msg){
						if(msg.trim() == "true"){
						q.innerHTML = "<p class='code1'>此账号已有人使用!</p>";
						return false;
					}else{
						q.innerHTML = "<p class='code2'>通过</p>";
						return true;
					}
				   }
				});
		}
}

/**
 * 用户邮箱验证
 */
function txtEmail(){
		var regCode =  /^([a-zA-Z0-9]+.*[a-zA-Z0-9]*)+@{1}(163|126|sina|google|qq)\.com$/;
		var q = document.getElementById("txtUserEmail");
		var name = document.getElementById("UserEmail").value;
		if(regCode.test(name) == false){
			q.innerHTML = "<p class='code1'>请输入正确的邮箱！</p>";
			return false;
		}else{
			q.innerHTML = "<p class='code2'>通过</p>";
			return true;
		}
}

/**
 * 用户密码验证
 */
function txtPwd(){
		var regCode =  /^(\w|\W){6,16}$/;
		var q = document.getElementById("txtUserPwd");
		var name = document.getElementById("UserPwd").value;
		
		if(regCode.test(name) == false){
			q.innerHTML = "<p class='code1'>请输入6-16位的字符、数字或符号！</p>";
			return false;
		}else{
			q.innerHTML = "<p class='code2'>通过</p>";
			return true;
		}
}

/**
 * 用户密码再次验证
 */
function txtPass(){
		var q = document.getElementById("txtUserPass");
		var pass = document.getElementById("UserPass").value;
		var pwd = document.getElementById("UserPwd").value;
		if(pass == "" || pass == null){
			q.innerHTML = "<p class='code1'>请再次输入6-16位密码！</p>";
			return false;
		}else{
			if(pass == pwd){
				q.innerHTML = "<p class='code2'>通过</p>";
				return true;
			}else{
				q.innerHTML = "<p class='code1'>两次密码必须一致！</p>";
				return false;
			}
		}
}

/**
 * 验证码验证事件
 */
function txtCode(){
	var q = document.getElementById("txtCode");
	var q1 = document.getElementById("inputCode").value;
		if(q1 != code){
			q.innerHTML = "<p class='code1'>请输入正确的验证码！</p>";
			return false;
		}else{
			q.innerHTML = "<p class='code2'>通过</p>";
			return true;
		}
}

function xyj()
{
	var q = document.getElementById("txtCheck");
	var box = document.getElementById("check");
	if(box.checked == true){
		if(txtName() == false | txtEmail() == false | txtPwd() == false | txtPass() == false | txtCode() == false){
			return false;
		}else{
			return true;
		}
	}else{
		q.innerHTML = "<p class='code3'>请仔细阅读酷狗协议！</p>";
		return false;
	}
 }



/**
 * A标签不跳转事件
 */
$(function(){
	$("#qq").click(function(){
		//return false;
	});
});

