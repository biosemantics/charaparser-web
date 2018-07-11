package edu.arizona.biosemantics.author.ontology.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.semanticweb.owlapi.model.OWLOntologyManager;

import edu.arizona.biosemantics.common.ontology.search.model.OntologyEntry;
import edu.arizona.biosemantics.common.ontology.search.model.OntologyEntry.Type;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.OntologyLookupClient;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.Entity;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.EntityProposals;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.FormalConcept;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.search.TermSearcher;

public class FileSearcher {

	private OntologyLookupClient ontologyLookupClient;
	private HashSet<String> entityOntologyNames;
	private HashSet<String> qualityOntologyNames;

	public FileSearcher(HashSet<String> entityOntologyNames, HashSet<String> qualityOntologyNames,
			String ontologyDir, String dictDir) {
		this.entityOntologyNames = entityOntologyNames;
		this.qualityOntologyNames = qualityOntologyNames;
		try {
			this.ontologyLookupClient = new OntologyLookupClient(
					entityOntologyNames, 
					qualityOntologyNames, 
					ontologyDir,
					dictDir);
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}
	
	public List<OntologyEntry> getEntityEntries(String term, String locator, String rel) {
		List<OntologyEntry> result = new ArrayList<OntologyEntry>();
		
		//Only search structures for now leveraging ontologylookup client
		//This is all construction zone to find out use cases of a Searcher of ontologies we have
		try {
			List<EntityProposals> entityProposals = this.ontologyLookupClient.searchStructure(term, locator, rel);
			if(entityProposals != null && !entityProposals.isEmpty()) {
				for(Entity entity : entityProposals.get(0).getProposals()) {
					result.add(new OntologyEntry(null, entity.getClassIRI(), Type.ENTITY, entity.getConfidenceScore(),entity.getLabel(), entity.getPLabel()));
				}
			}
		} catch(Throwable t) {
			t.printStackTrace();
		}
		
		Collections.sort(result);
		return result;
	}

	public List<OntologyEntry> getQualityEntries(String term) {
		List<OntologyEntry> result = new ArrayList<OntologyEntry>();
		
		TermSearcher termSearcher = new TermSearcher(ontologyLookupClient);
		ArrayList<FormalConcept> concepts = termSearcher.searchTerm(term, Type.QUALITY.toString().toLowerCase(), 1.0f);
		if(concepts != null)
			for(FormalConcept concept : concepts) 
				result.add(new OntologyEntry(null, concept.getClassIRI(), Type.QUALITY, concept.getConfidenceScore(), concept.getLabel(), concept.getPLabel()));
		
		Collections.sort(result);
		return result;
	}

	public OWLOntologyManager getOwlOntologyManager() {
		if(this.entityOntologyNames.size() > 0)
			return this.ontologyLookupClient.ontoutil.OWLentityOntoAPIs.get(0).getManager();
		return this.ontologyLookupClient.ontoutil.OWLqualityOntoAPIs.get(0).getManager();
	}
}