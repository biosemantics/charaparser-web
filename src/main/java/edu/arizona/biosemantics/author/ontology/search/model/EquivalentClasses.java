package edu.arizona.biosemantics.author.ontology.search.model;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EquivalentClasses {

		private String classIRI1;
		private String classIRI2;
		private String user;
		private String ontology;
		private String experts;
		private String decisionDate;
		private String reason;
		
		@JsonCreator
		public EquivalentClasses(@JsonProperty(value="user", required=false) String user, @JsonProperty("ontology") String ontology, 
				@JsonProperty("classIRI1") String classIRI1, @JsonProperty("classIRI2") String classIRI2,@JsonProperty(value="decisionExperts", required=false) String experts, @JsonProperty(value="decisionDate", required=false) String date,
				@JsonProperty(value="reason", required=false) String reason) {
			
			this.classIRI1 = classIRI1;
			this.classIRI2 = classIRI2;
			this.user = user;
			this.ontology = ontology;
			this.experts = experts;
			this.decisionDate = date;
			this.reason = reason;
		}
		
		
		public String getExperts() {
			return experts;
		}

		public String getReason() {
			return reason;
		}

		public String getUser(){
			return user==null? "" : user;
		}
		
		public String getOntology(){
			return ontology==null? "":ontology;
		}




		public String getClassIRI1() {
			return classIRI1;
		}




		public String getClassIRI2() {
			return classIRI2;
		}




		public String getDecisionDate() {
			return decisionDate;
		}

	}

