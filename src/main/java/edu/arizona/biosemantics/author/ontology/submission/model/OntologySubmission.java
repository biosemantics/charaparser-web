package edu.arizona.biosemantics.author.ontology.submission.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OntologySubmission {
	
	private String term;
	private String parentTerm;
	private String definition;
	private String sentence;
	private String author;
	private String relatedTaxon;
	private long submissionTime;
	
	@JsonCreator
	public OntologySubmission(
			@JsonProperty("term") String term, 
			@JsonProperty("parentTerm") String parentTerm, 
			@JsonProperty("definition") String definition, 
			@JsonProperty("sentence") String sentence, 
			@JsonProperty("author") String author,
			@JsonProperty("relatedTaxon") String relatedTaxon) {
		super();
		this.term = term;
		this.parentTerm = parentTerm;
		this.definition = definition;
		this.sentence = sentence;
		this.author = author;
		this.relatedTaxon = relatedTaxon;
	}

	public String getTerm() {
		return term;
	}

	public String getParentTerm() {
		return parentTerm;
	}

	public String getDefinition() {
		return definition;
	}

	public String getSentence() {
		return sentence;
	}

	public String getAuthor() {
		return author;
	}

	public String getRelatedTaxon() {
		return relatedTaxon;
	}

	public long getSubmissionTime() {
		return submissionTime;
	}	
	
}
