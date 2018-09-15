package edu.arizona.biosemantics.author.ontology.search;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PreDestroy;

import org.obolibrary.macro.ManchesterSyntaxTool;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.ChangeApplied;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import edu.arizona.biosemantics.author.ontology.search.model.UserOntology;
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
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OntologySearchController.class);
	static Hashtable<String, String> ontologyIRIs = new Hashtable<String, String>();
	static{
		ontologyIRIs.put("CAREX", "http://biosemantics.arizona.edu/ontologies/carex");
		ontologyIRIs.put("EXP", "http://biosemantics.arizona.edu/ontologies/exp");
		ontologyIRIs.put("PO", "http://purl.obolibrary.org/obo/po");
		ontologyIRIs.put("PATO", "http://purl.obolibrary.org/obo/pato");
	}

	
	private static String HAS_PART = "http://purl.obolibrary.org/obo/BFO_0000051"; 
	private static String ELUCIDATION = "http://purl.oblibrary.org/obo/IAO_0000600";
	private static String createdBy = "http://www.geneontology.org/formats/oboInOwl#created_by";
	private static String creationDate = "http://www.geneontology.org/formats/oboInOwl#creation_date";
	private static String definitionSrc = "http://purl.obolibrary.org/obo/IAO_0000119";
	
	private HashMap<String, OntologyAccess> ontologyAccessMap = new HashMap<String, OntologyAccess>();
	private HashMap<String, FileSearcher> searchersMap = new HashMap<String, FileSearcher>();
	private HashMap<String, OWLOntologyManager> owlOntologyManagerMap = new HashMap<String, OWLOntologyManager>();
	
	private OntologySearchResultCreator ontologySearchResultCreator;
	private String ontologyDir;
	private String wordNetDir;
	
	private ArrayList<OntologyIRI> entityOntologies;
	private ArrayList<OntologyIRI> qualityOntologies;

	/*@Autowired
	public OntologySearchController(@Value("${ontologySearch.ontologyDir}") String ontologyDir,
			@Value("${ontologySearch.wordNetDir}") String wordNetDir, 
			OntologySearchResultCreator ontologySearchResultCreator) throws OWLOntologyCreationException {
		this.ontologyDir = ontologyDir;
		this.searchersMap = new HashMap<Ontology, FileSearcher>();
		this.ontologySearchResultCreator = ontologySearchResultCreator;
		
		for(OntologyIRI o : entityOntologies) {
			HashSet<String> entityOntologyNames = new HashSet<String>();
			entityOntologyNames.add(o.getOntology().name());
			FileSearcher searcher = new FileSearcher(entityOntologyNames, new HashSet<String>(), 
					ontologyDir, wordNetDir);
			LOGGER.info("created searcher for " + entityOntologyNames);
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
	}*/
	
	@Autowired
	public OntologySearchController(@Value("${ontologySearch.ontologyDir}") String ontologyDir,
			@Value("${ontologySearch.wordNetDir}") String wordNetDir, OntologySearchResultCreator ontologySearchResultCreator){
		this.ontologyDir = ontologyDir;
		this.wordNetDir = wordNetDir;
		this.ontologySearchResultCreator = ontologySearchResultCreator;
	}
	
	@PostMapping(value = "/createUserOntology", consumes = { MediaType.APPLICATION_JSON_VALUE}, produces = { MediaType.APPLICATION_JSON_VALUE })
	public boolean createUserOntology(@Value("${ontologySearch.ontologyDir}") String ontologyDir,
			OntologySearchResultCreator ontologySearchResultCreator, @RequestBody UserOntology userOntology) throws OWLOntologyCreationException {
		//create shared or individual ontologies
		String userId = userOntology.getUserId();
		if(userId == null || userId.isEmpty()){
			return createSharedOntology(ontologySearchResultCreator);
		}else{
			return createInvidualOntology(userId, userOntology.getUserOntologies(), ontologySearchResultCreator);
		}
	}
	
	private boolean createSharedOntology(OntologySearchResultCreator ontologySearchResultCreator) throws OWLOntologyCreationException {
		//this.ontologySearchResultCreator = ontologySearchResultCreator;
		OntologyIRI CAREX = new OntologyIRI(new File(ontologyDir, "CAREX.owl").getAbsolutePath(), 
				"http://biosemantics.arizona.edu/ontologies/carex", "CAREX");
		OntologyIRI PO = new OntologyIRI(new File(ontologyDir, "PO.owl").getAbsolutePath(),
				"http://purl.obolibrary.org/obo/po", "PO");
		 OntologyIRI PATO = new OntologyIRI(new File(ontologyDir, "PATO.owl").getAbsolutePath(),
				"http://purl.obolibrary.org/obo/pato", "PATO");
		
		this.entityOntologies.add(PO);
		this.entityOntologies.add(CAREX);
		this.qualityOntologies.add(PATO);
		this.qualityOntologies.add(CAREX);
		setUpWorkbench(entityOntologies, qualityOntologies);
		return true;
	}
	
	private boolean createInvidualOntology(String userId, ArrayList<String> userOntologies, OntologySearchResultCreator ontologySearchResultCreator) 
			throws OWLOntologyCreationException{

		try{
			//copy base ontologies to user ontologies
			int i = 0;
			for(String onto: userOntologies){
				File ontoS = new File(ontologyDir, onto.toLowerCase()+".owl");
				File ontoD = new File(ontologyDir, onto.toLowerCase()+"_"+userId+".owl"); //ontology indexed as EXP_1.owl, EXP_2.owl, 1 and 2 are user ids.
				if(!ontoD.exists())
					Files.copy(ontoS.toPath(), ontoD.toPath(), StandardCopyOption.REPLACE_EXISTING);
				
				OntologyIRI EXP = new OntologyIRI(ontoD.getAbsolutePath(), 
						ontologyIRIs.get(onto.toUpperCase()), onto+"_"+userId.toUpperCase()); //for experiments
				entityOntologies.add(EXP); //EXP_1
				qualityOntologies.add(EXP);
			}
			setUpWorkbench(entityOntologies, qualityOntologies);
		}catch(Exception e){
			throw new OWLOntologyCreationException(e.getMessage());
		}
		return true;
	}

	private void setUpWorkbench(ArrayList<OntologyIRI> entityOntologies, ArrayList<OntologyIRI> qualityOntologies) {
		for(OntologyIRI o : entityOntologies) {
			HashSet<String> entityOntologyNames = new HashSet<String>();
			entityOntologyNames.add(o.getName());
			FileSearcher searcher = new FileSearcher(entityOntologyNames, new HashSet<String>(), 
					ontologyDir, wordNetDir);
			LOGGER.info("created searcher for " + entityOntologyNames);
			OWLOntologyManager owlOntologyManager = searcher.getOwlOntologyManager();
			OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(o.getIri()));
			Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
			ontologies.add(owlOntology);
			OntologyAccess ontologyAccess  = new OntologyAccess(ontologies);
			
			this.searchersMap.put(o.getName().toUpperCase(), searcher);
			this.ontologyAccessMap.put(o.getName().toUpperCase(), ontologyAccess);
			this.owlOntologyManagerMap.put(o.getName().toUpperCase(), owlOntologyManager);
		}
		
		for(OntologyIRI o : qualityOntologies) {
			HashSet<String> qualityOntologyNames = new HashSet<String>();
			qualityOntologyNames.add(o.getName());
			FileSearcher searcher = new FileSearcher(new HashSet<String>(), qualityOntologyNames, 
					ontologyDir, wordNetDir);
			OWLOntologyManager owlOntologyManager = searcher.getOwlOntologyManager();
			OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(o.getIri()));
			Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
			ontologies.add(owlOntology);
			OntologyAccess ontologyAccess  = new OntologyAccess(ontologies);
			
			this.searchersMap.put(o.getName().toUpperCase(), searcher);
			this.ontologyAccessMap.put(o.getName().toUpperCase(), ontologyAccess);
			this.owlOntologyManagerMap.put(o.getName().toUpperCase(), owlOntologyManager);
		}
	}

	
	

	@GetMapping(value = "/{ontology}/search", produces = { MediaType.APPLICATION_JSON_VALUE })
	public OntologySearchResult search(@PathVariable String ontology, @RequestParam String term, 
			@RequestParam Optional<String> parent, @RequestParam Optional<String> relation, @RequestParam Optional<String> user) throws Exception {
		String usrid = "";
		String ontoName = ontology.toUpperCase();
		if(user.isPresent()){
			usrid = user.get();
			ontoName = (ontology+"_"+usrid).toUpperCase();
		}
		OntologyIRI oIRI = getOntologyIRI(ontoName);
		
		//ontoName: EXP_1 or EXP, CAREX, etc.
		if(!searchersMap.containsKey(ontoName)) 
			throw new IllegalArgumentException();

		List<OntologyEntry> entries = new ArrayList<OntologyEntry>();
		FileSearcher searcher = this.searchersMap.get(ontoName);
		if(this.isQualityOntology(ontoName)) {
			entries.addAll(searcher.getQualityEntries(term));
		}
		if(this.isEntityOntology(ontoName)) {
			entries.addAll(
					searcher.getEntityEntries(term, parent.orElse(""), relation.orElse("")));
		}
		
		return ontologySearchResultCreator.create(ontoName, entries, 
				this.ontologyAccessMap.get(ontoName), 
				this.owlOntologyManagerMap.get(ontoName).getOntology(IRI.create(oIRI.getIri())),
				this.owlOntologyManagerMap.get(ontoName));
	}

	@PostMapping(value = "/esynonym", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ChangeApplied createESynonym(@RequestBody Synonym synonym) {
		//which ontology to use
		String usrid = "";
		String ontoName = synonym.getOntology().toUpperCase();
		if(!synonym.getUser().isEmpty()){
			usrid = synonym.getUser();
			ontoName = (ontoName+"_"+usrid).toUpperCase();
		}
		OntologyIRI oIRI = getOntologyIRI(ontoName);
		
		//use the selected ontology		
		OWLOntologyManager owlOntologyManager = this.owlOntologyManagerMap.get(oIRI);
		OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(oIRI.getIri()));
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
	
	@PostMapping(value = "/bsynonym", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ChangeApplied createBSynonym(@RequestBody Synonym synonym) {
		//which ontology to use
		String usrid = "";
		String ontoName = synonym.getOntology().toUpperCase();
		if(!synonym.getUser().isEmpty()){
			usrid = synonym.getUser();
			ontoName = (ontoName+"_"+usrid).toUpperCase();
		}
		OntologyIRI oIRI = getOntologyIRI(ontoName);
		
		//use the selected ontology		
		OWLOntologyManager owlOntologyManager = this.owlOntologyManagerMap.get(oIRI);
		OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(oIRI.getIri()));
		OWLDataFactory owlDataFactory = owlOntologyManager.getOWLDataFactory();
		
		String synonymTerm = synonym.getTerm();
		OWLClass clazz = owlDataFactory.getOWLClass(synonym.getClassIRI());
		OWLAnnotationProperty exactSynonymProperty = 
				owlDataFactory.getOWLAnnotationProperty(IRI.create(AnnotationProperty.BROAD_SYNONYM.getIRI()));
		OWLAnnotation synonymAnnotation = owlDataFactory.getOWLAnnotation(
						exactSynonymProperty, owlDataFactory.getOWLLiteral(synonymTerm, "en"));
		OWLAxiom synonymAxiom = owlDataFactory.getOWLAnnotationAssertionAxiom(clazz.getIRI(), synonymAnnotation);
		return owlOntologyManager.addAxiom(owlOntology, synonymAxiom);
	}
	
	@PostMapping(value = "/class", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public String createClass(@RequestBody Class c) {
		//which ontology to use
		String usrid = "";
		String ontoName = c.getOntology().toUpperCase();
		if(!c.getUser().isEmpty()){
			usrid = c.getUser();
			ontoName = (ontoName+"_"+usrid).toUpperCase();
		}
		OntologyIRI oIRI = getOntologyIRI(ontoName);
		
		//use the selected ontology		
		OWLOntologyManager owlOntologyManager = this.owlOntologyManagerMap.get(oIRI);
		OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(oIRI.getIri()));
		OWLDataFactory owlDataFactory = owlOntologyManager.getOWLDataFactory();
		
		String subclassIRI = oIRI.getIri() + "#" + c.getTerm();
		OWLClass subclass = owlDataFactory.getOWLClass(subclassIRI);
		
		OWLAnnotationProperty labelProperty =
				owlDataFactory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
		OWLLiteral labelLiteral = owlDataFactory.getOWLLiteral(c.getTerm(), "en");
		OWLAnnotation labelAnnotation = owlDataFactory.getOWLAnnotation(labelProperty, labelLiteral);
		OWLAxiom labelAxiom = owlDataFactory.getOWLAnnotationAssertionAxiom(subclass.getIRI(), labelAnnotation);
		ChangeApplied change = owlOntologyManager.addAxiom(owlOntology, labelAxiom);
		if(change != ChangeApplied.SUCCESSFULLY)
			return change.name();
		
		OWLClass superclass = owlDataFactory.getOWLClass(c.getSuperclassIRI());
		OWLAxiom subclassAxiom = owlDataFactory.getOWLSubClassOfAxiom(subclass, superclass);
		change = owlOntologyManager.addAxiom(owlOntology, subclassAxiom);
		if(change != ChangeApplied.SUCCESSFULLY)
			return change.name();
		
		OWLAnnotationProperty definitionProperty = 
				owlDataFactory.getOWLAnnotationProperty(IRI.create(AnnotationProperty.DEFINITION.getIRI()));
		OWLAnnotation definitionAnnotation = owlDataFactory.getOWLAnnotation
				(definitionProperty, owlDataFactory.getOWLLiteral(c.getDefinition(), "en")); 
		OWLAxiom definitionAxiom = owlDataFactory.getOWLAnnotationAssertionAxiom(
				subclass.getIRI(), definitionAnnotation); 
		change = owlOntologyManager.addAxiom(owlOntology, definitionAxiom);
		
		if(change != ChangeApplied.SUCCESSFULLY)
			return change.name();
		
		if(c.getElucidation() != null && !c.getElucidation().isEmpty()) {
			OWLAnnotationProperty elucidationProperty = 
					owlDataFactory.getOWLAnnotationProperty(IRI.create(ELUCIDATION));
			OWLAnnotation elucidationAnnotation = owlDataFactory.getOWLAnnotation
					(elucidationProperty, owlDataFactory.getOWLLiteral(c.getElucidation())); 
			OWLAxiom elucidationAxiom = owlDataFactory.getOWLAnnotationAssertionAxiom(
					subclass.getIRI(), elucidationAnnotation); 
			change = owlOntologyManager.addAxiom(owlOntology, elucidationAxiom);
			if(change != ChangeApplied.SUCCESSFULLY)
				return change.name();
		}
		
		
		if(c.getCreatedBy() != null && !c.getCreatedBy().isEmpty()) {
			OWLAnnotationProperty createdByProperty = 
					owlDataFactory.getOWLAnnotationProperty(IRI.create(createdBy));
			OWLAnnotation createdByAnnotation = owlDataFactory.getOWLAnnotation
					(createdByProperty, owlDataFactory.getOWLLiteral(c.getCreatedBy())); 
			OWLAxiom createdByAxiom = owlDataFactory.getOWLAnnotationAssertionAxiom(
					subclass.getIRI(), createdByAnnotation); 
			change = owlOntologyManager.addAxiom(owlOntology, createdByAxiom);
			if(change != ChangeApplied.SUCCESSFULLY)
				return change.name();
		}
		
		if(c.getCreationDate() != null && !c.getCreationDate().isEmpty()) {
			OWLAnnotationProperty CreationDateProperty = 
					owlDataFactory.getOWLAnnotationProperty(IRI.create(creationDate));
			OWLAnnotation CreationDateAnnotation = owlDataFactory.getOWLAnnotation
					(CreationDateProperty, owlDataFactory.getOWLLiteral(c.getCreationDate())); 
			OWLAxiom CreationDateAxiom = owlDataFactory.getOWLAnnotationAssertionAxiom(
					subclass.getIRI(), CreationDateAnnotation); 
			change = owlOntologyManager.addAxiom(owlOntology, CreationDateAxiom);
			if(change != ChangeApplied.SUCCESSFULLY)
				return change.name();
		}
		
		if(c.getDefinitionSrc() != null && !c.getDefinitionSrc().isEmpty()) {
			OWLAnnotationProperty DefinitionSrcProperty = 
					owlDataFactory.getOWLAnnotationProperty(IRI.create(definitionSrc));
			OWLAnnotation DefinitionSrcAnnotation = owlDataFactory.getOWLAnnotation
					(DefinitionSrcProperty, owlDataFactory.getOWLLiteral(c.getDefinitionSrc())); 
			OWLAxiom DefinitionSrcAxiom = owlDataFactory.getOWLAnnotationAssertionAxiom(
					subclass.getIRI(), DefinitionSrcAnnotation); 
			change = owlOntologyManager.addAxiom(owlOntology, DefinitionSrcAxiom);
			if(change != ChangeApplied.SUCCESSFULLY)
				return change.name();
		}

		//logic definition: 
		/*
		 *         <rdfs:subClassOf>
            <owl:Restriction>
                <owl:onProperty rdf:resource="http://purl.obolibrary.org/obo/BFO_0000051"/>
                <owl:someValuesFrom rdf:resource="http://purl.obolibrary.org/obo/PO_0025377"/>
            </owl:Restriction>
            </rdfs:subClassOf>
		 */
		if(c.getLogicDefinition() != null && !c.getLogicDefinition().isEmpty()) {
			OWLClassExpression clsB = null;
			try{
				ManchesterSyntaxTool parser = new ManchesterSyntaxTool(owlOntology, null);
				clsB = parser.parseManchesterExpression(c.getLogicDefinition());
			}catch(Exception e){
				String msg = e.getMessage();
				//System.out.println(msg);
				return msg;
			}
			OWLAxiom def = owlDataFactory.getOWLEquivalentClassesAxiom(subclass, clsB);
			change = owlOntologyManager.addAxiom(owlOntology, def);
			if(change != ChangeApplied.SUCCESSFULLY)
				return change.name();
			
			/*write out ontology for test
			File file = new File("C:/Users/hongcui/Documents/research/AuthorOntology/Data/CarexOntology/carex_tiny.owl");            
			OWLDocumentFormat format = manager.getOntologyFormat(carex);    
			try{
				manager.saveOntology(carex, format, IRI.create(file.toURI()));
			}catch(Exception e){
				System.out.println(e.getStackTrace());
			}*/

		}
		return subclass.getIRI().getIRIString();
	}
	
	@PostMapping(value = "/partOf", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ChangeApplied createPartOf(@RequestBody PartOf partOf) {
		//which ontology to use
		String usrid = "";
		String ontoName = partOf.getOntology().toUpperCase();
		if(!partOf.getUser().isEmpty()){
			usrid = partOf.getUser();
			ontoName = (ontoName+"_"+usrid).toUpperCase();
		}
		OntologyIRI oIRI = getOntologyIRI(ontoName);
		
		//use the selected ontology		
		
		OWLOntologyManager owlOntologyManager = this.owlOntologyManagerMap.get(oIRI);
		OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(oIRI.getIri()));
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
		//which ontology to use
		String usrid = "";
		String ontoName = hasPart.getOntology().toUpperCase();
		if(!hasPart.getUser().isEmpty()){
			usrid = hasPart.getUser();
			ontoName = (ontoName+"_"+usrid).toUpperCase();
		}
		OntologyIRI oIRI = getOntologyIRI(ontoName);
		
		//use the selected ontology		
		
		OWLOntologyManager owlOntologyManager = this.owlOntologyManagerMap.get(oIRI);
		OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(oIRI.getIri()));
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
	
	private boolean isEntityOntology(String o) {
		for(OntologyIRI on : entityOntologies) {
			if(on.getName().equals(o))
				return true;
		}
		return false;
	}

	private boolean isQualityOntology(String o) {
		for(OntologyIRI on : qualityOntologies) {
			if(on.getName().equals(o))
				return true;
		}
		return false;
	}
	
	private OntologyIRI getOntologyIRI(String o) {
		for(OntologyIRI oIRI : entityOntologies) {
			if(oIRI.getName().equals(o))
				return oIRI;
		}
		for(OntologyIRI oIRI : qualityOntologies) {
			if(oIRI.getName().equals(o))
				return oIRI;
		}
		return null;
	}
	
	@PreDestroy
	public void destroy() throws Exception {
		this.save();
	}
	
	@PostMapping(value = "/save", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public void save() throws Exception {
		for(String o : this.owlOntologyManagerMap.keySet()) {
			OWLOntologyManager manager = this.owlOntologyManagerMap.get(o);
			try(FileOutputStream fos = new FileOutputStream(ontologyDir + File.separator + o.toLowerCase() + ".owl")) {
				manager.saveOntology(manager.getOntology(IRI.create(this.getOntologyIRI(o).getIri())), fos);
			}
		}
	}
}
