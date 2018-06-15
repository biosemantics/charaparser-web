package edu.arizona.biosemantics.author.parse;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.springframework.stereotype.Component;

import edu.arizona.biosemantics.semanticmarkup.markupelement.description.model.BiologicalEntity;
import edu.arizona.biosemantics.semanticmarkup.markupelement.description.model.Description;
import edu.arizona.biosemantics.semanticmarkup.markupelement.description.model.Statement;

@Component
public class DocumentCreator {
	
	private Namespace bioNamespace = Namespace.getNamespace("bio", "http://www.github.com/biosemantics");
	
	public Document create(Description description) {
		Document document = new Document();
		
		Element root = new Element("treatment");
		root.setNamespace(bioNamespace);
		document.setRootElement(root);
		
		Element descr = new Element("description");
		descr.setAttribute("type", "morphology");
		root.addContent(descr);
		
		for(Statement s : description.getStatements()) {
			descr.addContent(createStatementElement(s));
		}
		
		return document;
	}

	private Element createStatementElement(Statement s) {
		Element statement = new Element("statement");
		statement.setAttribute("id", "d0_s0");
		for(BiologicalEntity b : s.getBiologicalEntities()) {
			Element entity = new Element("biological_entity");
			entity.setAttribute("alter_name", b.getAlterName());
			entity.setAttribute("constraint", b.getConstraint());
			entity.setAttribute("constraintid", b.getConstraintId());
			entity.setAttribute("constraint_original", b.getConstraintOriginal());
			entity.setAttribute("geographical_constraint", b.getGeographicalConstraint());
			entity.setAttribute("id", b.getId());
			entity.setAttribute("in_brackets", b.getInBrackets());
			entity.setAttribute("name", b.getName());
			entity.setAttribute("name_original", b.getNameOriginal());
			entity.setAttribute("notes", b.getNotes());
			entity.setAttribute("ontologyid", b.getOntologyId());
			entity.setAttribute("parallelism_constraint", b.getParallelismConstraint());
			entity.setAttribute("provenance", b.getProvenance());
			entity.setAttribute("src", b.getSrc());
			entity.setAttribute("taxon_constraint", b.getTaxonConstraint());
			entity.setAttribute("type", b.getType());
			entity.setAttribute("taxon_constraint", b.getTaxonConstraint());
			for(edu.arizona.biosemantics.semanticmarkup.markupelement.description.model.Character c : 
				b.getCharacters()) {
				Element character = new Element("character");
				character.setAttribute("char_type", c.getCharType());
				character.setAttribute("constraint", c.getConstraint());
				character.setAttribute("constraintid", c.getConstraintId());
				character.setAttribute("establishment_means", c.getEstablishedMeans());
				character.setAttribute("from", c.getFrom());
				character.setAttribute("from_inclusive", c.getFromInclusive());
				character.setAttribute("from_modifier", c.getFromModifier());
				character.setAttribute("from_unit", c.getFromUnit());
				character.setAttribute("geographical_constraint", c.getGeographicalConstraint());
				character.setAttribute("in_brackets", c.getInBrackets());
				character.setAttribute("is_modifier", c.getIsModifier());
				character.setAttribute("modifier", c.getModifier());
				character.setAttribute("name", c.getName());
				character.setAttribute("notes", c.getNotes());
				character.setAttribute("ontologyid", c.getOntologyId());
				character.setAttribute("organ_constraint", c.getOrganConstraint());
				character.setAttribute("other_constraint", c.getOtherConstraint());
				character.setAttribute("parallelism_constraint", c.getParallelismConstraint());
				character.setAttribute("provenance", c.getProvenance());
				character.setAttribute("src", c.getSrc());
				character.setAttribute("taxon_constraint", c.getTaxonConstraint());
				character.setAttribute("to", c.getTo());
				character.setAttribute("to_inclusive", c.getToInclusive());
				character.setAttribute("to_modifier", c.getToModifier());
				character.setAttribute("to_unit", c.getToUnit());
				character.setAttribute("type", c.getType());
				character.setAttribute("unit", c.getUnit());
				character.setAttribute("upper_restricted", c.getUpperRestricted());
				character.setAttribute("value", c.getValue());
				entity.addContent(character);
			}
			statement.addContent(entity);
		}
		
		return statement;
	}

}
