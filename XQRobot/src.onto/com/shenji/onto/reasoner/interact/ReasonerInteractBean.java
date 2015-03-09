package com.shenji.onto.reasoner.interact;

import com.shenji.onto.reasoner.data.ReasonerRoute;
import com.shenji.onto.reasoner.data.ReasonerTree;

public class ReasonerInteractBean {
	private ReasonerTree userTree;
	private ReasonerTree faqTree;
	private String faqMessage;
	private double relevance;
	private double score;
	private double similiary;
	private ReasonerRoute maxUserRoute;
	private ReasonerRoute maxFaqRoute;

	public ReasonerInteractBean(String faqMessage, ReasonerTree userTree,
			ReasonerTree faqTree, ReasonerRoute maxUserRoute,
			ReasonerRoute maxFaqRoute) throws NumberFormatException {
		this.userTree = userTree;
		this.faqTree = faqTree;
		this.faqMessage = faqMessage;
		this.score = Double.parseDouble(faqMessage.split("#")[1]);
		this.similiary = Double.parseDouble(faqMessage.split("#")[2]);
		this.maxUserRoute = maxUserRoute;
		this.maxFaqRoute = maxFaqRoute;
	
	}

	public ReasonerTree getUserTree() {
		return userTree;
	}

	public void setUserTree(ReasonerTree userTree) {
		this.userTree = userTree;
	}

	public ReasonerTree getFaqTree() {
		return faqTree;
	}

	public void setFaqTree(ReasonerTree faqTree) {
		this.faqTree = faqTree;
	}

	public String getFaqMessage() {
		return faqMessage;
	}

	public void setFaqMessage(String faqMessage) {
		this.faqMessage = faqMessage;
	}

	public double getRelevance() {
		return relevance;
	}

	public void setRelevance(double relevance) {
		this.relevance = relevance;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public double getSimiliraty() {
		return similiary;
	}

	public void setSimiliraty(double similiary) {
		this.similiary = similiary;
	}

	public ReasonerRoute getMaxUserRoute() {
		return maxUserRoute;
	}

	public void setMaxUserRoute(ReasonerRoute maxUserRoute) {
		this.maxUserRoute = maxUserRoute;
	}

	public ReasonerRoute getMaxFaqRoute() {
		return maxFaqRoute;
	}

	public void setMaxFaqRoute(ReasonerRoute maxFaqRoute) {
		this.maxFaqRoute = maxFaqRoute;
	}

}
