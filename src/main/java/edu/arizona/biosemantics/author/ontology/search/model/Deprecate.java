package edu.arizona.biosemantics.author.ontology.search.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Deprecate {
	public String user;
	public String classIRI;
	public String ontology;
	@JsonCreator
	public Deprecate(@JsonProperty(value="user", required=false) String user, 
			@JsonProperty("ontology") String ontology,
			@JsonProperty("classIRI") String classIRI){
		this.user= user;		
		this.classIRI = classIRI;
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
}
