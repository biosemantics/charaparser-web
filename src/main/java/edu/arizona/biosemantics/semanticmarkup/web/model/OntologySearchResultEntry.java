package edu.arizona.biosemantics.semanticmarkup.web.model;

public class OntologySearchResultEntry {

	private double score;
	private String term;
	private String parentTerm;

	public OntologySearchResultEntry(String term, double score, String parentTerm) {
		this.term = term;
		this.score = score;
		this.parentTerm = parentTerm;
	}
	
	public double getScore() {
		return score;
	}

	public String getTerm() {
		return term;
	}

	public String getParentTerm() {
		return parentTerm;
	}
	
	
	
}
