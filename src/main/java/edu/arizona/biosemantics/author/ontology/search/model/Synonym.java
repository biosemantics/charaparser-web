package edu.arizona.biosemantics.author.ontology.search.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Synonym {

	private String term;
	private String classIRI;
	
	@JsonCreator
	public Synonym(@JsonProperty("term")String term, 
			@JsonProperty("classIRI") String classIRI) {
		this.term = term;
		this.classIRI = classIRI;
	}
	
	public String getTerm() {
		return term;
	}

	public String getClassIRI() {
		return classIRI;
	}

}
