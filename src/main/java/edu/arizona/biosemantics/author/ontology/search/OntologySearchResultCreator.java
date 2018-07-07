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
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
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

	private static final Logger LOGGER = LoggerFactory.getLogger(OntologySearchResultCreator.class);
	private static Ontology[] entityOntologies = { Ontology.PO, Ontology.CAREX };
	
	public OntologySearchResult create(Ontology o, List<OntologyEntry> entries, 
			OntologyAccess ontologyAccess, OWLOntology owlOntology, OWLOntologyManager owlOntologyManager) {
		OntologySearchResult result = new OntologySearchResult();
		for(OntologyEntry e : entries) {
			String iri = e.getClassIRI();
			OWLClass owlClass = owlOntologyManager.getOWLDataFactory().getOWLClass(IRI.create(iri));
			
			Set<OWLAnnotation> annotations = EntitySearcher.getAnnotations(owlClass, owlOntology).collect(Collectors.toSet());
			
			/*
			Set<OWLIndividual> indivduals = EntitySearcher.getIndividuals(owlClass, owlOntology).collect(Collectors.toSet());
			for(OWLIndividual i : indivduals) {
				Multimap<OWLObjectPropertyExpression, OWLIndividual> properties = 
					EntitySearcher.getObjectPropertyValues(i, owlOntology);
				System.out.println(properties);
			}
			*/
			
			List<Annotation> resultAnnotations = new ArrayList<Annotation>();
			for(OWLAnnotation a : annotations) {
				Optional<OWLLiteral> literal = a.getValue().annotationValue().asLiteral();
				if(literal.isPresent()) {
					resultAnnotations.add(new Annotation(a.getProperty().getIRI().getIRIString(), literal.get().getLiteral()));
				} else {
					resultAnnotations.add(new Annotation(a.getProperty().getIRI().getIRIString(), ""));
				}
			}
			
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
	
}
