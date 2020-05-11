package edu.arizona.biosemantics.author.ontology.search.model;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Superclass {
		
		private String user;
		private String subclassTerm;
		private String experts; //; separated values
		private String ontology;
		private String subclassIRI; //subclass has superclass
		private String superclassIRI;
		private String decisionDate;
		

		@JsonCreator
		public Superclass(@JsonProperty(value="user", required=false) String user,
				@JsonProperty("ontology") String ontology,
				@JsonProperty("subclassIRI") String subclassIRI, 
				@JsonProperty("superclassIRI") String superclassIRI,
				@JsonProperty("subclassTerm") String subclassTerm,
				@JsonProperty("decisionExperts") String experts, 
				@JsonProperty("decisionDate") String date) {
			super();
			this.subclassIRI = subclassIRI;
			this.superclassIRI = superclassIRI;
			this.ontology = ontology;
			this.user = user;
			this.experts = experts;
			this.subclassTerm = subclassTerm;
			this.decisionDate = date;
		}
		
		
		
		
		
		public String getSubclassTerm() {
			return subclassTerm;
		}





		public void setSubclassTerm(String subclassTerm) {
			this.subclassTerm = subclassTerm;
		}





		public String getExperts() {
			return experts;
		}



		public void setExperts(String expert) {
			this.experts = expert;
		}



		public String getUser() {
			return user==null? "":user;
		}

		public String getOntology() {
			return ontology==null? "" : ontology;
		}

		public String getSuperclassIRI() {
			return superclassIRI;
		}

		public String getSubclassIRI() {
			return subclassIRI;
		}

		public String getDecisionDate() {
			return decisionDate;
		}

	}


