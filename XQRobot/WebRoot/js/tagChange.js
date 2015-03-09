var exStrs=new Array('如：网上认证怎么安装？','如：网上认证','如：网上认证怎么安装？','如：你好','这块功能没开呢！瞎点什么！','请输入管理员密码!');
function doClick(o) {
	// 当前被中的对象设置css
	o.className = "nav_current";
	var j;
	var id;
	var e;
	var p = document.getElementById("reStr");
	p.innerHTML = "";

	// 遍历所有的页签，没有被选中的设置其没有被选中的css
	for ( var i = 1; i <= 7; i++) { // i<7 多少个栏目就填多大值
		id = "nav" + i;
		j = document.getElementById(id);
		e = document.getElementById("sub" + i);
		if (id != o.id) {
			j.className = "nav_link";
			e.style.display = "none";
		} else {
			document.getElementById("tagType").value = i;// 设置标签当前类型
			e.style.display = "block";
			var inputD = document.getElementById("searchTxt");
			inputOnblur(inputD);
		}
	}

}

function inputOnfocus(o) {
	if (o.value.length > 0) {
		o.value = ''
	}
	o.style.color = 'black';
	var p = document.getElementById("reStr");
	p.innerHTML = "";
}


function inputOnblur(o) {
	var tagType = document.getElementById("tagType").value;
	//alert(o.value+":"+exStrs[tagType-1]);
	if (o.value == '' ||o.value!=exStrs[tagType-1]) {	
		o.value = exStrs[tagType-1];
		o.style.color = '#808080';
	}
}




function bodyLoad() {
	var inputD = document.getElementById("searchTxt");
	inputOnblur(inputD);
}


function display(targetid,isShow) {
	var target = document.getElementById(targetid);
	if(isShow==true){
		target.style.display="block"
	}
	else	
		target.style.display = "none";
}


/*function diplay(targetid,isShow) {
	var target = document.getElementById(targetid);
	if (target.style.display == "block") {
		target.style.display = "none";
	} else {
		target.style.display = "block";
	}
}
*/
/*
 * function inputOnfocus(o) { if (o.value == '如：网上认证怎么安装？') { o.value = '' }
 * o.style.color = 'black'; var p = document.getElementById("reStr");
 * p.innerHTML = ""; }
 * 
 * function inputOnblur(o) { if (o.value == '' || o.value == '如：网上认证怎么安装？') {
 * o.value = '如：网上认证怎么安装？'; o.style.color = '#808080'; } }
 */

function doLoad(num) {
	// 当前被中的对象设置css
	var checkId = "nav" + num;
	var checkDoc = document.getElementById(checkId);
	checkDoc.className = "nav_current";
	var id;
	var doc;
	var sub;
	// 遍历所有的页签，没有被选中的设置其没有被选中的css

	for ( var i = 1; i <= 7; i++) { // i<7 多少个栏目就填多大值
		id = "nav" + i;
		doc = document.getElementById(id);
		sub = document.getElementById("sub" + i);
		if (id != checkId) {
			doc.className = "nav_link";
			sub.style.display = "none";
		} else {
			document.getElementById("tagType").value = i;// 设置标签当前类型
			sub.style.display = "block";
		}
	}

}

function load() {
	var tagType = getCookieValue("tagType");
	doLoad(tagType);
}
window.onload = load;

// 获取cookie
function getCookieValue(cookieName) {
	var cookieValue = document.cookie;
	var cookieStartAt = cookieValue.indexOf("" + cookieName + "=");
	if (cookieStartAt == -1) {
		cookieStartAt = cookieValue.indexOf(cookieName + "=");
	}
	if (cookieStartAt == -1) {
		cookieValue = null;
	} else {
		cookieStartAt = cookieValue.indexOf("=", cookieStartAt) + 1;
		cookieEndAt = cookieValue.indexOf(";", cookieStartAt);
		if (cookieEndAt == -1) {
			cookieEndAt = cookieValue.length;
		}
		cookieValue = unescape(cookieValue
				.substring(cookieStartAt, cookieEndAt));// 解码latin-1
	}
	return cookieValue;
}
