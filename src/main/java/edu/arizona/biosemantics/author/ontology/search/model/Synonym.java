package edu.arizona.biosemantics.author.ontology.search.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Synonym {

	private String synonym;
	private String classIRI;
	private String user;
	private String ontology;
	private String experts;
	private String decisionDate;
	private String reason;
	
	@JsonCreator
	public Synonym(@JsonProperty(value="user", required=false) String user, @JsonProperty("term")String term, @JsonProperty("ontology") String ontology, 
			@JsonProperty("classIRI") String classIRI, @JsonProperty(value="decisionExperts", required=false) String experts, @JsonProperty(value="decisionDate", required=false) String date,
			@JsonProperty(value="reason", required=false) String reason) {
		this.synonym = term;
		this.classIRI = classIRI;
		this.user = user;
		this.ontology = ontology;
		this.experts = experts;
		this.decisionDate = date;
		this.reason = reason;
	}
	
	
	public String getExperts() {
		return experts;
	}

	public String getReason() {
		return reason;
	}

	public void setExperts(String experts) {
		this.experts = experts;
	}


	public String getUser(){
		return user==null? "" : user;
	}
	
	public String getOntology(){
		return ontology==null? "":ontology;
	}
	public String getTerm() {
		return synonym;
	}

	public String getClassIRI() {
		return classIRI;
	}


	public String getDecisionDate() {
		return decisionDate;
	}

}
