package com.shenji.web.bean;

public class OneResultBean {

	private float score;
	private double similarity;
	private String question;
	private String answer;
	private String path;

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public double getSimilarity() {
		return similarity;
	}

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
