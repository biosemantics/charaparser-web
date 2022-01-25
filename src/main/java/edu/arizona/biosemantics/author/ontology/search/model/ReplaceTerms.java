package edu.arizona.biosemantics.author.ontology.search.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReplaceTerms {

		private String[] replaceTerms;
		private String depClassIRI;
		private String user;
		private String ontology;
		private String[] experts;
		private String decisionDate;
		private String reason;
		
		@JsonCreator
		public ReplaceTerms(@JsonProperty(value="user", required=false) String user, @JsonProperty("replaceTerms") String[] replaceTerms, @JsonProperty("ontology") String ontology, 
				@JsonProperty("depClassIRI") String depClassIRI, @JsonProperty(value="decisionExperts", required=false) String[] experts, @JsonProperty(value="decisionDate", required=false) String date,
				@JsonProperty(value="reason", required=false) String reason) {
			this.replaceTerms = replaceTerms;
			this.depClassIRI = depClassIRI;
			this.user = user;
			this.ontology = ontology;
			this.experts = experts;
			this.decisionDate = date;
			this.reason = reason;
		}
		
		
		public String[] getExperts() {
			return experts;
		}

		public String getReason() {
			return reason;
		}

		public void setExperts(String[] experts) {
			this.experts = experts;
		}


		public String getUser(){
			return user==null? "" : user;
		}
		
		public String getOntology(){
			return ontology==null? "":ontology;
		}
		public String[] getReplaceTerms() {
			return replaceTerms;
		}

		public String getDepClassIRI() {
			return depClassIRI;
		}


		public String getDecisionDate() {
			return decisionDate;
		}

	}


