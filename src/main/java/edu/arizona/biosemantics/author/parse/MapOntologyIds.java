package edu.arizona.biosemantics.author.parse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.common.ontology.search.FileSearcher;
import edu.arizona.biosemantics.common.ontology.search.Searcher;
import edu.arizona.biosemantics.common.ontology.search.model.Ontology;
import edu.arizona.biosemantics.common.ontology.search.model.OntologyEntry;
import edu.arizona.biosemantics.semanticmarkup.enhance.transform.AbstractTransformer;

@Component
public class MapOntologyIds extends AbstractTransformer {

	private static Ontology[] ontologies = { Ontology.PO, Ontology.PATO, Ontology.CAREX };
	private HashMap<Ontology, Searcher> searchersMap;
	
	@Autowired
	public MapOntologyIds(@Value("${ontologySearch.ontologyDir}") String ontologyDir,
			@Value("${ontologySearch.wordNetDir}") String wordNetDir) throws OWLOntologyCreationException {
		this.searchersMap = new HashMap<Ontology, Searcher>();
		for(Ontology o : ontologies) 
			this.searchersMap.put(o, new FileSearcher(o, ontologyDir, wordNetDir));
	}
	
	@Override
	public void transform(Document document) {
		mapEntities(document);
		mapCharacters(document);
	}

	private void mapCharacters(Document document) {
		for (Element character : this.characterPath.evaluate(document)) {
			String value = character.getAttributeValue("value");
			String charType = character.getAttributeValue("char_type");
			if(value != null)
				value = value.trim();
			if(charType != null)
				charType = charType.trim();
			
			if(charType == null || !charType.equals("range_value")) {
				//if(value != null) {
					String searchTerm = value;
					List<OntologyEntry> entries = getCharacterEntries(searchTerm);

					String ontologyId = "";
					for(OntologyEntry entry : entries) {
						if(!ontologyId.isEmpty())
							ontologyId += ";";
						ontologyId += entry.getClassIRI() + "[" + searchTerm + ":" + entry.getParentLabel() + 
								"/" + entry.getLabel() + ":" + entry.getScore() + "]";
					}
					if(!entries.isEmpty())
						character.setAttribute("ontologyid", ontologyId);
				//}
			}
		}
	}

	private void mapEntities(Document document) {
		for (Element biologicalEntity : this.biologicalEntityPath.evaluate(document)) {
			
			String name = biologicalEntity.getAttributeValue("name");
			if(name != null)
				name = name.trim();
			String constraint = biologicalEntity.getAttributeValue("constraint");
			if(constraint != null)
				constraint = constraint.trim();
			
			String searchTerm = name;
			if(constraint != null)
				searchTerm = constraint + " " + name;
			
			if(searchTerm != null) {
				List<OntologyEntry> entries = getEntityEntries(searchTerm);
				String ontologyId = "";
				for(OntologyEntry entry : entries) {
					if(!ontologyId.isEmpty());
						ontologyId += ";";
					ontologyId += entry.getClassIRI() + "[" + searchTerm + ":" + entry.getParentLabel() + 
							"/" + entry.getLabel() + ":" + entry.getScore() + "]";
				}
				if(!entries.isEmpty())
					biologicalEntity.setAttribute("ontologyid", ontologyId);
			}
		}
	}

	private List<OntologyEntry> getEntityEntries(String searchTerm) {
		Searcher searcher = this.searchersMap.get(Ontology.PO);
		List<OntologyEntry> ontologyEntries = searcher.getEntityEntries(searchTerm, "", "");
		
		searcher = this.searchersMap.get(Ontology.CAREX);
		ontologyEntries.addAll(searcher.getEntityEntries(searchTerm, "", ""));
		
		return ontologyEntries;
	}
	
	private List<OntologyEntry> getCharacterEntries(String searchTerm) {
		Searcher searcher = this.searchersMap.get(Ontology.PATO);
		return searcher.getEntityEntries(searchTerm, "", "");
	}
}
