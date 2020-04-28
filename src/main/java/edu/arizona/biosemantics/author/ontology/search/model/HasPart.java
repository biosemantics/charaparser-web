package edu.arizona.biosemantics.author.ontology.search.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HasPart {
	private String user;
	private String ontology;
	private String bearerIRI;
	private String partIRI;
	private String experts;
	
	@JsonCreator
	public HasPart(@JsonProperty(value="user", required=false) String user, 
			@JsonProperty("ontology") String ontology, 
			@JsonProperty("bearerIRI") String bearerIRI, 
			@JsonProperty("partIRI") String partIRI, 
			@JsonProperty(value="experts", required=false) String experts) {
		super();
		this.bearerIRI = bearerIRI;
		this.partIRI = partIRI;
		this.user = user; //decides use shared or individual ontology
		this.ontology = ontology;
		this.experts = experts; // the creator of this relation
	}
	
	
	public String getExperts() {
		return experts;
	}


	public void setExperts(String experts) {
		this.experts = experts;
	}


	public String getBearerIRI() {
		return this.bearerIRI;
	}

	public String getPartIRI() {
		return this.partIRI;
	}
	public String getUser() {
		return user==null? "":user;
	}

	public String getOntology() {
		return ontology==null? "" : ontology;
	}
}
