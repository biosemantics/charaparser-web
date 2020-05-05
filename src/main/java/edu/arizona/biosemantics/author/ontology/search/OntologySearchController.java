package edu.arizona.biosemantics.author.ontology.search;

import java.io.File;

import java.io.FileOutputStream;
//import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PreDestroy;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.obolibrary.macro.ManchesterSyntaxTool;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
//import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.model.parameters.ChangeApplied;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.search.EntitySearcher;
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

//import com.fasterxml.jackson.annotation.JsonProperty;
import edu.arizona.biosemantics.common.ontology.search.FileSearcher;
import edu.arizona.biosemantics.author.ontology.search.model.AnAnnotation;
import edu.arizona.biosemantics.author.ontology.search.model.Class;
import edu.arizona.biosemantics.author.ontology.search.model.Comment;
import edu.arizona.biosemantics.author.ontology.search.model.Definition;
import edu.arizona.biosemantics.author.ontology.search.model.Deprecate;
import edu.arizona.biosemantics.author.ontology.search.model.UserOntology;
import edu.arizona.biosemantics.author.ontology.search.model.HasPart;
import edu.arizona.biosemantics.author.ontology.search.model.OntologyIRI;
import edu.arizona.biosemantics.author.ontology.search.model.OntologySearchResult;
import edu.arizona.biosemantics.author.ontology.search.model.PartOf;
import edu.arizona.biosemantics.author.ontology.search.model.SaveOntology;
import edu.arizona.biosemantics.author.ontology.search.model.Superclass;
import edu.arizona.biosemantics.author.ontology.search.model.Synonym;
import edu.arizona.biosemantics.common.ontology.search.OntologyAccess;
import edu.arizona.biosemantics.common.ontology.search.model.Ontology;
import edu.arizona.biosemantics.common.ontology.search.model.OntologyEntry;
import edu.arizona.biosemantics.common.ontology.search.model.OntologyEntry.Type;
import edu.arizona.biosemantics.semanticmarkup.enhance.know.AnnotationProperty;
import uk.ac.manchester.cs.jfact.JFactFactory;

@RestController
public class OntologySearchController {

	private static final Logger LOGGER = LoggerFactory.getLogger(OntologySearchController.class);
	static Hashtable<String, String> ontologyIRIs = new Hashtable<String, String>();
	static{
		ontologyIRIs.put("carex", "http://biosemantics.arizona.edu/ontologies/carex");
		ontologyIRIs.put("exp", "http://biosemantics.arizona.edu/ontologies/exp");
		ontologyIRIs.put("po", "http://purl.obolibrary.org/obo/po");
		ontologyIRIs.put("pato", "http://purl.obolibrary.org/obo/pato");
	}


	private static String HAS_PART = "http://purl.obolibrary.org/obo/BFO_0000051"; 
	private static String ELUCIDATION = "http://purl.oblibrary.org/obo/IAO_0000600";
	private static String createdBy = "http://www.geneontology.org/formats/oboInOwl#created_by";
	private static String creationDate = "http://www.geneontology.org/formats/oboInOwl#creation_date";
	private static String definitionSrc = "http://purl.obolibrary.org/obo/IAO_0000119";
	private static String exampleOfUsage = "http://purl.obolibrary.org/obo/IAO_0000112";
	private static String synonymnr = "http://biosemantics.arizona.edu/ontologies/carex#has_not_recommended_synonym";
	private static String synonymb = "http://www.geneontology.org/formats/oboInOwl#hasBroadSynonym";
	private static String synonyme = "http://www.geneontology.org/formats/oboInOwl#hasExactSynonym";
	private static String definitions = "http://purl.obolibrary.org/obo/IAO_0000115";
	private static String elucidations = "http://purl.obolibrary.org/obo/IAO_0000600";
	private static String comment = "http://www.w3.org/2000/01/rdf-schema#comment";

	private HashMap<String, OntologyAccess> ontologyAccessMap = new HashMap<String, OntologyAccess>();
	private HashMap<String, FileSearcher> searchersMap = new HashMap<String, FileSearcher>();
	private HashMap<String, OWLOntologyManager> owlOntologyManagerMap = new HashMap<String, OWLOntologyManager>();
	//private HashMap<String, Hashtable<String, String>> termDefinitionMap = new HashMap<String, Hashtable<String, String>>();

	private OntologySearchResultCreator ontologySearchResultCreator = new OntologySearchResultCreator();
	private String ontologyDir;
	private String wordNetDir;

	private ArrayList<OntologyIRI> entityOntologies = new ArrayList<OntologyIRI> ();
	private ArrayList<OntologyIRI> qualityOntologies = new ArrayList<OntologyIRI>();

	private ArrayList<OntologyIRI> allLiveEntityOntologies = new ArrayList<OntologyIRI> ();
	private ArrayList<OntologyIRI> allLiveQualityOntologies = new ArrayList<OntologyIRI>();

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
			@Value("${ontologySearch.wordNetDir}") String wordNetDir/*, OntologySearchResultCreator ontologySearchResultCreator*/){
		this.ontologyDir = ontologyDir;
		this.wordNetDir = wordNetDir;
		//this.ontologySearchResultCreator = ontologySearchResultCreator;
	}

	@PostMapping(value = "/createUserOntology", consumes = { MediaType.APPLICATION_JSON_VALUE}, produces = { MediaType.APPLICATION_JSON_VALUE })
	public boolean createUserOntology(@Value("${ontologySearch.ontologyDir}") String ontologyDir,
			OntologySearchResultCreator ontologySearchResultCreator, @RequestBody UserOntology userOntology) throws OWLOntologyCreationException {
		//create shared or individual ontologies
		entityOntologies = new ArrayList<OntologyIRI> ();
		qualityOntologies = new ArrayList<OntologyIRI>();
		String userId = userOntology.getUserId();
		if(userId == null || userId.isEmpty()){
			return createSharedOntology(userOntology.getUserOntologies()/*ontologySearchResultCreator*/);
		}else{
			return createInvidualOntology(userId, userOntology.getUserOntologies()/*, ontologySearchResultCreator*/);
		}
	}

	public boolean createSharedOntology(ArrayList<String> ontos/*OntologySearchResultCreator ontologySearchResultCreator*/) throws OWLOntologyCreationException {
		//this.ontologySearchResultCreator = ontologySearchResultCreator;

		/*OntologyIRI CAREX = new OntologyIRI(new File(ontologyDir, "carex.owl").getAbsolutePath(), 
				"http://biosemantics.arizona.edu/ontologies/carex", "CAREX");
		OntologyIRI PO = new OntologyIRI(new File(ontologyDir, "po.owl").getAbsolutePath(),
				"http://purl.obolibrary.org/obo/po", "PO");
		 OntologyIRI PATO = new OntologyIRI(new File(ontologyDir, "pato.owl").getAbsolutePath(),
				"http://purl.obolibrary.org/obo/pato", "PATO");

		this.entityOntologies.add(PO);
		this.entityOntologies.add(CAREX);
		this.qualityOntologies.add(PATO);
		this.qualityOntologies.add(CAREX);
		setUpWorkbench(entityOntologies, qualityOntologies);*/
		//copy base ontologies to user ontologies

		//String onto = "carex"; //both entity and quality
		for(String onto: ontos){

		File ontoD = new File(ontologyDir, onto+".owl");

		OntologyIRI EXP = new OntologyIRI(ontoD.getAbsolutePath(), 
				ontologyIRIs.get(onto), onto); //for experiments
		entityOntologies.add(EXP); //EXP
		////System.outprintln("####################entityOntolgies.added:"+EXP.getName());
		qualityOntologies.add(EXP);

		if(! this.allLiveEntityOntologies.contains(EXP)) this.allLiveEntityOntologies.add(EXP);
		if(! this.allLiveQualityOntologies.contains(EXP)) this.allLiveQualityOntologies.add(EXP);

		setUpWorkbench(entityOntologies, qualityOntologies);
		}

		return true;
	}

	private boolean createInvidualOntology(String userId, ArrayList<String> userOntologies/*, OntologySearchResultCreator ontologySearchResultCreator*/) 
			throws OWLOntologyCreationException{


		//copy base ontologies to user ontologies
		for(String onto: userOntologies){
			////System.outprintln("####################user ontology:"+onto);
			File ontoS = new File(ontologyDir, onto+".owl");
			File ontoD = new File(ontologyDir, onto+"_"+userId+".owl"); //ontology indexed as EXP_1.owl, EXP_2.owl, 1 and 2 are user ids.
			try{
				if(!ontoD.exists())
					Files.copy(ontoS.toPath(), ontoD.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}catch(Exception e){
				e.printStackTrace();
				throw new OWLOntologyCreationException("Creating individual ontology error:"+e.getMessage());
			}

			OntologyIRI EXP = new OntologyIRI(ontoD.getAbsolutePath(), 
					ontologyIRIs.get(onto), onto+"_"+userId); //for experiments
			entityOntologies.add(EXP); //EXP_1
			////System.outprintln("####################entityOntolgies.added:"+EXP.getName());
			qualityOntologies.add(EXP);

			if(! this.allLiveEntityOntologies.contains(EXP)) this.allLiveEntityOntologies.add(EXP);
			if(! this.allLiveQualityOntologies.contains(EXP)) this.allLiveQualityOntologies.add(EXP);
		}
		setUpWorkbench(entityOntologies, qualityOntologies);

		return true;
	}

	private void setUpWorkbench(ArrayList<OntologyIRI> entityOntologies, ArrayList<OntologyIRI> qualityOntologies) {
		//find shared ontologies between entity and quality lists
		ArrayList<OntologyIRI> shared = new ArrayList<OntologyIRI> ();
		for(OntologyIRI e : entityOntologies){ 
			for(OntologyIRI q : qualityOntologies) {
				if(e.equals(q))
					shared.add(e);
			}
		}

		//add shared
		for(OntologyIRI o : shared) {
			HashSet<String> entityOntologyNames = new HashSet<String>();
			HashSet<String> qualityOntologyNames = new HashSet<String>();
			qualityOntologyNames.add(o.getName());
			entityOntologyNames.add(o.getName());

			FileSearcher searcher = new FileSearcher(entityOntologyNames, qualityOntologyNames,
					ontologyDir, wordNetDir, false);

			LOGGER.info("created searcher for shared e-q ontology:" + entityOntologyNames);
			OWLOntologyManager owlOntologyManager = searcher.getOwlOntologyManager();

			OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(o.getIri()));
			Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
			ontologies.add(owlOntology);
			////System.outprintln("####################owlOntology="+owlOntology);
			OntologyAccess ontologyAccess  = new OntologyAccess(ontologies);

			this.searchersMap.put(o.getName(), searcher);
			this.ontologyAccessMap.put(o.getName(), ontologyAccess);
			this.owlOntologyManagerMap.put(o.getName(), owlOntologyManager);
			//this.termDefinitionMap.put(o.getName(), new Hashtable<String, String>());
		}

		//add the rest
		for(OntologyIRI o : entityOntologies) {
			if(shared.contains(o)) continue;
			HashSet<String> entityOntologyNames = new HashSet<String>();
			entityOntologyNames.add(o.getName());

			FileSearcher searcher = new FileSearcher(entityOntologyNames, new HashSet<String>(), 
					ontologyDir, wordNetDir, false);

			LOGGER.info("created searcher for " + entityOntologyNames);
			OWLOntologyManager owlOntologyManager = searcher.getOwlOntologyManager();

			OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(o.getIri()));
			Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
			ontologies.add(owlOntology);
			////System.outprintln("####################owlOntology="+owlOntology);
			OntologyAccess ontologyAccess  = new OntologyAccess(ontologies);

			this.searchersMap.put(o.getName(), searcher);
			this.ontologyAccessMap.put(o.getName(), ontologyAccess);
			this.owlOntologyManagerMap.put(o.getName(), owlOntologyManager);
			//this.termDefinitionMap.put(o.getName(), new Hashtable<String, String>());
		}

		for(OntologyIRI o : qualityOntologies) {
			if(shared.contains(o)) continue;
			HashSet<String> qualityOntologyNames = new HashSet<String>();
			qualityOntologyNames.add(o.getName());
			FileSearcher searcher = new FileSearcher(new HashSet<String>(), qualityOntologyNames, 
					ontologyDir, wordNetDir, false);
			OWLOntologyManager owlOntologyManager = searcher.getOwlOntologyManager();
			OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(o.getIri()));
			Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
			ontologies.add(owlOntology);
			OntologyAccess ontologyAccess  = new OntologyAccess(ontologies);

			this.searchersMap.put(o.getName(), searcher);
			this.ontologyAccessMap.put(o.getName(), ontologyAccess);
			this.owlOntologyManagerMap.put(o.getName(), owlOntologyManager);
			//this.termDefinitionMap.put(o.getName(), new Hashtable<String, String>());
		}
	}




	@GetMapping(value = "/{ontology}/search", produces = { MediaType.APPLICATION_JSON_VALUE })
	public OntologySearchResult search(@PathVariable String ontology, @RequestParam String term, @RequestParam Optional<String> ancestorIRI,
			@RequestParam Optional<String> parent, @RequestParam Optional<String> relation, @RequestParam Optional<String> user) throws Exception {
		String usrid = "";
		String ontoName = ontology;
		if(user.isPresent()){
			usrid = user.get();
			ontoName = ontology+"_"+usrid;
		}
		OntologyIRI oIRI = getOntologyIRI(ontoName);
		////System.outprintln("/search ####################search ontoName="+ontoName);

		OWLReasoner reasoner = null;
		OWLDataFactory owlDataFactory = null;
		if(ancestorIRI.isPresent()){
			//use selected ontology
			OWLOntologyManager owlOntologyManager = this.owlOntologyManagerMap.get(ontoName);//this.owlOntologyManagerMap.get(oIRI);
			OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(oIRI.getIri()));
			JFactFactory reasonerFactory = new JFactFactory();
			reasoner = reasonerFactory.createNonBufferingReasoner(owlOntology);
			owlDataFactory = owlOntologyManager.getOWLDataFactory();
			reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		}
		//ontoName: EXP_1 or EXP, CAREX, etc.
		if(!searchersMap.containsKey(ontoName)) 
			throw new IllegalArgumentException();

		List<OntologyEntry> entries = new ArrayList<OntologyEntry>();
		FileSearcher searcher = this.searchersMap.get(ontoName);
		//searcher.updateSearcher(oIRI);
		if(this.isQualityOntology(ontoName)) {
			//System.out.println("/search q "+ontoName +"####################searcher="+searcher);
			//System.out.println("/search q "+ontoName +"####################ontology count ="+
			//searcher.getOwlOntologyManager().getOntologies().size());
			//System.out.println("/search q "+ontoName +"####################ontology axiom count ="+
			//searcher.getOwlOntologyManager().getOntology(IRI.create(oIRI.getIri())).getAxiomCount());
			//System.out.println("/search q "+ontoName +"####################ontology api  ="+
			//searcher.getOntoLookupClient().ontoutil.OWLqualityOntoAPIs.get(0));
			entries.addAll(searcher.getQualityEntries(term));
		}
		if(this.isEntityOntology(ontoName)) {
			//System.out.println("/searcher ####################searcher="+searcher);
			//System.out.println("/search e "+ontoName +"####################ontology count ="+
			//searcher.getOwlOntologyManager().getOntologies().size());
			//System.out.println("/search e "+ontoName +"####################ontology axiom count ="+
			//searcher.getOwlOntologyManager().getOntology(IRI.create(oIRI.getIri())).getAxiomCount());
			//System.out.println("/search e "+ontoName+ "####################ontology api  ="+
			//searcher.getOntoLookupClient().ontoutil.OWLentityOntoAPIs.get(0));
			entries.addAll(
					searcher.getEntityEntries(term, parent.orElse(""), relation.orElse("")));
		}


		if(ancestorIRI.isPresent()){
			//required superclass
			OWLClass superClazz = owlDataFactory.getOWLClass(IRI.create(ancestorIRI.get().replaceAll("%23", "#").replaceAll("%20", "_").replaceAll("\\s+", "_"))); //use either # or / in iri
			//filter the result entries
			List<OntologyEntry> fentries = new ArrayList<OntologyEntry>();
			for(OntologyEntry result: entries){
				OWLClass thisClaz = owlDataFactory.getOWLClass(IRI.create(result.getClassIRI()));
				//find all superclasses of this matching class
				Set <OWLClass> superClzz = reasoner.getSuperClasses(thisClaz,false).entities().collect(Collectors.toSet());
				for(OWLClass sup: superClzz){
					//any super class matches the required?
					if(sup.getIRI().toString().compareTo(superClazz.getIRI().toString())==0) fentries.add(result);
				}
			}
			entries = fentries;
		}
		return ontologySearchResultCreator.create(ontoName, entries, 
				this.ontologyAccessMap.get(ontoName), 
				this.owlOntologyManagerMap.get(ontoName).getOntology(IRI.create(oIRI.getIri())),
				this.owlOntologyManagerMap.get(ontoName));
	}

	/**
	 * attempt to add an exact synonym to a class
	 * if such synonym is not also a synonym of another class, add as exact synonym as requested
	 * else add as a bsynonym to all related classes (change e to b for all classes)
	 * @param synonym
	 * @return
	 */
	@PostMapping(value = "/esynonym", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ChangeApplied createESynonym(@RequestBody Synonym synonym) {
		//which ontology to use
		String usrid = "";
		String ontoName = synonym.getOntology();
		if(!synonym.getUser().isEmpty()){
			usrid = synonym.getUser();
			ontoName = ontoName+"_"+usrid;
		}
		OntologyIRI oIRI = getOntologyIRI(ontoName);
		////System.outprintln("####################createESynonym ontoName="+ontoName);
		////System.outprintln("####################Iri="+oIRI.getIri());

		//use the selected ontology		
		OWLOntologyManager owlOntologyManager = this.owlOntologyManagerMap.get(ontoName);// this.owlOntologyManagerMap.get(oIRI);
		OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(oIRI.getIri()));
		OWLDataFactory owlDataFactory = owlOntologyManager.getOWLDataFactory();

		String synonymTerm = synonym.getTerm();
		ArrayList<OWLClass> classesWesynonym = new ArrayList<OWLClass>();//exact_syno
		ArrayList<OWLClass> classesWnsynonym = new ArrayList<OWLClass>();//not_recommended_synonym
		findClassesWithExactSynonym(owlOntology, owlDataFactory, synonymTerm, classesWesynonym, classesWnsynonym);
		OWLClass clazz = owlDataFactory.getOWLClass(synonym.getClassIRI());
		ChangeApplied c = null;
		
		//if esynonym is a not_recommended_synonym, report error
		if(!classesWnsynonym.isEmpty()){
			return ChangeApplied.valueOf("NO_OPERATION"); //synonym is a not recommended synonmy, so not action
		}else if(classesWesynonym.isEmpty()){
			OWLAnnotationProperty exactSynonymProperty = 
					owlDataFactory.getOWLAnnotationProperty(IRI.create(AnnotationProperty.EXACT_SYNONYM.getIRI()));
			OWLAnnotation synonymAnnotation = owlDataFactory.getOWLAnnotation(
					exactSynonymProperty, owlDataFactory.getOWLLiteral(synonymTerm));
			OWLAxiom synonymAxiom = owlDataFactory.getOWLAnnotationAssertionAxiom(clazz.getIRI(), synonymAnnotation);
	
			c = owlOntologyManager.addAxiom(owlOntology, synonymAxiom);
		}else{
			//add as bsynonym
			for(OWLClass clz: classesWesynonym){
				//add bsynonym
				OWLAnnotationProperty bSynonymProperty = 
						owlDataFactory.getOWLAnnotationProperty(IRI.create(AnnotationProperty.BROAD_SYNONYM.getIRI()));
				OWLAnnotation synonymAnnotation = owlDataFactory.getOWLAnnotation(
						bSynonymProperty, owlDataFactory.getOWLLiteral(synonymTerm));
				OWLAxiom synonymAxiom = owlDataFactory.getOWLAnnotationAssertionAxiom(clz.getIRI(), synonymAnnotation);
				owlOntologyManager.addAxiom(owlOntology, synonymAxiom);
				synonymAxiom = owlDataFactory.getOWLAnnotationAssertionAxiom(clazz.getIRI(), synonymAnnotation);
				owlOntologyManager.addAxiom(owlOntology, synonymAxiom);
				
				//remove esynonym
				OWLAnnotationProperty eSynonymProperty = 
						owlDataFactory.getOWLAnnotationProperty(IRI.create(AnnotationProperty.EXACT_SYNONYM.getIRI()));
				OWLAnnotation esynonymAnnotation = owlDataFactory.getOWLAnnotation(
						eSynonymProperty, owlDataFactory.getOWLLiteral(synonymTerm));
				OWLAxiom esynonymAxiom = owlDataFactory.getOWLAnnotationAssertionAxiom(clz.getIRI(), esynonymAnnotation);
				RemoveAxiom remove = new RemoveAxiom(owlOntology, esynonymAxiom);
				owlOntologyManager.applyChange(remove);
				
				//add note about the change
				OWLAnnotationProperty noteProperty = 
						owlDataFactory.getOWLAnnotationProperty(IRI.create("http://purl.obolibrary.org/obo/IAO_0000116")); //editor_note
				OWLAnnotation noteAnnotation = owlDataFactory.getOWLAnnotation(
						noteProperty, owlDataFactory.getOWLLiteral("Adding '"+synonym.getTerm() +"' as exact_synomyn to "+synonym.getClassIRI() + 
								" by "+synonym.getExperts() + " triggered the change of making the term a broad_synonym on this class (as opposed to the initial extact_synonym)"));
				OWLAxiom noteAxiom = owlDataFactory.getOWLAnnotationAssertionAxiom(clz.getIRI(), noteAnnotation);
				owlOntologyManager.addAxiom(owlOntology, noteAxiom);
				
			}
			c = ChangeApplied.valueOf("SUCCESSFULLY"); 
		}
		
		//refresh ontology search environment after the addition
		FileSearcher searcher = this.searchersMap.get(ontoName);
		searcher.updateSearcher(IRI.create(oIRI.getIri()));

		//save ontology
		//saveOntology(ontoName, oIRI);
		
		return c; //return the last change
	}
	
	

	private void findClassesWithExactSynonym(OWLOntology owlOntology, OWLDataFactory owlDataFactory, String esynonym, ArrayList<OWLClass> classesWesynonym, ArrayList<OWLClass> classesWnsynonym) {
		
		//esyn = "term"
		OWLAnnotationProperty eSynonymProperty = 
				owlDataFactory.getOWLAnnotationProperty(IRI.create(AnnotationProperty.EXACT_SYNONYM.getIRI()));
		
		OWLAnnotationProperty nSynonymProperty = 
				owlDataFactory.getOWLAnnotationProperty(IRI.create("http://biosemantics.arizona.edu/ontologies/carex#has_not_recommended_synonym"));
		
		
		//loop through all classes to find matching classes
	    Set<OWLClass> set = owlOntology.classesInSignature().collect(Collectors.toSet());
	    for(OWLClass clz: set){
	    	Set<OWLAnnotation> annos = EntitySearcher.getAnnotationObjects(clz, owlOntology, eSynonymProperty).collect(Collectors.toSet());
	    	for(OWLAnnotation anno: annos){
	    		if(anno.getValue().equals(owlDataFactory.getOWLLiteral(esynonym))){
	    			classesWesynonym.add(clz);
	    		}
  		
	    	}
	    	
	    	annos = EntitySearcher.getAnnotationObjects(clz, owlOntology, nSynonymProperty).collect(Collectors.toSet());
	    	for(OWLAnnotation anno: annos){
	    		if(anno.getValue().equals(owlDataFactory.getOWLLiteral(esynonym))){
	    			classesWnsynonym.add(clz);
	    		}
	    	}
	    }
		
	}

	@PostMapping(value = "/bsynonym", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ChangeApplied createBSynonym(@RequestBody Synonym synonym) {
		//which ontology to use
		String usrid = "";
		String ontoName = synonym.getOntology();
		if(!synonym.getUser().isEmpty()){
			usrid = synonym.getUser();
			ontoName = ontoName+"_"+usrid;
		}
		OntologyIRI oIRI = getOntologyIRI(ontoName);
		////System.outprintln("####################createBSynonym ontoName="+ontoName);
		////System.outprintln("####################Iri="+oIRI.getIri());

		//use the selected ontology		
		OWLOntologyManager owlOntologyManager = this.owlOntologyManagerMap.get(ontoName); //this.owlOntologyManagerMap.get(oIRI);
		OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(oIRI.getIri()));
		OWLDataFactory owlDataFactory = owlOntologyManager.getOWLDataFactory();

		String synonymTerm = synonym.getTerm();
		OWLClass clazz = owlDataFactory.getOWLClass(synonym.getClassIRI());
		OWLAnnotationProperty exactSynonymProperty = 
				owlDataFactory.getOWLAnnotationProperty(IRI.create(AnnotationProperty.BROAD_SYNONYM.getIRI()));
		OWLAnnotation synonymAnnotation = owlDataFactory.getOWLAnnotation(
				exactSynonymProperty, owlDataFactory.getOWLLiteral(synonymTerm));
		OWLAxiom synonymAxiom = owlDataFactory.getOWLAnnotationAssertionAxiom(clazz.getIRI(), synonymAnnotation);

		ChangeApplied c = owlOntologyManager.addAxiom(owlOntology, synonymAxiom);
		
		//refresh ontology search environment after the addition
		FileSearcher searcher = this.searchersMap.get(ontoName);
		searcher.updateSearcher(IRI.create(oIRI.getIri()));
		//save ontology
		//saveOntology(ontoName, oIRI);
		
		return c;
	}
	
	/**
	 * return all classes with this property
	 * @param ontology
	 * @param propertyIRI
	 * @return
	 * @throws Exception
	 */
	@GetMapping(value = "/{ontology}/getStandardCollection", produces = { MediaType.APPLICATION_JSON_VALUE })
	public OntologySearchResult getClassesWithProperty(@PathVariable String ontology, @RequestParam Optional<String> user) throws Exception {
		
		String usrid = "";
		String ontoName = ontology;
		if(user.isPresent()){
			usrid = user.get();
			ontoName = ontology+"_"+usrid;
		}
		OntologyIRI oIRI = getOntologyIRI(ontoName);
		////System.outprintln("/search ####################search ontoName="+ontoName);

		OWLReasoner reasoner = null;
		OWLDataFactory owlDataFactory = null;

		OWLOntologyManager owlOntologyManager = this.owlOntologyManagerMap.get(ontoName);//this.owlOntologyManagerMap.get(oIRI);
		OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(oIRI.getIri()));
		JFactFactory reasonerFactory = new JFactFactory();
		reasoner = reasonerFactory.createNonBufferingReasoner(owlOntology);//efficiency concern: create a reasoner for each request is not very efficient
		owlDataFactory = owlOntologyManager.getOWLDataFactory();
		reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		
		if(!searchersMap.containsKey(ontoName)) 
			throw new IllegalArgumentException();
		
		List<OntologyEntry> entries = new ArrayList<OntologyEntry>();
		//define a class with this property
		//find all subclasses of this class
		
		//String classIRI = oIRI.getIri() + "#query";
		OWLClass propValue = owlDataFactory.getOWLClass("http://biosemantics.arizona.edu/ontologies/carex#carex_standard_character_set");
		
		OWLObjectProperty property = owlDataFactory.getOWLObjectProperty(IRI.create("http://biosemantics.arizona.edu/ontologies/carex#in_collection"));

		//property some valueIRI, for example in_collection some carex standard collection
		OWLClassExpression queryExpression = owlDataFactory.getOWLObjectSomeValuesFrom(property, propValue); 
		Set <OWLClass> subClzz = reasoner.getSubClasses(queryExpression, true).entities().collect(Collectors.toSet());
		for(OWLClass c: subClzz){
			//public OntologyEntry(Ontology ontology, String iri, Type type, double score, String label, String definition, String parentLabel, String matchType) {
			OWLClass parent = (reasoner.getSuperClasses(c, true).entities().collect(Collectors.toSet())).iterator().next();
			String parentLabel = labelFor(parent, owlOntology, owlDataFactory);	
			entries.add(new OntologyEntry(Ontology.carex, c.getIRI().toString(), Type.QUALITY, 1.0, labelFor(c, owlOntology, owlDataFactory),
					"", parentLabel, "quality"));
			
		}
		return ontologySearchResultCreator.create(ontoName, entries, 
				this.ontologyAccessMap.get(ontoName), 
				this.owlOntologyManagerMap.get(ontoName).getOntology(IRI.create(oIRI.getIri())),
				this.owlOntologyManagerMap.get(ontoName));
	}

	
	@PostMapping(value = "/definition", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ChangeApplied addDefinition(@RequestBody Definition definition) {
		String definitionIRI = AnnotationProperty.DEFINITION.getIRI();
		return addAnnotation(definition, definitionIRI);
	}
	
	@PostMapping(value = "/comment", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ChangeApplied addComment(@RequestBody Comment comment) {
		String commentIRI = OntologySearchController.comment;
		return addAnnotation(comment, commentIRI);
	}

	private ChangeApplied addAnnotation(AnAnnotation annotation, String annotationIRI) {
		//which ontology to use
		String usrid = "";
		String ontoName = annotation.getOntology();
		if(!annotation.getUser().isEmpty()){
			usrid = annotation.getUser();
			ontoName = ontoName+"_"+usrid;
		}
		OntologyIRI oIRI = getOntologyIRI(ontoName);
		////System.outprintln("####################createBSynonym ontoName="+ontoName);
		////System.outprintln("####################Iri="+oIRI.getIri());

		//use the selected ontology		
		OWLOntologyManager owlOntologyManager = this.owlOntologyManagerMap.get(ontoName); //this.owlOntologyManagerMap.get(oIRI);
		OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(oIRI.getIri()));
		OWLDataFactory owlDataFactory = owlOntologyManager.getOWLDataFactory();

		String def = annotation.getAnnotationContent();
		OWLClass clazz = owlDataFactory.getOWLClass(annotation.getClassIRI());
		OWLAnnotationProperty definitionProperty = 
				owlDataFactory.getOWLAnnotationProperty(IRI.create(annotationIRI));
		OWLAnnotation synonymAnnotation = owlDataFactory.getOWLAnnotation(
				definitionProperty, owlDataFactory.getOWLLiteral(def));
		OWLAxiom definitionAxiom = owlDataFactory.getOWLAnnotationAssertionAxiom(clazz.getIRI(), synonymAnnotation);

		ChangeApplied c = owlOntologyManager.addAxiom(owlOntology, definitionAxiom);
		
		//refresh ontology search environment after the addition
		//FileSearcher searcher = this.searchersMap.get(ontoName);
		//searcher.updateSearcher(oIRI);

		//save ontology
		//saveOntology(ontoName, oIRI);
		
		return c;
	}
	
	/**
	 * add new class to the ontology
	 * @param c
	 * @return
	 */
	@PostMapping(value = "/class", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public String createClass(@RequestBody Class c) {
		//which ontology to use
		String usrid = "";
		String ontoName = c.getOntology();
		if(!c.getUser().isEmpty()){
			usrid = c.getUser();
			ontoName = ontoName+"_"+usrid;
		}
		OntologyIRI oIRI = getOntologyIRI(ontoName);
		////System.outprintln("/class ####################class ontoName="+ontoName);
		////System.outprintln("/class ####################Iri="+oIRI.getIri());

		//use the selected ontology		
		OWLOntologyManager owlOntologyManager = this.owlOntologyManagerMap.get(ontoName);//this.owlOntologyManagerMap.get(oIRI);
		OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(oIRI.getIri()));
		////System.outprintln("/class ####################owlOntology="+owlOntology);
		////System.outprintln("/class ####################owlOntology axiom count (before)="+owlOntology.getAxiomCount());
		OWLDataFactory owlDataFactory = owlOntologyManager.getOWLDataFactory();

		String subclassIRI = oIRI.getIri() + "#" + c.getTerm().trim().replaceAll("\\s+", "_").replaceAll("%20", "_");
		OWLClass subclass = owlDataFactory.getOWLClass(subclassIRI);

		OWLAnnotationProperty labelProperty =
				owlDataFactory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
		OWLLiteral labelLiteral = owlDataFactory.getOWLLiteral(c.getTerm().trim().replaceAll("%20", " "));
		OWLAnnotation labelAnnotation = owlDataFactory.getOWLAnnotation(labelProperty, labelLiteral);
		OWLAxiom labelAxiom = owlDataFactory.getOWLAnnotationAssertionAxiom(subclass.getIRI(), labelAnnotation);
		ChangeApplied change = owlOntologyManager.addAxiom(owlOntology, labelAxiom);
		if(change != ChangeApplied.SUCCESSFULLY && change !=ChangeApplied.NO_OPERATION )
			return change.name();

		OWLClass superclass = owlDataFactory.getOWLClass(c.getSuperclassIRI().trim().replaceAll("\\s+", "_").replaceAll("%20", "_"));
		OWLAxiom subclassAxiom = owlDataFactory.getOWLSubClassOfAxiom(subclass, superclass);
		change = owlOntologyManager.addAxiom(owlOntology, subclassAxiom);
		if(change != ChangeApplied.SUCCESSFULLY && change !=ChangeApplied.NO_OPERATION)
			return change.name();

		OWLAnnotationProperty definitionProperty = 
				owlDataFactory.getOWLAnnotationProperty(IRI.create(AnnotationProperty.DEFINITION.getIRI()));
		OWLAnnotation definitionAnnotation = owlDataFactory.getOWLAnnotation
				(definitionProperty, owlDataFactory.getOWLLiteral(c.getDefinition())); 
		OWLAxiom definitionAxiom = owlDataFactory.getOWLAnnotationAssertionAxiom(
				subclass.getIRI(), definitionAnnotation); 
		change = owlOntologyManager.addAxiom(owlOntology, definitionAxiom);

		if(change != ChangeApplied.SUCCESSFULLY && change !=ChangeApplied.NO_OPERATION)
			return change.name();

		if(c.getElucidation() != null && !c.getElucidation().isEmpty()) {
			OWLAnnotationProperty elucidationProperty = 
					owlDataFactory.getOWLAnnotationProperty(IRI.create(ELUCIDATION));
			OWLAnnotation elucidationAnnotation = owlDataFactory.getOWLAnnotation
					(elucidationProperty, owlDataFactory.getOWLLiteral(c.getElucidation())); 
			OWLAxiom elucidationAxiom = owlDataFactory.getOWLAnnotationAssertionAxiom(
					subclass.getIRI(), elucidationAnnotation); 
			change = owlOntologyManager.addAxiom(owlOntology, elucidationAxiom);
			if(change != ChangeApplied.SUCCESSFULLY && change !=ChangeApplied.NO_OPERATION)
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
			if(change != ChangeApplied.SUCCESSFULLY && change !=ChangeApplied.NO_OPERATION)
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
			if(change != ChangeApplied.SUCCESSFULLY&& change !=ChangeApplied.NO_OPERATION)
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
			if(change != ChangeApplied.SUCCESSFULLY&& change !=ChangeApplied.NO_OPERATION)
				return change.name();
		}

		if(c.getExampleOfUsage() != null && !c.getExampleOfUsage().isEmpty()) {
			String[] exps = c.getExampleOfUsage().split("#");
			for(String example: exps){
				OWLAnnotationProperty DefinitionSrcProperty = 
						owlDataFactory.getOWLAnnotationProperty(IRI.create(exampleOfUsage));
				OWLAnnotation DefinitionSrcAnnotation = owlDataFactory.getOWLAnnotation
						(DefinitionSrcProperty, owlDataFactory.getOWLLiteral(example)); 
				OWLAxiom DefinitionSrcAxiom = owlDataFactory.getOWLAnnotationAssertionAxiom(
						subclass.getIRI(), DefinitionSrcAnnotation); 
				change = owlOntologyManager.addAxiom(owlOntology, DefinitionSrcAxiom);
				if(change != ChangeApplied.SUCCESSFULLY&& change !=ChangeApplied.NO_OPERATION)
					return change.name();
			}
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
				clsB = parser.parseManchesterExpression(c.getLogicDefinition()); //"'part of' some 'physical entity'"
			}catch(Exception e){
				String msg = e.getMessage();
				//////System.outprintln(msg);
				return msg;
			}
			OWLAxiom def = owlDataFactory.getOWLEquivalentClassesAxiom(subclass, clsB);
			change = owlOntologyManager.addAxiom(owlOntology, def);
			if(change != ChangeApplied.SUCCESSFULLY && change !=ChangeApplied.NO_OPERATION)
				return change.name();

			/*write out ontology for test
			File file = new File("C:/Users/hongcui/Documents/research/AuthorOntology/Data/CarexOntology/carex_tiny.owl");            
			OWLDocumentFormat format = manager.getOntologyFormat(carex);    
			try{
				manager.saveOntology(carex, format, IRI.create(file.toURI()));
			}catch(Exception e){
				////System.outprintln(e.getStackTrace());
			}*/

		}

		////System.outprintln("/class ####################owlOntology axiom count (after)="+owlOntology.getAxiomCount());

		//refresh ontology search environment after the addition
		FileSearcher searcher = this.searchersMap.get(ontoName);
		searcher.updateSearcher(IRI.create(oIRI.getIri()));
		////System.outprintln("/class ####################refreshed searcher="+searcher);
		
		//save ontology
		//saveOntology(ontoName, oIRI);

		return subclass.getIRI().getIRIString();
	}

	@PostMapping(value = "/partOf", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ChangeApplied createPartOf(@RequestBody PartOf partOf) {
		//which ontology to use
		String usrid = "";
		String ontoName = partOf.getOntology();
		if(!partOf.getUser().isEmpty()){
			usrid = partOf.getUser();
			ontoName = ontoName+"_"+usrid;
		}
		OntologyIRI oIRI = getOntologyIRI(ontoName);

		//use the selected ontology		

		OWLOntologyManager owlOntologyManager = this.owlOntologyManagerMap.get(ontoName); //this.owlOntologyManagerMap.get(oIRI);
		OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(oIRI.getIri()));
		OWLDataFactory owlDataFactory = owlOntologyManager.getOWLDataFactory();

		OWLClass bearer = owlDataFactory.getOWLClass(partOf.getBearerIRI());
		OWLClass part = owlDataFactory.getOWLClass(partOf.getPartIRI());

		OWLObjectProperty partOfProperty = 
				owlDataFactory.getOWLObjectProperty(IRI.create(AnnotationProperty.PART_OF.getIRI()));
		OWLClassExpression partOfExpression = 
				owlDataFactory.getOWLObjectSomeValuesFrom(partOfProperty, bearer);
		OWLAxiom partOfAxiom = owlDataFactory.getOWLSubClassOfAxiom(part, partOfExpression);
		ChangeApplied c= owlOntologyManager.addAxiom(owlOntology, partOfAxiom);
		
		//refresh ontology search environment after the addition
		FileSearcher searcher = this.searchersMap.get(ontoName);
		searcher.updateSearcher(IRI.create(oIRI.getIri()));

		//save ontology
		//saveOntology(ontoName, oIRI);
		
		return c;
	}

	@PostMapping(value = "/hasPart", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ChangeApplied createHasPart(@RequestBody HasPart hasPart) {
		//which ontology to use
		String usrid = "";
		String ontoName = hasPart.getOntology();
		if(!hasPart.getUser().isEmpty()){
			usrid = hasPart.getUser();
			ontoName = ontoName+"_"+usrid;
		}
		OntologyIRI oIRI = getOntologyIRI(ontoName);

		//use the selected ontology		

		OWLOntologyManager owlOntologyManager = this.owlOntologyManagerMap.get(ontoName); //this.owlOntologyManagerMap.get(oIRI);
		OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(oIRI.getIri()));
		OWLDataFactory owlDataFactory = owlOntologyManager.getOWLDataFactory();

		OWLClass bearer = owlDataFactory.getOWLClass(hasPart.getBearerIRI());
		OWLClass part = owlDataFactory.getOWLClass(hasPart.getPartIRI());

		OWLObjectProperty partOfProperty = 
				owlDataFactory.getOWLObjectProperty(IRI.create(HAS_PART));
		OWLClassExpression partOfExpression = 
				owlDataFactory.getOWLObjectSomeValuesFrom(partOfProperty, part);
		OWLAxiom partOfAxiom = owlDataFactory.getOWLSubClassOfAxiom(bearer, partOfExpression);
		ChangeApplied c = owlOntologyManager.addAxiom(owlOntology, partOfAxiom);
		//refresh ontology search environment after the addition
		FileSearcher searcher = this.searchersMap.get(ontoName);
		searcher.updateSearcher(IRI.create(oIRI.getIri()));
		//save ontology
		//saveOntology(ontoName, oIRI);
		
		return c;
	}
	
	/**
	 * subclass: red
	 * superclass: coloration
	 * @param superclass
	 * @return
	 */
	@PostMapping(value = "/moveFromToreviewToSuperclass", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ChangeApplied moveFromToReviewToSuperclass(@RequestBody Superclass superclass) {
		//which ontology to use
		String usrid = "";
		String ontoName = superclass.getOntology();
		if(!superclass.getUser().isEmpty()){
			usrid = superclass.getUser();
			ontoName = ontoName+"_"+usrid;
		}
		OntologyIRI oIRI = getOntologyIRI(ontoName);

		//use the selected ontology		

		OWLOntologyManager owlOntologyManager = this.owlOntologyManagerMap.get(ontoName); //this.owlOntologyManagerMap.get(oIRI);
		OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(oIRI.getIri()));
		OWLDataFactory owlDataFactory = owlOntologyManager.getOWLDataFactory();

		//attachment_%28structure%29
		
		String osubIRI = superclass.getSubclassIRI();
		String subIRI = osubIRI;
		if(osubIRI.contains("_%28")){
			subIRI = osubIRI.replaceFirst("_%28.*", ""); //attachment
		}
		OWLClass osub = owlDataFactory.getOWLClass(osubIRI); //found the old class
		OWLClass sub = owlDataFactory.getOWLClass(subIRI); //created a new class
		OWLClass supr = owlDataFactory.getOWLClass(superclass.getSuperclassIRI());
		
		
		OWLClass toreview = owlDataFactory.getOWLClass(IRI.create("http://biosemantics.arizona.edu/ontologies/carex#toreview"));
		OWLAxiom toreviewSubAxiom = owlDataFactory.getOWLSubClassOfAxiom(osub, toreview);
		RemoveAxiom remove = new RemoveAxiom(owlOntology, toreviewSubAxiom);
		owlOntologyManager.applyChange(remove);
		if(!osub.equals(sub)){
			//move annotation/object properties of osub to sub, then deprecate osub
			//annotations
			Set<OWLAnnotationAssertionAxiom> aaas = EntitySearcher.getAnnotationAssertionAxioms(osub, owlOntology).collect(Collectors.toSet());	
			for(OWLAnnotationAssertionAxiom aaa: aaas){
				OWLAnnotation annot = null;
				if(aaa.getProperty().equals(owlDataFactory.getRDFSLabel())){
					//String av = aaa.getValue().literalValue().toString();
					String av = superclass.getSubclassTerm();
					//remove _() from the label
					//av: Optional["attachment (structure)"^^xsd:string] or attachment (structure)
					av = av.replaceFirst("^.*?\"", "").replaceFirst("\".*$", "").replaceFirst("\\s+\\(.*$", "");
					annot = owlDataFactory.getOWLAnnotation(aaa.getProperty(), owlDataFactory.getOWLLiteral(av));
				}else{
					annot = owlDataFactory.getOWLAnnotation(aaa.getProperty(), aaa.getValue());
				}
				OWLAxiom aAxiom = owlDataFactory.getOWLAnnotationAssertionAxiom(sub.getIRI(), annot);
				owlOntologyManager.addAxiom(owlOntology, aAxiom);				
			}
			
			//object properties
			//TODO: couldn't figure out how to do this. 
			
			OWLAnnotationAssertionAxiom dAxiom = owlDataFactory.getDeprecatedOWLAnnotationAssertionAxiom(osub.getIRI()); //deprecate the old class
			owlOntologyManager.addAxiom(owlOntology, dAxiom);
		}
		
		//add note about the change
		OWLAnnotationProperty noteProperty = 
				owlDataFactory.getOWLAnnotationProperty(IRI.create("http://purl.obolibrary.org/obo/IAO_0000116")); //editor_note
		OWLAnnotation noteAnnotation = owlDataFactory.getOWLAnnotation(
				noteProperty, owlDataFactory.getOWLLiteral("Moved "+superclass.getSubclassIRI() +" from class toreview to subclass of "+superclass.getSuperclassIRI() + 
						" by "+superclass.getExperts() + " via the mobile app "));
		OWLAxiom noteAxiom = owlDataFactory.getOWLAnnotationAssertionAxiom(sub.getIRI(), noteAnnotation);
		owlOntologyManager.addAxiom(owlOntology, noteAxiom);
		
		OWLAxiom subclassAxiom = owlDataFactory.getOWLSubClassOfAxiom(sub, supr);
		ChangeApplied c= owlOntologyManager.addAxiom(owlOntology, subclassAxiom);
		
		
		
		//refresh ontology search environment after the addition
		FileSearcher searcher = this.searchersMap.get(ontoName);
		searcher.updateSearcher(IRI.create(oIRI.getIri()));

		//save ontology
		//saveOntology(ontoName, oIRI);
		
		return c;
	}

	private boolean isEntityOntology(String o) {
		for(OntologyIRI on : allLiveEntityOntologies) {
			if(on.getName().equalsIgnoreCase(o))
				return true;
		}
		return false;
	}

	private boolean isQualityOntology(String o) {
		for(OntologyIRI on : allLiveQualityOntologies) {
			if(on.getName().equalsIgnoreCase(o))
				return true;
		}
		return false;
	}

	/*private boolean isEntityOntology(String o) {
		for(OntologyIRI on : entityOntologies) {
			if(on.getName().equalsIgnoreCase(o))
				return true;
		}
		return false;
	}

	private boolean isQualityOntology(String o) {
		for(OntologyIRI on : qualityOntologies) {
			if(on.getName().equalsIgnoreCase(o))
				return true;
		}
		return false;
	}*/

	private OntologyIRI getOntologyIRI(String o) {
		for(OntologyIRI oIRI : allLiveEntityOntologies) {
			if(oIRI.getName().equalsIgnoreCase(o))
				return oIRI;
		}
		for(OntologyIRI oIRI : allLiveQualityOntologies) {
			if(oIRI.getName().equalsIgnoreCase(o))
				return oIRI;
		}
		return null;
	}

	/*private OntologyIRI getOntologyIRI(String o) {
		for(OntologyIRI oIRI : entityOntologies) {
			if(oIRI.getName().equalsIgnoreCase(o))
				return oIRI;
		}
		for(OntologyIRI oIRI : qualityOntologies) {
			if(oIRI.getName().equalsIgnoreCase(o))
				return oIRI;
		}
		return null;
	}*/
	@PreDestroy
	public void destroy() throws Exception {
		this.saveAll();
	}

	private void saveAll() throws Exception {
		for(String o : this.owlOntologyManagerMap.keySet()) {
			OWLOntologyManager manager = this.owlOntologyManagerMap.get(o);
			try(FileOutputStream fos = new FileOutputStream(ontologyDir + File.separator + o + ".owl")) {
				manager.saveOntology(manager.getOntology(IRI.create(this.getOntologyIRI(o).getIri())), fos);
			}
		}

	}

	@PostMapping(value = "/save", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public boolean save(@RequestBody SaveOntology saveOntology) throws Exception {
		//which ontology to use
		String usrid = "";
		String ontoName = saveOntology.getOntology();
		if(!saveOntology.getUser().isEmpty()){
			usrid = saveOntology.getUser();
			ontoName = ontoName+"_"+usrid;
		}
		OntologyIRI oIRI = getOntologyIRI(ontoName);


		OWLOntologyManager owlOntologyManager = this.owlOntologyManagerMap.get(ontoName); //this.owlOntologyManagerMap.get(oIRI);
		try(FileOutputStream fos = new FileOutputStream(oIRI.getOntologyFP())) {
			owlOntologyManager.saveOntology(
					owlOntologyManager.getOntology(
							IRI.create(oIRI.getIri())
							), 
					fos);
			
			
		}
		return true;
	}
	
	@PostMapping(value = "/deprecate", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public  ChangeApplied deprecate(@RequestBody Deprecate deprecate) throws Exception {
		//which ontology to use
		String usrid = "";
		String ontoName = deprecate.getOntology();
		if(!deprecate.getUser().isEmpty()){
			usrid = deprecate.getUser();
			ontoName = ontoName+"_"+usrid;
		}
		OntologyIRI oIRI = getOntologyIRI(ontoName);

		OWLOntologyManager owlOntologyManager = this.owlOntologyManagerMap.get(ontoName); //this.owlOntologyManagerMap.get(oIRI);
		OWLDataFactory owlDataFactory = owlOntologyManager.getOWLDataFactory();
		OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(oIRI.getIri()));
		OWLAnnotationAssertionAxiom dAxiom = owlDataFactory.getDeprecatedOWLAnnotationAssertionAxiom(IRI.create(deprecate.getClassIRI())); //deprecate the old class
		return owlOntologyManager.addAxiom(owlOntology, dAxiom);
		
	}
	
	
	public boolean saveOntology(String ontoName, OntologyIRI oIRI){
		boolean success = true;
		OWLOntologyManager owlOntologyManager = this.owlOntologyManagerMap.get(ontoName); //this.owlOntologyManagerMap.get(oIRI);
		try(FileOutputStream fos = new FileOutputStream(oIRI.getOntologyFP())) {
			owlOntologyManager.saveOntology(
					owlOntologyManager.getOntology(
							IRI.create(oIRI.getIri())
							), 
					fos);
		}catch(Exception e){
			success = false;
			LOGGER.debug("Failed to save "+ontoName +"("+oIRI+")");
		}
		return success;
		
	}

	/*@GetMapping(value = "/{ontology}/getDefinition", produces = { MediaType.APPLICATION_JSON_VALUE })
	public String getClassHierarchyInJSON(@PathVariable String ontology, @RequestParam Optional<String> user, 
			@RequestParam String baseIri, @RequestParam String term){
		String usrid = "";
		String ontoName = ontology;
		if(user.isPresent()){
			usrid = user.get();
			ontoName = ontology+"_"+usrid;
		}
		return this.termDefinitionMap.get(ontoName).get(baseIri+"#"+term);
	}*/
	
	
	/**
	 * Obtain the subclasses of the termIri as a JSON object
	 * 
	 */
	@GetMapping(value = "/{ontology}/getSubclasses", produces = { MediaType.APPLICATION_JSON_VALUE })
	public String getSubclassesInJSON(@PathVariable String ontology, @RequestParam Optional<String> user, 
			@RequestParam String baseIri, @RequestParam String term){
		 //@RequestParam String termIri){ //use %23 for #, allows both forms:/term #term 
		String usrid = "";
		String ontoName = ontology;
		if(user.isPresent()){
			usrid = user.get();
			ontoName = ontology+"_"+usrid;
		}
		if(term.contains(" ")) term = term.trim().replaceAll("\\s+", "_"); //carex ontology: use _ in multiple words phrases, such as life_cycle, in class IRI. 
		//termIri = termIri.replaceAll("%23", "#");
		OntologyIRI oIRI = getOntologyIRI(ontoName);
		

		//use the selected ontology		
		OWLOntologyManager owlOntologyManager = this.owlOntologyManagerMap.get(ontoName);//this.owlOntologyManagerMap.get(oIRI);
		OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(oIRI.getIri()));
		JFactFactory reasonerFactory = new JFactFactory();
		OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(owlOntology);
		reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		OWLDataFactory owlDataFactory = owlOntologyManager.getOWLDataFactory();
		OWLClass clazz = owlDataFactory.getOWLClass(IRI.create(baseIri+"#"+term)); //must use # in the ontology
		//OWLClass clazz = owlDataFactory.getOWLClass(IRI.create(termIri)); //must use # in the ontology
		
		JSONObject object = new JSONObject();
		
		OWLAnnotationProperty definition = owlDataFactory.getOWLAnnotationProperty(IRI.create(definitions));
		OWLAnnotationProperty elucidation = owlDataFactory.getOWLAnnotationProperty(IRI.create(elucidations));
		
		writeSimplifiedJSONObject(reasoner, owlDataFactory, clazz, object, owlOntologyManager, owlOntology, definition, elucidation);

		return object.toJSONString(); 
	}
	
	
	//Obtain the entire ontology as a JSON object
	@GetMapping(value = "/{ontology}/getTree", produces = { MediaType.APPLICATION_JSON_VALUE })
	public String getClassHierarchyInJSON(@PathVariable String ontology, @RequestParam Optional<String> user) throws Exception {
		String usrid = "";
		String ontoName = ontology;
		if(user.isPresent()){
			usrid = user.get();
			ontoName = ontology+"_"+usrid;
		}
		OWLOntologyManager owlOntologyManager = this.owlOntologyManagerMap.get(ontoName); //ontology: carex
		//Hashtable<String, String> termDefinitionCache = this.termDefinitionMap.get(ontoName);
		OntologyIRI oIRI = getOntologyIRI(ontoName);
		OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(oIRI.getIri()));
		OWLDataFactory owlDataFactory = owlOntologyManager.getOWLDataFactory();

		JSONObject object = new JSONObject();
		OWLClass root;
		OWLAnnotationProperty synonymE = owlDataFactory.getOWLAnnotationProperty(IRI.create(synonyme));
		OWLAnnotationProperty synonymB = owlDataFactory.getOWLAnnotationProperty(IRI.create(synonyme));
		OWLAnnotationProperty synonymNR =owlDataFactory.getOWLAnnotationProperty(IRI.create(synonyme));
		OWLAnnotationProperty definition = owlDataFactory.getOWLAnnotationProperty(IRI.create(definitions));
		OWLAnnotationProperty elucidation = owlDataFactory.getOWLAnnotationProperty(IRI.create(elucidations));

		root = owlDataFactory.getOWLClass(IRI.create("http://www.w3.org/2002/07/owl#Thing"));
		JFactFactory reasonerFactory = new JFactFactory();
		OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(owlOntology);
		writeJSONObject(reasoner, owlDataFactory, root, object, owlOntologyManager, owlOntology, synonymE, synonymB, synonymNR, definition, elucidation);

		return object.toJSONString(); 
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject writeSimplifiedJSONObject(OWLReasoner reasoner, OWLDataFactory owlDataFactory, OWLClass clazz, JSONObject object, OWLOntologyManager manager, OWLOntology onto, OWLAnnotationProperty definition, OWLAnnotationProperty elucidation) {
		if(reasoner.isSatisfiable(clazz)){
			//print this  class
			object.put("text", labelFor(clazz, onto, owlDataFactory));
			//System.out.println("text: "+ labelFor(clazz));

			JSONObject data = new JSONObject();
			//details holds: class IRI, synonyms
			JSONArray details = new JSONArray();
			JSONObject o = new JSONObject();

			o.put("IRI", clazz.getIRI().getIRIString());
			
			ArrayList<String> result = getAnnotationValues(clazz, definition, onto);
			for(String def: result){
				o.put("definition", def);
				//termDefinitionCache.put(clazz.getIRI().toString(), result);
				//System.out.println("shared synonyms: "+synonymB4(clazz));
			}

			result = getAnnotationValues(clazz, elucidation, onto);
			for(String elu: result){
				o.put("elucidation", elu);
				//System.out.println("shared synonyms: "+synonymB4(clazz));
			}

			details.add(o);
			data.put("details", details);
			object.put("data", data);


			//subclasses for the children field
			Set <OWLClass> subClzz = reasoner.getSubClasses(clazz, true).entities().collect(Collectors.toSet());
			if(!isEmpty(subClzz, manager)){
				JSONArray children = new JSONArray();
				Iterator<OWLClass> it = subClzz.iterator();
				while(it.hasNext()){
					OWLClass c = it.next();
					if(!c.equals(clazz))
						children.add(/*i++,*/ writeSimplifiedJSONObject(reasoner, owlDataFactory, c, new JSONObject(), manager, onto, definition, elucidation));
				}
				if(children.size()>0)
					object.put("children", children);
			}
		}
		return object;
	}

	@SuppressWarnings("unchecked")
	private JSONObject writeJSONObject(OWLReasoner reasoner, OWLDataFactory owlDataFactory, OWLClass clazz, JSONObject object, OWLOntologyManager manager, OWLOntology onto, OWLAnnotationProperty synonymE, OWLAnnotationProperty synonymB, OWLAnnotationProperty synonymNR, OWLAnnotationProperty definition, OWLAnnotationProperty elucidation) {

		if(reasoner.isSatisfiable(clazz)){
			//print one  class
			object.put("text", labelFor(clazz, onto, owlDataFactory));

			JSONObject data = new JSONObject();
			//details holds: class IRI, synonyms
			JSONArray details = new JSONArray();
			JSONObject o = new JSONObject();

			o.put("IRI", clazz.getIRI().getIRIString());
			ArrayList<String> result = synonymE4(clazz, synonymE, onto);
			for(String esyn: result){
				o.put("exact synonyms", esyn);
				//System.out.println("exact synonyms: "+result));
			}

			result = synonymB4(clazz, synonymB, onto);
			for(String bsyn: result){
				o.put("shared broad synonyms",bsyn);
				//System.out.println("shared synonyms: "+result);
			}

			result = synonymNotR4(clazz, synonymNR, onto);
			for(String nsyn: result){
				o.put("not recommended synonyms", nsyn);
				//System.out.println("shared synonyms: "+result);
			}

			result = getAnnotationValues(clazz, definition, onto);
			for(String def: result){
				o.put("definition", def);
				//termDefinitionCache.put(clazz.getIRI().toString(), result);
				//System.out.println("shared synonyms: "+synonymB4(clazz));
			}

			result = getAnnotationValues(clazz, elucidation, onto);
			for(String eluc: result){
				o.put("elucidation", eluc);
				//System.out.println("shared synonyms: "+synonymB4(clazz));
			}

			details.add(o);
			data.put("details", details);
			object.put("data", data);


			//subclasses for the children field
			Set <OWLClass> subClzz = reasoner.getSubClasses(clazz, true).entities().collect(Collectors.toSet());
			if(!isEmpty(subClzz, manager)){
				JSONArray children = new JSONArray();
				Iterator<OWLClass> it = subClzz.iterator();
				//int i = 1;
				while(it.hasNext()){
					OWLClass c = it.next();
					if(!c.equals(clazz))
						children.add(/*i++,*/ writeJSONObject(reasoner, owlDataFactory, c, new JSONObject(), manager, onto, synonymE, synonymB, synonymNR, definition, elucidation));
				}
				object.put("children", children);
			}
		}

		return object;
	}
	
	private boolean isEmpty(Set<OWLClass> subClzz, OWLOntologyManager manager) {
		return subClzz.iterator().next().equals(manager.getOWLDataFactory().getOWLNothing());
	}


	private ArrayList<String> getAnnotationValues(OWLClass clazz, OWLAnnotationProperty annotationProperty, OWLOntology onto) {
		ArrayList<String> values = new ArrayList<String>();
		
		for(OWLAnnotation a : EntitySearcher.getAnnotations(clazz, onto, annotationProperty).collect(Collectors.toSet())) {
			OWLAnnotationValue value = a.getValue();
			if(value instanceof OWLLiteral) {
				values.add(((OWLLiteral) value).getLiteral());   
			}
		}

		return values;
	}

	
	
	//leaf blade = blade and part_of some leaf
	/*private String logicDef(OWLClass clazz, String definition, OWLDataFactory owlDataFactory, OWLOntologyManager manager, OWLOntology onto){
		OWLClassExpression clsB = null;
		try{
			ManchesterSyntaxTool parser = new ManchesterSyntaxTool(carex, null);
			clsB = parser.parseManchesterExpression(definition);
		}catch(Exception e){
			String msg = e.getMessage();
			System.out.println(msg);
			return msg;
		}
		if(clsB == null) return "equivalent class expression syntax error";
		OWLAxiom def = owlDataFactory.getOWLEquivalentClassesAxiom(clazz, clsB);
		ChangeApplied change = manager.addAxiom(onto, def);
		if(change != ChangeApplied.SUCCESSFULLY)
			return change.name();
		File file = new File("C:/Users/hongcui/Documents/research/AuthorOntology/Data/CarexOntology/carex_tiny.owl");            
		OWLDocumentFormat format = manager.getOntologyFormat(onto);    
		try{
			manager.saveOntology(onto, format, IRI.create(file.toURI()));
		}catch(Exception e){
			System.out.println(e.getStackTrace());
		}
		return definition + " added!";
	}*/



	private ArrayList<String> synonymE4(OWLClass clazz, OWLAnnotationProperty synonymE, OWLOntology onto) {
		ArrayList<String> eSyns = new ArrayList<String>();
		for(OWLAnnotation a : EntitySearcher.getAnnotations(clazz, onto, synonymE).collect(Collectors.toSet())) {
			OWLAnnotationValue value = a.getValue();
			if(value instanceof OWLLiteral) {
				eSyns.add(((OWLLiteral) value).getLiteral());   
			}
		}

		return eSyns;
	}

	private ArrayList<String> synonymB4(OWLClass clazz, OWLAnnotationProperty synonymB, OWLOntology onto) {
		ArrayList<String> bSyns = new ArrayList<String>();
		for(OWLAnnotation a : EntitySearcher.getAnnotations(clazz, onto, synonymB).collect(Collectors.toSet())) {
			OWLAnnotationValue value = a.getValue();
			if(value instanceof OWLLiteral) {
				bSyns.add(((OWLLiteral) value).getLiteral());   
			}
		}

		return bSyns;
	}

	private ArrayList<String> synonymNotR4(OWLClass clazz, OWLAnnotationProperty synonymNR, OWLOntology onto) {
		
		ArrayList<String> nSyns = new ArrayList<String>();
		for(OWLAnnotation a : EntitySearcher.getAnnotations(clazz, onto, synonymNR).collect(Collectors.toSet())) {
			OWLAnnotationValue value = a.getValue();
			if(value instanceof OWLLiteral) {
				nSyns.add(((OWLLiteral) value).getLiteral());   
			}
		}

		return nSyns;
	}

	private String labelFor(OWLClass clazz, OWLOntology onto, OWLDataFactory owlDataFactory) {
		for(OWLAnnotation a : EntitySearcher.getAnnotations(clazz, onto, owlDataFactory.getRDFSLabel()).collect(Collectors.toSet())) {
			OWLAnnotationValue value = a.getValue();
			if(value instanceof OWLLiteral) {
				return ((OWLLiteral) value).getLiteral();   
			}
		}
		//else return the last segment of the IRI
		String iri = clazz.getIRI().getIRIString();
		int i = iri.lastIndexOf("#") >0? iri.lastIndexOf("#") : iri.lastIndexOf("/");
		return iri.substring(i+1);
	}
	
	public FileSearcher getSearcher(String ontoName){
		return this.searchersMap.get(ontoName);
	}

	/**
	 * get conflict type 1: classes with multiple superclasses, and with at least one sentence. 
	 * Without any example sentence, the user can solve the conflict.
	 * 
	 */
	@GetMapping(value = "/{ontology}/getClassesWMSuperclasses", produces = { MediaType.APPLICATION_JSON_VALUE })
	public String getClassesWMSupersInJSON(@PathVariable String ontology, @RequestParam Optional<String> user){

		String usrid = "";
		String ontoName = ontology;
		if(user.isPresent()){
			usrid = user.get();
			ontoName = ontology+"_"+usrid;
		}
		OntologyIRI oIRI = getOntologyIRI(ontoName);
		//use the selected ontology		
		OWLOntologyManager owlOntologyManager = this.owlOntologyManagerMap.get(ontoName);//this.owlOntologyManagerMap.get(oIRI);
		OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(oIRI.getIri()));
		JFactFactory reasonerFactory = new JFactFactory();
		OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(owlOntology);
		reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		OWLDataFactory owlDataFactory = owlOntologyManager.getOWLDataFactory();
				
		JSONObject object = new JSONObject();
		writeJSON4ClassesWMSupers(reasoner, owlDataFactory, object, owlOntologyManager, owlOntology);

		return object.toJSONString(); 
	}

	
	@SuppressWarnings("unchecked")
	private void writeJSON4ClassesWMSupers(OWLReasoner reasoner, OWLDataFactory owlDataFactory, JSONObject object,
			OWLOntologyManager owlOntologyManager, OWLOntology owlOntology) {
		
		OWLAnnotationProperty definition = owlDataFactory.getOWLAnnotationProperty(IRI.create(definitions));
		OWLAnnotationProperty elucidation = owlDataFactory.getOWLAnnotationProperty(IRI.create(elucidations));
		OWLAnnotationProperty creator = owlDataFactory.getOWLAnnotationProperty(IRI.create(createdBy));
		Set<OWLClass> set = owlOntology.classesInSignature().collect(Collectors.toSet());
		JSONObject terms = new JSONObject();
		int t = 1;
		for(OWLClass clz: set){
			if(!isQuality(clz, owlOntology, reasoner)) continue;
			//OWLClass test = owlDataFactory.getOWLClass(IRI.create("http://biosemantics.arizona.edu/ontologies/carex#black_brown"));
			//if(!clz.equals(test)) continue;
			Set<OWLClass> suprs = reasoner.getSuperClasses(clz, true).entities().collect(Collectors.toSet()); //direct superclass
			if(suprs.size()>1){
				List<String> sentences = getExampleSentences(owlOntology, owlDataFactory, clz);
				if (sentences.size() >= 1) {
					// write JSON for this class
					// https://www.screencast.com/t/CgGKIEp549eL
					// JSONArray term = new JSONArray();
					JSONObject term = new JSONObject();
					term.put("iri", clz.getIRI().toString());
					term.put("label", labelFor(clz, owlOntology, owlDataFactory));
					
					//createdBy for term
					List<String> acreator = getAnnotationValues(clz, creator, owlOntology);
					if(acreator.size()>0)
						term.put("termCreator", acreator.get(0));
					else
						term.put("termCreator", "carex team");
					
					JSONArray sents = new JSONArray();
					for (String sent : sentences) {
						sents.add(sent);
					}
					term.put("sentences", sents);

					JSONObject categories = new JSONObject();
					int c = 1;
					for (OWLClass supr : suprs) {
						JSONObject category = new JSONObject();
						category.put("iri", supr.getIRI().toString());
						category.put("name", labelFor(supr, owlOntology, owlDataFactory));
						category.put("definition", getAnnotationValues(supr, definition, owlOntology));
						category.put("elucidation", getAnnotationValues(supr, elucidation, owlOntology));
						categories.put("category " + c, category);
						c++;
						term.put("categories", categories);
					}

					terms.put("term " + t, term);
					t++;
				}
			}
		}
		object.put("terms", terms);
		
	}

	private boolean isQuality(OWLClass clz, OWLOntology owlOntology, OWLReasoner reasoner) {
		Set<OWLClass> suprs =  reasoner.getSuperClasses(clz).entities().collect(Collectors.toSet());;
		for(OWLClass supr: suprs){
				if(supr.getIRI().equals(IRI.create("http://biosemantics.arizona.edu/ontologies/carex#quality"))){
					return true;
				}
		}
		return false;
	}

	private List<String> getExampleSentences(OWLOntology owlOntology, OWLDataFactory owlDataFactory, OWLClass clz) {
		// TODO Auto-generated method stub
		ArrayList<String> sents = new ArrayList<String>();
		OWLAnnotationProperty exampleSentencesAnnotationProperty = owlDataFactory.getOWLAnnotationProperty(IRI.create("http://purl.obolibrary.org/obo/IAO_0000112")); //OAI:example of usage
		Set<OWLAnnotation> sentAnnotations = EntitySearcher.getAnnotationObjects(clz, owlOntology, exampleSentencesAnnotationProperty).collect(Collectors.toSet());
		for (OWLAnnotation annot: sentAnnotations){
			OWLAnnotationValue value = annot.getValue();
			if(value instanceof OWLLiteral) {
				sents.add(((OWLLiteral) value).getLiteral()); 
		}
		}

		return sents;
	}
	
	
	/**
	 * get conflict type 2: classes with zero or multiple definitions
	 * 
	 */
	@GetMapping(value = "/{ontology}/getClassesWMZdefinitions", produces = { MediaType.APPLICATION_JSON_VALUE })
	public String getClassesWMZdefinitionsInJSON(@PathVariable String ontology, @RequestParam Optional<String> user){

		String usrid = "";
		String ontoName = ontology;
		if(user.isPresent()){
			usrid = user.get();
			ontoName = ontology+"_"+usrid;
		}
		OntologyIRI oIRI = getOntologyIRI(ontoName);
		//use the selected ontology		
		OWLOntologyManager owlOntologyManager = this.owlOntologyManagerMap.get(ontoName);//this.owlOntologyManagerMap.get(oIRI);
		OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(oIRI.getIri()));
		//JFactFactory reasonerFactory = new JFactFactory();
		//OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(owlOntology);
		//reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		OWLDataFactory owlDataFactory = owlOntologyManager.getOWLDataFactory();
				
		JSONObject object = new JSONObject();
		writeJSON4ClassesWMZdefintions(owlOntology, owlDataFactory, object, owlOntologyManager);

		return object.toJSONString(); 
	}
	

	@SuppressWarnings("unchecked")
	private void writeJSON4ClassesWMZdefintions(OWLOntology owlOntology, OWLDataFactory owlDataFactory,
			JSONObject object, OWLOntologyManager owlOntologyManager) {
		
		OWLAnnotationProperty definition = owlDataFactory.getOWLAnnotationProperty(IRI.create(definitions));
		OWLAnnotationProperty elucidation = owlDataFactory.getOWLAnnotationProperty(IRI.create(elucidations));
		OWLAnnotationProperty creator = owlDataFactory.getOWLAnnotationProperty(IRI.create(createdBy));
		Set<OWLClass> set = owlOntology.classesInSignature().collect(Collectors.toSet());
		JSONObject terms = new JSONObject();
		int t = 1;
		for(OWLClass clz: set){
			ArrayList<String> defs = getAnnotationValues(clz, definition, owlOntology);
			if(defs.isEmpty() || defs.size()>1){
				//write JSON for this class
				//https://www.screencast.com/t/yMyt9VzmS
				
				JSONObject term = new JSONObject();
				term.put("iri", clz.getIRI().toString());
				term.put("label", labelFor(clz, owlOntology, owlDataFactory));
				//createdBy for term
				List<String> acreator = getAnnotationValues(clz, creator, owlOntology);
				if(acreator.size()>0)
					term.put("termCreator", acreator.get(0));
				else
					term.put("termCreator", "carex team");
				
				Set<OWLClassExpression> suprs = EntitySearcher.getSuperClasses(clz, owlOntology).collect(Collectors.toSet());
				
				JSONArray slabels = new JSONArray();
				for(OWLClassExpression supr: suprs){
					if(supr instanceof OWLClass){
						slabels.add(labelFor((OWLClass)supr, owlOntology, owlDataFactory));
					}
				}
				term.put("superclass label", slabels);
				
				JSONArray sents = new JSONArray();
				List<String> sentences = getExampleSentences(owlOntology, owlDataFactory, clz);
				for(String sent: sentences){
					sents.add(sent);
				}
				term.put("sentences", sents);
				
				JSONArray definitions = new JSONArray();
				for(String def: defs){
					definitions.add(def);
				}
				term.put("definitions", definitions);
				
				JSONArray elucidations = new JSONArray();
				List<String> elus = getAnnotationValues(clz, elucidation, owlOntology);
				for(String elu: elus){
					elucidations.add(elu);
				}
				term.put("elucidations", elucidations);
				
				terms.put("term "+t, term);
				t++;
			}
		}
		
		object.put("terms", terms);
		
	}
	
		
		/**
		 * Get conflict type 3: toreview classes with exactly 1 definition. 
		 * All conflict types should be disjoint.
		 * Type 3 and type 2 are disjoint too. So if a term has two definitions, it needs to be resolved as a type 2 conflict first.
		 * 
		 * 
		 */
		@GetMapping(value = "/{ontology}/getToreviewClasses", produces = { MediaType.APPLICATION_JSON_VALUE })
		public String getToreviewClassesInJSON(@PathVariable String ontology, @RequestParam Optional<String> user){

			String usrid = "";
			String ontoName = ontology;
			if(user.isPresent()){
				usrid = user.get();
				ontoName = ontology+"_"+usrid;
			}
			OntologyIRI oIRI = getOntologyIRI(ontoName);
			//use the selected ontology		
			OWLOntologyManager owlOntologyManager = this.owlOntologyManagerMap.get(ontoName);//this.owlOntologyManagerMap.get(oIRI);
			OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(oIRI.getIri()));
			//JFactFactory reasonerFactory = new JFactFactory();
			//OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(owlOntology);
			//reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
			OWLDataFactory owlDataFactory = owlOntologyManager.getOWLDataFactory();
					
			JSONObject object = new JSONObject();
		
			writeJSON4ToreviewClasses(owlOntology, owlDataFactory, object, owlOntologyManager);

			return object.toJSONString(); 
		}

		@SuppressWarnings("unchecked")
		private void writeJSON4ToreviewClasses(OWLOntology owlOntology, OWLDataFactory owlDataFactory,
				JSONObject object, OWLOntologyManager owlOntologyManager) {
			
			OWLClass toreview = owlDataFactory.getOWLClass(IRI.create("http://biosemantics.arizona.edu/ontologies/carex#toreview"));
			OWLAnnotationProperty definition = owlDataFactory.getOWLAnnotationProperty(IRI.create(definitions));
			OWLAnnotationProperty elucidation = owlDataFactory.getOWLAnnotationProperty(IRI.create(elucidations));
			OWLAnnotationProperty creator = owlDataFactory.getOWLAnnotationProperty(IRI.create(createdBy));
			//do not need reasoner
			Set<OWLClassExpression> set = EntitySearcher.getSubClasses(toreview, owlOntology).collect(Collectors.toSet());
			JSONObject terms = new JSONObject();
			int t = 1;
			for(OWLClassExpression clzx: set){
				if(clzx instanceof OWLClass){
					//write JSON for this class
					//https://www.screencast.com/t/yMyt9VzmS
					OWLClass clz = (OWLClass)clzx;
					List<String> defs = getAnnotationValues(clz, definition, owlOntology);
					if(defs.size()==1){ //exactly one definition
						String adefinition = defs.get(0);
						JSONObject term = new JSONObject();
						term.put("iri", clz.getIRI().toString());
						term.put("label", labelFor(clz, owlOntology, owlDataFactory));
						
						//createdBy for term
						List<String> acreator = getAnnotationValues(clz, creator, owlOntology);
						if(acreator.size()>0)
							term.put("termCreator", acreator.get(0));
						else
							term.put("termCreator", "carex team");
						
					
						JSONArray sents = new JSONArray();
						List<String> sentences = getExampleSentences(owlOntology, owlDataFactory, clz);
						for(String sent: sentences){
							sents.add(sent);
						}
						term.put("sentences", sents);
						term.put("definition", adefinition);
						
						
	
						JSONArray elucidations = new JSONArray();
						List<String> elus = getAnnotationValues(clz, elucidation, owlOntology);
						for(String elu: elus){
							elucidations.add(elu);
						}
						term.put("elucidations", elucidations);
						
						terms.put("term "+t, term);
						t++;
					}
				}
				
			}
			
			object.put("terms", terms);
			
		}
			
		
	

	/*
	 * 
	 * 
	 * shared Test:
	 * POST /createUserOntology
	 * POST /class term=apex hair ontology=exp user=1 superclassIRI="http://biosemantics.arizona.edu/ontology/exp#physical_entity" 
	               defintion=def elucidation=http://iri.org/image.jpg  createdBy=hongcui  examples=example1, example2
	               creationDate=2009/11/11 definitionSrc=me logicDefinition=partOf some physical entity
	 * 
	 * individual Test:
	 * POST /createUserOntology user=1 ontology=exp
	 * POST /bsynonym term=bsynonym classIRI="http://biosemantics.arizona.edu/ontology/exp#apex" user=1 ontology=exp
	 * POST /esynonym term=esynonym classIRI="http://biosemantics.arizona.edu/ontology/exp#apex" user=1 ontology=exp
	   POST /class term=apex hair ontology=exp user=1 superclassIRI="http://biosemantics.arizona.edu/ontology/exp#physical_entity" 
	               defintion=def elucidation=http://iri.org/image.jpg  createdBy=hongcui  examples=example1, example2
	               creationDate=2009/11/11 definitionSrc=me logicDefinition=partOf some physical entity
	   POST /hasPart user=1 ontology=exp bearerIRI=""http://biosemantics.arizona.edu/ontology/exp#apex" partIRI="http://biosemantics.arizona.edu/ontology/exp#apex_hair"
	   POST /partOf user=1 ontology=exp bearerIRI=""http://biosemantics.arizona.edu/ontology/exp#apex" partIRI="http://biosemantics.arizona.edu/ontology/exp#apex_hair"
	 * POST /save
	 * GET /search ontology=exp term=apex user=1
	 */
}
