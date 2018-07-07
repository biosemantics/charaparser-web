package edu.arizona.biosemantics.author.ontology.search.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HasPart {

	private String bearerIRI;
	private String partIRI;
	
	@JsonCreator
	public HasPart(@JsonProperty("bearerIRI") String bearerIRI, 
			@JsonProperty("partIRI") String partIRI) {
		super();
		this.bearerIRI = bearerIRI;
		this.partIRI = partIRI;
	}
	
	public String getBearerIRI() {
		return this.bearerIRI;
	}

	public String getPartIRI() {
		return this.partIRI;
	}

}
