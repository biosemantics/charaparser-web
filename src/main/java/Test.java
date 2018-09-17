import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import edu.arizona.biosemantics.author.ontology.search.FileSearcher;
import edu.arizona.biosemantics.author.ontology.search.model.OntologyIRI;
import edu.arizona.biosemantics.author.ontology.search.model.Synonym;
import edu.arizona.biosemantics.common.ontology.search.OntologyAccess;
import edu.arizona.biosemantics.common.ontology.search.model.Ontology;
import edu.arizona.biosemantics.semanticmarkup.enhance.know.AnnotationProperty;

public class Test {

	private static String ontoDir = "C:/Users/hongcui/Documents/research/AuthorOntology/Experiment/ontologies";
	private static OntologyIRI CAREX = new OntologyIRI(ontoDir+"/"+"CAREX.owl", 
			"http://biosemantics.arizona.edu/ontologies/carex",
			"CAREX");
	private static String wordNetDir = "C:/Users/hongcui/Documents/research/AuthorOntology/Experiment/wordnet/wn31/dict";
	static Hashtable<String, String> ontologyIRIs = new Hashtable<String, String>();
	static{
		ontologyIRIs.put("CAREX", "http://biosemantics.arizona.edu/ontologies/carex");
		ontologyIRIs.put("EXP", "http://biosemantics.arizona.edu/ontologies/exp");
		ontologyIRIs.put("PO", "http://purl.obolibrary.org/obo/po");
		ontologyIRIs.put("PATO", "http://purl.obolibrary.org/obo/pato");
	}
	
	public static void main(String[] args) throws Exception{
		/** Setting up ontology access (search, modify) facilitators; same as in controller initialization **/
		//individual
		String userId = "1";
		String[] userOntologies ={"EXP"};
		//copy base ontologies to user ontologies
		HashSet<String> entityOntologyNames = new HashSet<String>();
		OntologyIRI o;
		int i = 0;
		String onto = userOntologies[0];
			File ontoS = new File(ontoDir, onto.toLowerCase()+".owl");
			File ontoD = new File(ontoDir, onto.toLowerCase()+"_"+userId+".owl"); //ontology indexed as EXP_1.owl, EXP_2.owl, 1 and 2 are user ids.
			if(!ontoD.exists())
				Files.copy(ontoS.toPath(), ontoD.toPath(), StandardCopyOption.REPLACE_EXISTING);
			
			o = new OntologyIRI(ontoD.getAbsolutePath(), 
					ontologyIRIs.get(onto.toUpperCase()), onto+"_"+userId.toUpperCase()); //for experiments
			entityOntologyNames.add(onto+"_"+userId.toUpperCase()); //EXP_1
	
		
		
		//shared
		/*
		OntologyIRI o = CAREX;
		HashSet<String> entityOntologyNames = new HashSet<String>();
		entityOntologyNames.add(o.getName());
		*/
		
		
		
		
		FileSearcher searcher = new FileSearcher(entityOntologyNames, new HashSet<String>(), 
				ontoDir, wordNetDir);
		OWLOntologyManager owlOntologyManager = searcher.getOwlOntologyManager();
		OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(o.getIri()));
		Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
		ontologies.add(owlOntology);
		OntologyAccess ontologyAccess  = new OntologyAccess(ontologies);
		
		/** Try sample addition of synonym; same as in controller upon incoming request **/
		OWLDataFactory owlDataFactory = owlOntologyManager.getOWLDataFactory();
		Synonym synonym = new Synonym("", "test", "", "http://biosemantics.arizona.edu/ontologies/exp#apex");
		String synonymTerm = synonym.getTerm();
		OWLClass clazz = owlDataFactory.getOWLClass(synonym.getClassIRI());
		OWLAnnotationProperty exactSynonymProperty = 
				owlDataFactory.getOWLAnnotationProperty(IRI.create(AnnotationProperty.EXACT_SYNONYM.getIRI()));
		OWLAnnotation synonymAnnotation = owlDataFactory.getOWLAnnotation(
						exactSynonymProperty, owlDataFactory.getOWLLiteral(synonymTerm, "en"));
		OWLAxiom synonymAxiom = owlDataFactory.getOWLAnnotationAssertionAxiom(clazz.getIRI(), synonymAnnotation);
		try {
			owlOntologyManager.addAxiom(owlOntology, synonymAxiom);
			try(FileOutputStream fos = new FileOutputStream(ontoDir + File.separator + o.getName().toLowerCase() + ".owl")) {
				owlOntologyManager.saveOntology(owlOntologyManager.getOntology(IRI.create(o.getIri())), fos);
			}
		} catch(Throwable t) {
			t.printStackTrace();
		}
		
	}
	
}
