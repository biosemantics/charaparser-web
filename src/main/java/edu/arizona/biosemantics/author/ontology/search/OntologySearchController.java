package edu.arizona.biosemantics.author.ontology.search;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PreDestroy;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.parameters.ChangeApplied;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.arizona.biosemantics.author.ontology.search.model.Class;
import edu.arizona.biosemantics.author.ontology.search.model.HasPart;
import edu.arizona.biosemantics.author.ontology.search.model.OntologyIRI;
import edu.arizona.biosemantics.author.ontology.search.model.OntologySearchResult;
import edu.arizona.biosemantics.author.ontology.search.model.PartOf;
import edu.arizona.biosemantics.author.ontology.search.model.Synonym;
import edu.arizona.biosemantics.common.ontology.search.OntologyAccess;
import edu.arizona.biosemantics.common.ontology.search.model.Ontology;
import edu.arizona.biosemantics.common.ontology.search.model.OntologyEntry;
import edu.arizona.biosemantics.semanticmarkup.enhance.know.AnnotationProperty;

@RestController
public class OntologySearchController {
	
	private static OntologyIRI CAREX = new OntologyIRI(Ontology.CAREX, 
			"http://purl.obolibrary.org/obo/carex.owl");
	private static OntologyIRI PO = new OntologyIRI(Ontology.PO,
			"http://purl.obolibrary.org/obo/po.owl");
	private static OntologyIRI PATO = new OntologyIRI(Ontology.PATO,
			"http://purl.obolibrary.org/obo/pato.owl");
	
	private static String HAS_PART = "http://purl.obolibrary.org/obo/BFO_0000051"; 
	private static OntologyIRI[] entityOntologies = { PO, /*CAREX*/ };
	private static OntologyIRI[] qualityOntologies = { PATO, /*CAREX*/ };
	
	private HashMap<Ontology, OntologyAccess> ontologyAccessMap = new HashMap<Ontology, OntologyAccess>();
	private HashMap<Ontology, FileSearcher> searchersMap;
	private HashMap<Ontology, OWLOntologyManager> owlOntologyManagerMap = new HashMap<Ontology, OWLOntologyManager>();
	
	private OntologySearchResultCreator ontologySearchResultCreator;
	private String carexOntologyBaseIRI;
	private String ontologyDir;

	@Autowired
	public OntologySearchController(@Value("${ontologySearch.ontologyDir}") String ontologyDir,
			@Value("${ontologySearch.wordNetDir}") String wordNetDir, 
			OntologySearchResultCreator ontologySearchResultCreator) throws OWLOntologyCreationException {
		this.ontologyDir = ontologyDir;
		this.searchersMap = new HashMap<Ontology, FileSearcher>();
		this.ontologySearchResultCreator = ontologySearchResultCreator;
		
		/*List<OWLAccessorImpl> list = 
		searcher.getOntologyLookupClient().getTermOutputterUtilities().OWLentityOntoAPIs;
		List<OWLAccessorImpl> list2 = 
		searcher.getOntologyLookupClient().getTermOutputterUtilities().OWLqualityOntoAPIs;*/
		for(OntologyIRI o : entityOntologies) {
			HashSet<String> entityOntologyNames = new HashSet<String>();
			entityOntologyNames.add(o.getOntology().name());
			FileSearcher searcher = new FileSearcher(entityOntologyNames, new HashSet<String>(), 
					ontologyDir, wordNetDir);
			OWLOntologyManager owlOntologyManager = searcher.getOwlOntologyManager();
			OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(o.getIri()));
			Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
			ontologies.add(owlOntology);
			OntologyAccess ontologyAccess  = new OntologyAccess(ontologies);
			
			this.searchersMap.put(o.getOntology(), searcher);
			this.ontologyAccessMap.put(o.getOntology(), ontologyAccess);
			this.owlOntologyManagerMap.put(o.getOntology(), owlOntologyManager);
		}
		
		for(OntologyIRI o : qualityOntologies) {
			HashSet<String> qualityOntologyNames = new HashSet<String>();
			qualityOntologyNames.add(o.getOntology().name());
			FileSearcher searcher = new FileSearcher(new HashSet<String>(), qualityOntologyNames, 
					ontologyDir, wordNetDir);
			OWLOntologyManager owlOntologyManager = searcher.getOwlOntologyManager();
			OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(o.getIri()));
			Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
			ontologies.add(owlOntology);
			OntologyAccess ontologyAccess  = new OntologyAccess(ontologies);
			
			this.searchersMap.put(o.getOntology(), searcher);
			this.ontologyAccessMap.put(o.getOntology(), ontologyAccess);
			this.owlOntologyManagerMap.put(o.getOntology(), owlOntologyManager);
		}
	}
	
	@GetMapping(value = "/{ontology}/search", produces = { MediaType.APPLICATION_JSON_VALUE })
	public OntologySearchResult search(@PathVariable String ontology, @RequestParam String term, 
			@RequestParam Optional<String> parent, @RequestParam Optional<String> relation) throws Exception {
		Ontology o = Ontology.valueOf(ontology.toUpperCase());
		OntologyIRI oIRI = getOntologyIRI(o);
		if(!searchersMap.containsKey(o)) 
			throw new IllegalArgumentException();

		List<OntologyEntry> entries = new ArrayList<OntologyEntry>();
		FileSearcher searcher = this.searchersMap.get(o);
		if(this.isQualityOntology(o)) {
			entries.addAll(searcher.getQualityEntries(term));
		}
		if(this.isEntityOntology(o)) {
			entries.addAll(
					searcher.getEntityEntries(term, parent.orElse(""), relation.orElse("")));
		}
		
		return ontologySearchResultCreator.create(o, entries, 
				this.ontologyAccessMap.get(o), 
				this.owlOntologyManagerMap.get(o).getOntology(IRI.create(oIRI.getIri())),
				this.owlOntologyManagerMap.get(o));
	}

	@PostMapping(value = "/synonym", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ChangeApplied createSynonym(@RequestBody Synonym synonym) {
		OWLOntologyManager owlOntologyManager = this.owlOntologyManagerMap.get(Ontology.CAREX);
		OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(CAREX.getIri()));
		OWLDataFactory owlDataFactory = owlOntologyManager.getOWLDataFactory();
		
		String synonymTerm = synonym.getTerm();
		OWLClass clazz = owlDataFactory.getOWLClass(synonym.getClassIRI());
		OWLAnnotationProperty exactSynonymProperty = 
				owlDataFactory.getOWLAnnotationProperty(IRI.create(AnnotationProperty.EXACT_SYNONYM.getIRI()));
		OWLAnnotation synonymAnnotation = owlDataFactory.getOWLAnnotation(
						exactSynonymProperty, owlDataFactory.getOWLLiteral(synonymTerm, "en"));
		OWLAxiom synonymAxiom = owlDataFactory.getOWLAnnotationAssertionAxiom(clazz.getIRI(), synonymAnnotation);
		return owlOntologyManager.addAxiom(owlOntology, synonymAxiom);
	}
	
	@PostMapping(value = "/class", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ChangeApplied createClass(@RequestBody Class c) {
		OWLOntologyManager owlOntologyManager = this.owlOntologyManagerMap.get(Ontology.CAREX);
		OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(CAREX.getIri()));
		OWLDataFactory owlDataFactory = owlOntologyManager.getOWLDataFactory();
		
		String subclassIRI = this.carexOntologyBaseIRI + "#" + c.getTerm();
		OWLClass subclass = owlDataFactory.getOWLClass(subclassIRI);
		OWLClass superclass = owlDataFactory.getOWLClass(c.getSuperclassIRI());
		OWLAxiom subclassAxiom = owlDataFactory.getOWLSubClassOfAxiom(subclass, superclass);
		ChangeApplied change = owlOntologyManager.addAxiom(owlOntology, subclassAxiom);
		if(change != ChangeApplied.SUCCESSFULLY)
			return change;
		
		OWLAnnotationProperty definitionProperty = 
				owlDataFactory.getOWLAnnotationProperty(IRI.create(AnnotationProperty.DEFINITION.getIRI()));
		OWLAnnotation definitionAnnotation = owlDataFactory.getOWLAnnotation
				(definitionProperty, owlDataFactory.getOWLLiteral(c.getDefinition(), "en")); 
		OWLAxiom definitionAxiom = owlDataFactory.getOWLAnnotationAssertionAxiom(
				subclass.getIRI(), definitionAnnotation); 
		return owlOntologyManager.addAxiom(owlOntology, definitionAxiom);
	}
	
	@PostMapping(value = "/partOf", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ChangeApplied createPartOf(@RequestBody PartOf partOf) {
		OWLOntologyManager owlOntologyManager = this.owlOntologyManagerMap.get(Ontology.CAREX);
		OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(CAREX.getIri()));
		OWLDataFactory owlDataFactory = owlOntologyManager.getOWLDataFactory();
		
		OWLClass bearer = owlDataFactory.getOWLClass(partOf.getBearerIRI());
		OWLClass part = owlDataFactory.getOWLClass(partOf.getPartIRI());
		
		OWLObjectProperty partOfProperty = 
				owlDataFactory.getOWLObjectProperty(IRI.create(AnnotationProperty.PART_OF.getIRI()));
		OWLClassExpression partOfExpression = 
				owlDataFactory.getOWLObjectSomeValuesFrom(partOfProperty, part);
		OWLAxiom partOfAxiom = owlDataFactory.getOWLSubClassOfAxiom(bearer, partOfExpression);
		return owlOntologyManager.addAxiom(owlOntology, partOfAxiom);
	}
	
	@PostMapping(value = "/hasPart", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ChangeApplied createHasPart(@RequestBody HasPart hasPart) {
		OWLOntologyManager owlOntologyManager = this.owlOntologyManagerMap.get(Ontology.CAREX);
		OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(CAREX.getIri()));
		OWLDataFactory owlDataFactory = owlOntologyManager.getOWLDataFactory();
		
		OWLClass bearer = owlDataFactory.getOWLClass(hasPart.getBearerIRI());
		OWLClass part = owlDataFactory.getOWLClass(hasPart.getPartIRI());
		
		OWLObjectProperty partOfProperty = 
				owlDataFactory.getOWLObjectProperty(IRI.create(HAS_PART));
		OWLClassExpression partOfExpression = 
				owlDataFactory.getOWLObjectSomeValuesFrom(partOfProperty, part);
		OWLAxiom partOfAxiom = owlDataFactory.getOWLSubClassOfAxiom(bearer, partOfExpression);
		return owlOntologyManager.addAxiom(owlOntology, partOfAxiom);
	}
	
	private boolean isEntityOntology(Ontology o) {
		for(OntologyIRI on : entityOntologies) {
			if(on.getOntology().equals(o))
				return true;
		}
		return false;
	}

	private boolean isQualityOntology(Ontology o) {
		for(OntologyIRI on : qualityOntologies) {
			if(on.getOntology().equals(o))
				return true;
		}
		return false;
	}
	
	private OntologyIRI getOntologyIRI(Ontology o) {
		for(OntologyIRI oIRI : entityOntologies) {
			if(oIRI.getOntology().equals(o))
				return oIRI;
		}
		for(OntologyIRI oIRI : qualityOntologies) {
			if(oIRI.getOntology().equals(o))
				return oIRI;
		}
		return null;
	}
	
	@PreDestroy
	public void destroy() throws Exception {
		for(Ontology o : this.owlOntologyManagerMap.keySet()) {
			OWLOntologyManager manager = this.owlOntologyManagerMap.get(o);
			try(FileOutputStream fos = new FileOutputStream(ontologyDir + File.separator + o.name().toLowerCase() + ".owl")) {
				manager.saveOntology(manager.getOntology(IRI.create(this.getOntologyIRI(o).getIri())), fos);
			}
		}
	}
}
