package edu.arizona.biosemantics.author.ontology.search.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Deprecate {
	public String user;
	public String classIRI;
	public String ontology;
	public String decisionExperts;
	public String deprecateReasons;
	public String replacementTerm;
	public String decisionDate;
	
	@JsonCreator
	public Deprecate(@JsonProperty(value="user", required=false) String user, 
			@JsonProperty("ontology") String ontology,
			@JsonProperty("classIRI") String classIRI,
			@JsonProperty("decisionExperts") String experts, 
			@JsonProperty("reasons") String deprecateReasons, 
			@JsonProperty("alternativeTerm") String replacementTerm,
			@JsonProperty("decisionDate") String date){
		this.user= user;		
		this.classIRI = classIRI;
		this.ontology = ontology;
		this.decisionExperts = experts;
		this.replacementTerm = replacementTerm;
		this.deprecateReasons = deprecateReasons;
		this.decisionDate = date;
	}
	
	public String getExperts() {
		return decisionExperts;
	}

	
	
	public String getReplacementTerm() {
		return replacementTerm;
	}



	public String getReasons() {
		return deprecateReasons;
	}


	public String getUser(){
		return user;
	}
	
	public String getClassIRI(){
		return classIRI;
	}
	
	public String getOntology(){
		return ontology;
	}

	public String getDecisionDate() {
		return decisionDate;
	}
}
