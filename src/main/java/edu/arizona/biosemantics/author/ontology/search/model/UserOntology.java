package edu.arizona.biosemantics.author.ontology.search.model;

import java.util.ArrayList;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserOntology {
	public String userId;
	public ArrayList<String> ontos;
	@JsonCreator
	public UserOntology(@JsonProperty("userId") String userId, 
			@JsonProperty("onto") String ontos){
		this.userId = userId;		
		this.ontos = new ArrayList<String>(Arrays.asList(ontos.split(",")));
	}
	
	public String getUserId(){
		return userId;
	}
	
	public ArrayList<String> getUserOntologies(){
		return ontos;
	}
}
