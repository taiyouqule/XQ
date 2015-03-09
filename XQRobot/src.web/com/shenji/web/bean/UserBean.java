package com.shenji.web.bean;
/**
 * 用户信息
 * @author zhq	
 */
public class UserBean {
	//用户名
	private String name;
	//密码（MD5加密）
	private String passWord;
	//用户级别
	private int level;
	//电子邮箱
	private String email;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
}
