import java.util.HashSet;
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

	private static OntologyIRI CAREX = new OntologyIRI(Ontology.CAREX, 
			"http://biosemantics.arizona.edu/ontologies/carex");
	private static String ontologyDir = "ontologies";
	private static String wordNetDir = "wordnet/wn31/dict";
	
	public static void main(String[] args) {
		/** Setting up ontology access (search, modify) facilitators; same as in controller initialization **/
		OntologyIRI o = CAREX;
		HashSet<String> entityOntologyNames = new HashSet<String>();
		entityOntologyNames.add(o.getOntology().name());
		FileSearcher searcher = new FileSearcher(entityOntologyNames, new HashSet<String>(), 
				ontologyDir, wordNetDir);
		OWLOntologyManager owlOntologyManager = searcher.getOwlOntologyManager();
		OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(o.getIri()));
		Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
		ontologies.add(owlOntology);
		OntologyAccess ontologyAccess  = new OntologyAccess(ontologies);
		
		/** Try sample addition of synonym; same as in controller upon incoming request **/
		OWLDataFactory owlDataFactory = owlOntologyManager.getOWLDataFactory();
		Synonym synonym = new Synonym("test", "http://purl.obolibrary.org/obo/UBERON_0001062");
		String synonymTerm = synonym.getTerm();
		OWLClass clazz = owlDataFactory.getOWLClass(synonym.getClassIRI());
		OWLAnnotationProperty exactSynonymProperty = 
				owlDataFactory.getOWLAnnotationProperty(IRI.create(AnnotationProperty.EXACT_SYNONYM.getIRI()));
		OWLAnnotation synonymAnnotation = owlDataFactory.getOWLAnnotation(
						exactSynonymProperty, owlDataFactory.getOWLLiteral(synonymTerm, "en"));
		OWLAxiom synonymAxiom = owlDataFactory.getOWLAnnotationAssertionAxiom(clazz.getIRI(), synonymAnnotation);
		try {
			owlOntologyManager.addAxiom(owlOntology, synonymAxiom);
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}
	
}
