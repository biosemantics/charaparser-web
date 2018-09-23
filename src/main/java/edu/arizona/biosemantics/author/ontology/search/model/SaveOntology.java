package edu.arizona.biosemantics.author.ontology.search.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SaveOntology {
	public String user;
	public String ontology;
	@JsonCreator
	public SaveOntology(@JsonProperty("user") String user, 
			@JsonProperty("ontology") String ontology){
		this.user= user;		
		this.ontology = ontology;
	}
	
	public String getUser(){
		return user;
	}
	
	public String getOntology(){
		return ontology;
	}
}
