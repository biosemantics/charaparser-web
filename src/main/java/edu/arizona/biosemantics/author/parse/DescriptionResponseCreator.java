package edu.arizona.biosemantics.author.parse;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.stereotype.Component;

import edu.arizona.biosemantics.author.parse.model.BiologicalEntity;
import edu.arizona.biosemantics.author.parse.model.Description;
import edu.arizona.biosemantics.author.parse.model.Relation;
import edu.arizona.biosemantics.author.parse.model.Statement;

@Component
public class DescriptionResponseCreator {
	
	public Description create(edu.arizona.biosemantics.semanticmarkup.markupelement.description.model.Description d) {
		return new Description(d.getText(), createResponseStatements(d.getStatements()));
	}

	private List<Statement> createResponseStatements(List<edu.arizona.biosemantics.semanticmarkup.markupelement.description.model.Statement> statements) {
		List<Statement> result = new ArrayList<Statement>();
		for(edu.arizona.biosemantics.semanticmarkup.markupelement.description.model.Statement s : statements) {
			result.add(createResponseStatement(s));
		}
		return result;
	}

	private Statement createResponseStatement(edu.arizona.biosemantics.semanticmarkup.markupelement.description.model.Statement s) {
		return new Statement(s.getId(), s.getNotes(), s.getProvenance(), s.getText(), 
				createResponseBiologicalEntities(s.getBiologicalEntities()), createResponseRelations(s.getRelations()));
	}

	private List<BiologicalEntity> createResponseBiologicalEntities(List<edu.arizona.biosemantics.semanticmarkup.markupelement.description.model.BiologicalEntity> biologicalEntities) {
		List<BiologicalEntity> result = new ArrayList<BiologicalEntity>();
		for(edu.arizona.biosemantics.semanticmarkup.markupelement.description.model.BiologicalEntity e : biologicalEntities) {
			result.add(createResponseStatement(e));
		}
		return result;
	}

	private BiologicalEntity createResponseStatement(edu.arizona.biosemantics.semanticmarkup.markupelement.description.model.BiologicalEntity e) {
		return new BiologicalEntity(e.getAlterName(), createResponseCharacters(e.getCharacters()),
				e.getConstraint(), e.getConstraintId(), e.getConstraintOriginal(), e.getGeographicalConstraint(), 
				e.getId(), e.getInBrackets(), e.getName(), e.getNameOriginal(), e.getNotes(), 
				e.getOntologyId(), e.getParallelismConstraint(), e.getProvenance(), e.getSrc(), e.getTaxonConstraint(), 
				e.getType());
	}

	private List<edu.arizona.biosemantics.author.parse.model.Character> createResponseCharacters(
			LinkedHashSet<edu.arizona.biosemantics.semanticmarkup.markupelement.description.model.Character> characters) {
		List<edu.arizona.biosemantics.author.parse.model.Character> result = 
				new ArrayList<edu.arizona.biosemantics.author.parse.model.Character>();
		for(edu.arizona.biosemantics.semanticmarkup.markupelement.description.model.Character c : characters) {
			result.add(createResponseCharacter(c));
		}
		return result;
	}

	private edu.arizona.biosemantics.author.parse.model.Character createResponseCharacter(
			edu.arizona.biosemantics.semanticmarkup.markupelement.description.model.Character c) {
		return new edu.arizona.biosemantics.author.parse.model.Character(
				c.getCharType(), c.getConstraint(), c.getConstraintId(), c.getEstablishedMeans(), 
				c.getFrom(), c.getFromInclusive(), c.getFromModifier(), c.getFromUnit(), c.getGeographicalConstraint(), 
				c.getInBrackets(), c.getIsModifier(), c.getModifier(), c.getName(), c.getNotes(), 
				c.getOntologyId(), c.getOrganConstraint(), c.getOtherConstraint(), c.getParallelismConstraint(), 
				c.getProvenance(), c.getSrc(), c.getTaxonConstraint(), c.getTo(), c.getToInclusive(), c.getToModifier(),
				c.getToUnit(), c.getType(), c.getUnit(), c.getUpperRestricted(), c.getValue());
	}

	private List<Relation> createResponseRelations(List<edu.arizona.biosemantics.semanticmarkup.markupelement.description.model.Relation> relations) {
		List<Relation> result = new ArrayList<Relation>();
		for(edu.arizona.biosemantics.semanticmarkup.markupelement.description.model.Relation r : relations) {
			result.add(createResponseStatement(r));
		}
		return result;
	}

	private Relation createResponseStatement(edu.arizona.biosemantics.semanticmarkup.markupelement.description.model.Relation r) {
		return new Relation(r.getAlterName(), r.getFrom(), r.getGeographicalConstraint(),
				r.getId(), r.getInBrackets(), r.getModifier(), r.getName(), r.getNegation(), r.getNotes(), r.getOntologyId(), 
				r.getOrganConstraint(), r.getParallelismConstraint(), r.getProvenance(), r.getSrc(), r.getTaxonConstraint(), 
				r.getTo());
	}
	
}
