package edu.arizona.biosemantics.author.ontology.search.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Detach {
	public String user;
	public String superclassIRI;
	public String subclassIRI;
	public String ontology;
	public String decisionExperts;
	public String decisionDate;
	@JsonCreator
	public Detach(@JsonProperty(value="user", required=false) String user, 
			@JsonProperty("ontology") String ontology,
			@JsonProperty("superclassIRI") String superclassIRI, 
			@JsonProperty("subclassIRI") String subclassIRI, 
			@JsonProperty("decisionExperts") String experts, 
			@JsonProperty("decisionDate") String date){
		this.user= user;		
		this.superclassIRI = superclassIRI;
		this.ontology = ontology;
		this.subclassIRI = subclassIRI;
		this.decisionExperts = experts;
		this.decisionDate = date;
	}
	
	
	
	public String getExperts() {
		return decisionExperts;
	}



	public String getUser(){
		return user;
	}
	
	public String getSuperclassIRI(){
		return superclassIRI;
	}
	
	public String getSubclassIRI(){
		return subclassIRI;
	}
	public String getOntology(){
		return ontology;
	}



	public String getDecisionDate() {
		return decisionDate;
	}
}
