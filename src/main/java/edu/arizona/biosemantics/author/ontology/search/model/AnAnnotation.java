package edu.arizona.biosemantics.author.ontology.search.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AnAnnotation {

	protected String annotationContent; //should include annotator name and annotation date
	protected String classIRI;
	protected String user;
	protected String ontology;
	protected String example;
	protected String providedBy;

	

	/*
	 * add annotation to class
	 * */
	public AnAnnotation(String user, 
			String annotationContent, 
			 String ontology, 
			 String classIRI, 
			String exampleSentence,
			 String experts) {
		this.annotationContent = annotationContent;
		this.classIRI = classIRI;
		this.user = user;
		this.ontology = ontology;
		this.example = exampleSentence;
		this.providedBy = experts;
	}
	
	public String getUser(){
		return user==null? "" : user;
	}
	
	public String getOntology(){
		return ontology==null? "":ontology;
	}
	
	public String getAnnotationContent(){
		return annotationContent;
	}
	public String getProvanance() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		return annotationContent +"(BY "+providedBy+" on "+ dateFormat.format(date)+ ")";
	}

	public String getClassIRI() {
		return classIRI;
	}
	
	public String getExample() {
		if(this.example!=null) return "";
		return example;
	}
	
}
