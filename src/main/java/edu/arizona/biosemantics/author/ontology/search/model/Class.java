package edu.arizona.biosemantics.author.ontology.search.model;

import org.semanticweb.owlapi.model.IRI;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Class {

	private String term;
	private String superclassIRI;
	private String definition;
	private String elucidation;
	
	@JsonCreator
	public Class(@JsonProperty("term") String term, 
			@JsonProperty("superclassIRI")String superclassIRI, 
			@JsonProperty("definition")String definition,
			@JsonProperty("elucidation") String elucidation) {
		super();
		this.term = term;
		this.superclassIRI = superclassIRI;
		this.definition = definition;
		this.elucidation = elucidation;
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

	public String getElucidation() {
		return elucidation;
	}
}
