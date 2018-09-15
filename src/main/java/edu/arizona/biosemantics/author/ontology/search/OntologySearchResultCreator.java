package edu.arizona.biosemantics.author.ontology.search;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Multimap;

import edu.arizona.biosemantics.author.ontology.search.model.Annotation;
import edu.arizona.biosemantics.author.ontology.search.model.OntologySearchResult;
import edu.arizona.biosemantics.author.ontology.search.model.OntologySearchResultEntry;
import edu.arizona.biosemantics.common.ontology.search.OntologyAccess;
import edu.arizona.biosemantics.common.ontology.search.model.Ontology;
import edu.arizona.biosemantics.common.ontology.search.model.OntologyEntry;

@Component
public class OntologySearchResultCreator {

	//private static final Logger LOGGER = LoggerFactory.getLogger(OntologySearchResultCreator.class);
	//private static Ontology[] entityOntologies = { Ontology.PO, Ontology.CAREX };
	private static String ELUCIDATION = "http://purl.oblibrary.org/obo/IAO_0000600";
	
	public OntologySearchResult create(String o, List<OntologyEntry> entries, 
			OntologyAccess ontologyAccess, OWLOntology owlOntology, OWLOntologyManager owlOntologyManager) {
		OntologySearchResult result = new OntologySearchResult();
		for(OntologyEntry e : entries) {
			String iri = e.getClassIRI();
			OWLClass owlClass = owlOntologyManager.getOWLDataFactory().getOWLClass(IRI.create(iri));
			List<Annotation> resultAnnotations = new ArrayList<Annotation>();
			
			Set<OWLAnnotation> annotations = EntitySearcher.getAnnotations(owlClass, owlOntology).collect(Collectors.toSet());
			for(OWLAnnotation a : annotations) {
				Optional<OWLLiteral> literal = a.getValue().annotationValue().asLiteral();
				if(literal.isPresent()) {
					resultAnnotations.add(new Annotation(a.getProperty().getIRI().getIRIString(), literal.get().getLiteral()));
				} /*else {
					resultAnnotations.add(new Annotation(a.getProperty().getIRI().getIRIString(), ""));
				}*/
			}
			
			String elucidation = this.getElucidation(owlClass, owlOntology);
			if(elucidation != null)
				resultAnnotations.add(new Annotation("elucidation", elucidation));
			Set<OWLClass> bearers = ontologyAccess.getBearers(owlClass);
			Set<OWLClass> parts = ontologyAccess.getParts(owlClass);
			for(OWLClass bearer : bearers)
				resultAnnotations.add(new Annotation("has part", bearer.getIRI().getIRIString()));
			for(OWLClass part : parts)
				resultAnnotations.add(new Annotation("part of", part.getIRI().getIRIString()));
			
			result.getEntries().add(new OntologySearchResultEntry(e.getLabel(), e.getScore(), e.getParentLabel(), resultAnnotations));
		}
		return result;
	}
	
	public String getElucidation(OWLClass owlClass, OWLOntology owlOntology) {
		String elucidation = null;
		Optional<OWLAnnotationAssertionAxiom> firstElucidation = EntitySearcher.getAnnotationAssertionAxioms(owlClass, owlOntology).filter(a -> {
			return a.getProperty().getIRI().equals(IRI.create(ELUCIDATION));
		}).findFirst();
		
		if(firstElucidation.isPresent()) {
			Optional<OWLLiteral> literal = firstElucidation.get().literalValue();
			if(literal.isPresent())
				elucidation = literal.get().getLiteral();
		}
		return elucidation;
	}
	
}
