package edu.arizona.biosemantics.author.ontology.search.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PartOf {
	
	private String user;
	private String ontology;
	private String bearerIRI;
	private String partIRI;
	private String experts;
	

	@JsonCreator
	public PartOf(@JsonProperty(value="user", required=false) String user,
			@JsonProperty("ontology") String ontology,
			@JsonProperty("bearerIRI") String bearerIRI, 
			@JsonProperty("partIRI") String partIRI,
			@JsonProperty(value="experts", required=false) String experts) {
		super();
		this.bearerIRI = bearerIRI;
		this.partIRI = partIRI;
		this.ontology = ontology;
		this.user = user; //use shared or individual ontology
		this.experts = experts; //creator of this relation
	}
	
	
	public String getExperts() {
		return experts;
	}


	public void setExperts(String experts) {
		this.experts = experts;
	}


	public String getUser() {
		return user==null? "":user;
	}

	public String getOntology() {
		return ontology==null? "" : ontology;
	}

	public String getPartIRI() {
		return partIRI;
	}

	public String getBearerIRI() {
		return bearerIRI;
	}

}
