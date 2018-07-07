package edu.arizona.biosemantics.author.ontology.search.model;

import org.semanticweb.owlapi.model.IRI;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Class {

	private String term;
	private String superclassIRI;
	private String definition;
	
	@JsonCreator
	public Class(@JsonProperty("term") String term, 
			@JsonProperty("superclassIRI")String superclassIRI, 
			@JsonProperty("definition")String definition) {
		super();
		this.term = term;
		this.superclassIRI = superclassIRI;
		this.definition = definition;
	}

	public String getTerm() {
		return term;
	}

	public String getSuperclassIRI() {
		return superclassIRI;
	}

	public String getDefinition() {
		return definition;
	}
}
