package edu.arizona.biosemantics.author.ontology.search.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Comment extends AnAnnotation {

	@JsonCreator
	public Comment(@JsonProperty(value="user", required=false) String user, 
			@JsonProperty("comment")String annotationContent, 
			@JsonProperty("ontology") String ontology, 
			@JsonProperty("classIRI") String classIRI, 
			@JsonProperty(value="exampleSentence", required=false) String exampleSentence,
			@JsonProperty(value="providedBy") String providedBy) {
     super(user, annotationContent, ontology, classIRI, exampleSentence, providedBy);
	}
}
