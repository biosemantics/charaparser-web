package edu.arizona.biosemantics.semanticmarkup.web;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Multimap;

import edu.arizona.biosemantics.common.ontology.search.FileSearcher;
import edu.arizona.biosemantics.common.ontology.search.Searcher;
import edu.arizona.biosemantics.common.ontology.search.model.Ontology;
import edu.arizona.biosemantics.common.ontology.search.model.OntologyEntry;
import edu.arizona.biosemantics.semanticmarkup.web.model.Annotation;
import edu.arizona.biosemantics.semanticmarkup.web.model.OntologySearchResult;
import edu.arizona.biosemantics.semanticmarkup.web.model.OntologySearchResultEntry;

//@RestController
public class OntologySearchController {

	private static final Logger LOGGER = LoggerFactory.getLogger(OntologySearchController.class);
	private static Ontology[] ontologies = { Ontology.PO, Ontology.PATO/*, Ontology.CAREX*/ };
	
	private String ontologyDir;
	private String wordNetDir;
	
	private OWLOntologyManager owlOntologyManager;
	private HashMap<Ontology, Searcher> searchersMap;
	private HashMap<Ontology, OWLOntology> owlOntologyMap;

	@Autowired
	public OntologySearchController(@Value("${ontologySearch.ontologyDir}") String ontologyDir,
			@Value("${ontologySearch.wordNetDir}") String wordNetDir) throws OWLOntologyCreationException {
		this.ontologyDir = ontologyDir;
		this.wordNetDir = wordNetDir;
		this.searchersMap = new HashMap<Ontology, Searcher>();
		this.owlOntologyMap = new HashMap<Ontology, OWLOntology>();
		
		LOGGER.info("Loading ontologies");
		this.owlOntologyManager = OWLManager.createOWLOntologyManager();
		for(Ontology o : ontologies) {
			this.searchersMap.put(o, new FileSearcher(o, ontologyDir, wordNetDir));
			this.owlOntologyMap.put(o, owlOntologyManager.loadOntologyFromOntologyDocument(new File(ontologyDir + "/" + 
					o.name().toLowerCase() + ".owl")));
		}
		LOGGER.info("Completed loading ontologies");
	}
	
	@RequestMapping(value = "/{ontology}/search", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public OntologySearchResult parse(@PathVariable String ontology, @RequestParam String term, 
			@RequestParam Optional<String> parent, @RequestParam Optional<String> relation) throws Exception {
		Ontology o = Ontology.valueOf(ontology.toUpperCase());
		if(!searchersMap.containsKey(o)) 
			throw new IllegalArgumentException();
		
		Searcher searcher = this.searchersMap.get(o);
		List<OntologyEntry> entries = 
				searcher.getEntityEntries(term, parent.orElse(""), relation.orElse(""));
		
		return this.createResult(o, entries);
	}

	private OntologySearchResult createResult(Ontology o, List<OntologyEntry> entries) {
		OntologySearchResult result = new OntologySearchResult();
		OWLOntology owlOntology = this.owlOntologyMap.get(o);
		for(OntologyEntry e : entries) {
			String iri = e.getClassIRI();
			OWLClass owlClass = owlOntologyManager.getOWLDataFactory().getOWLClass(IRI.create(iri));
			Set<OWLAnnotation> annotations = EntitySearcher.getAnnotations(owlClass, owlOntology).collect(Collectors.toSet());
			Set<OWLIndividual> indivduals = EntitySearcher.getIndividuals(owlClass, owlOntology).collect(Collectors.toSet());
			for(OWLIndividual i : indivduals) {
				Multimap<OWLObjectPropertyExpression, OWLIndividual> properties = 
					EntitySearcher.getObjectPropertyValues(i, owlOntology);
			}
			
			List<Annotation> resultAnnotations = new ArrayList<Annotation>();
			for(OWLAnnotation a : annotations) {
				System.out.println(a);
				//resultAnnotations.add(new Annotation(a.getProperty().getIRI(), a.getValue().annotationValue()));
			}
			result.getEntries().add(new OntologySearchResultEntry(e.getLabel(), e.getScore(), e.getParentLabel()));
		}
		return result;
	}
}
