var tags = [ "tag0", "tag1", "tag2", "tag3", "tag4", "tag5","tag6"];
var tagNow = 2;//初始默认为普通搜索

function load(num) {
	//alert(num);
	foo(num);
}
//window.onload = load;

function foo(num) {
	// alert(tags[num]);
	/*if(tagNow!=num){
		var v = document.getElementById("resultShow");
		v.innerHTML = "";
	}*/
	var v = document.getElementById(tags[num]);
	var Iname = v.innerHTML;
	v.innerHTML = "<strong>" + Iname + "</strong>";
	document.getElementById(tags[0]).value = num;// 设置标签当前类型
	tagNow = num;
	for ( var i = 1; i < 7; i++) {
		if (i == num)
			continue;
		var tag = tags[i];
		var v = document.getElementById(tag);
		var Iname = v.innerHTML;
		var reg = /<strong>/;
		Iname = Iname.replace(reg, "");
		v.innerHTML = Iname;
	}

} 