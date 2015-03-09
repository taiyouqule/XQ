package com.shenji.search.bean;

import java.util.ArrayList;
import java.util.List;

public class XmlDialogueBean {
	private String sessionId;
	private String startTime;
	private String endTime;
	private List<RecordBean> rocords;
	
	public XmlDialogueBean(){
		rocords=new ArrayList<XmlDialogueBean.RecordBean>();
	}
	
	public class RecordBean{
		private String speaker;
		private String time;
		private String content;
		
		public String getSpeaker() {
			return speaker;
		}
		public void setSpeaker(String speaker) {
			this.speaker = speaker;
		}
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		
	}
	
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public List<RecordBean> getRocords() {
		return rocords;
	}

	public void addRocord(String speaker,String time,String content) {
		RecordBean recordBean=new RecordBean();
		recordBean.setSpeaker(speaker);
		recordBean.setTime(time);
		recordBean.setContent(content);
		rocords.add(recordBean);		
	}


}
