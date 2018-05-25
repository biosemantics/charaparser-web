package edu.arizona.biosemantics.author.ontology.search;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValueVisitor;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Multimap;

import edu.arizona.biosemantics.author.ontology.search.model.Annotation;
import edu.arizona.biosemantics.author.ontology.search.model.OntologySearchResult;
import edu.arizona.biosemantics.author.ontology.search.model.OntologySearchResultEntry;
import edu.arizona.biosemantics.common.ontology.search.FileSearcher;
import edu.arizona.biosemantics.common.ontology.search.Searcher;
import edu.arizona.biosemantics.common.ontology.search.model.Ontology;
import edu.arizona.biosemantics.common.ontology.search.model.OntologyEntry;

@RestController
public class OntologySearchController {

	private static Ontology[] ontologies = { Ontology.PO, Ontology.PATO/*, Ontology.CAREX*/ };
	
	private HashMap<Ontology, Searcher> searchersMap;
	private OntologySearchResultCreator ontologySearchResultCreator;

	@Autowired
	public OntologySearchController(@Value("${ontologySearch.ontologyDir}") String ontologyDir,
			@Value("${ontologySearch.wordNetDir}") String wordNetDir, 
			OntologySearchResultCreator ontologySearchResultCreator) throws OWLOntologyCreationException {
		this.searchersMap = new HashMap<Ontology, Searcher>();
		this.ontologySearchResultCreator = ontologySearchResultCreator;
		for(Ontology o : ontologies) 
			this.searchersMap.put(o, new FileSearcher(o, ontologyDir, wordNetDir));
	}
	
	@GetMapping(value = "/{ontology}/search", produces = { MediaType.APPLICATION_JSON_VALUE })
	public OntologySearchResult parse(@PathVariable String ontology, @RequestParam String term, 
			@RequestParam Optional<String> parent, @RequestParam Optional<String> relation) throws Exception {
		Ontology o = Ontology.valueOf(ontology.toUpperCase());
		if(!searchersMap.containsKey(o)) 
			throw new IllegalArgumentException();
		
		Searcher searcher = this.searchersMap.get(o);
		List<OntologyEntry> entries = 
				searcher.getEntityEntries(term, parent.orElse(""), relation.orElse(""));
		
		return ontologySearchResultCreator.create(o, entries);
	}
}
