package edu.arizona.biosemantics.author.ontology.search.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PartOf {
	
	private String bearerIRI;
	private String partIRI;

	@JsonCreator
	public PartOf(@JsonProperty("bearerIRI") String bearerIRI, 
			@JsonProperty("partIRI") String partIRI) {
		super();
		this.bearerIRI = bearerIRI;
		this.partIRI = partIRI;
	}
	
	public String getPartIRI() {
		return partIRI;
	}

	public String getBearerIRI() {
		return bearerIRI;
	}

}
