package edu.arizona.biosemantics.author.ontology.search.model;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Superclass {
		
		private String user;
		private String ontology;
		private String subclassIRI; //subclass has superclass
		private String superclassIRI;
		

		@JsonCreator
		public Superclass(@JsonProperty(value="user", required=false) String user,
				@JsonProperty("ontology") String ontology,
				@JsonProperty("subclassIRI") String subclassIRI, 
				@JsonProperty("superclassIRI") String superclassIRI) {
			super();
			this.subclassIRI = subclassIRI;
			this.superclassIRI = superclassIRI;
			this.ontology = ontology;
			this.user = user;
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

	}


