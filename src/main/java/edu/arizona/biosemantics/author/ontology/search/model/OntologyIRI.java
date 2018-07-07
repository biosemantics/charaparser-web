package edu.arizona.biosemantics.author.ontology.search.model;

import edu.arizona.biosemantics.common.ontology.search.model.Ontology;

public class OntologyIRI {

	private Ontology ontology;
	private String iri;

	public OntologyIRI(Ontology ontology, String iri) {
		this.ontology = ontology;
		this.iri = iri;
	}

	public Ontology getOntology() {
		return ontology;
	}

	public String getIri() {
		return iri;
	}
	
}
