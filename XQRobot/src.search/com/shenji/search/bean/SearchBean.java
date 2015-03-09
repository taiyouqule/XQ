package com.shenji.search.bean;


public class SearchBean {     
    private float score;
    private double similarity;
    private String content; 
	private String pureContent;
	private String question;
	private String answer;
	private String faqId;
	
	public String getFaqId() {
		return faqId;
	}
	public void setFaqId(String faqId) {
		this.faqId = faqId;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
    public double getSimilarity() {
		return similarity;
	}
	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}
	public String getContent() {  
        return content;  
    }  
    public void setContent(String content) {  
        this.content = content;  
    }
    public float getScore() {  
        return score;  
    }  
    public void setScore(float score) {  
        this.score = score;  
    }  
    public String getPureContent() {
  		return pureContent;
  	}
  	public void setPureContent(String pureContent) {
  		this.pureContent = pureContent;
  	}
}

