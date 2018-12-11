package edu.arizona.biosemantics.author.ontology.search.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Definition {

	private String definition;
	private String classIRI;
	private String user;
	private String ontology;
	private String example;
	private String providedBy;
	
	@JsonCreator
	public Definition(@JsonProperty(value="user", required=false) String user, 
			@JsonProperty("definition")String definition, 
			@JsonProperty("ontology") String ontology, 
			@JsonProperty("classIRI") String classIRI, 
			@JsonProperty(value="exampleSentence", required=false) String exampleSentence,
			@JsonProperty(value="providedBy") String providedBy) {
		this.definition = definition;
		this.classIRI = classIRI;
		this.user = user;
		this.ontology = ontology;
		this.example = exampleSentence;
		this.providedBy = providedBy;
	}
	
	public String getUser(){
		return user==null? "" : user;
	}
	
	public String getOntology(){
		return ontology==null? "":ontology;
	}
	public String getDefinition() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		return definition +"(BY "+providedBy+" "+ dateFormat.format(date)+" AS USED IN: "+this.example+ ")";
	}

	public String getClassIRI() {
		return classIRI;
	}

}
