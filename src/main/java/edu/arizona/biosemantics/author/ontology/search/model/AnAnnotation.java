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
			 String providedBy) {
		this.annotationContent = annotationContent;
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
	public String getAnnotationContent() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		if(this.example!=null && this.example.trim().length()>0)
			 return annotationContent +"(BY "+providedBy+" "+ dateFormat.format(date)+" Usage Example: "+this.example+ ")";	
		else
			return annotationContent +"(BY "+providedBy+" "+ dateFormat.format(date)+ ")";
	}

	public String getClassIRI() {
		return classIRI;
	}
}
