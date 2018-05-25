package edu.arizona.biosemantics.semanticmarkup.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import edu.arizona.biosemantics.semanticmarkup.ling.chunk.ChunkCollector;
import edu.arizona.biosemantics.semanticmarkup.markupelement.description.ling.extract.IDescriptionExtractor;
import edu.arizona.biosemantics.semanticmarkup.markupelement.description.transform.SentenceChunkerRun;
import edu.arizona.biosemantics.semanticmarkup.web.model.BiologicalEntity;
import edu.arizona.biosemantics.semanticmarkup.web.model.Description;
import edu.arizona.biosemantics.semanticmarkup.web.model.Relation;
import edu.arizona.biosemantics.semanticmarkup.web.model.Statement;

@RestController
public class MarkupController {

	private MarkupCreator markupCreator;
	private DescriptionResponseCreator descriptionResponseCreator;

	@Autowired
	public MarkupController(MarkupCreator markupCreator, DescriptionResponseCreator descriptionResponseCreator) throws Exception {
		this.markupCreator = markupCreator;
		this.descriptionResponseCreator = descriptionResponseCreator;
	}

	@RequestMapping(value = "/parse", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Description parse(@RequestParam String sentence) throws Exception {
		SentenceChunkerRun chunkerRun = markupCreator.createChunkerRun(sentence);
		ChunkCollector chunkCollector = chunkerRun.call();
		System.out.println(chunkCollector.toString());

		IDescriptionExtractor descriptionExtractor = markupCreator.createDescriptionExtractor();
		List<ChunkCollector> chunkCollectors = Arrays.asList(chunkCollector);
		edu.arizona.biosemantics.semanticmarkup.markupelement.description.model.Description description = 
				new edu.arizona.biosemantics.semanticmarkup.markupelement.description.model.Description();
		descriptionExtractor.extract(description, 1, chunkCollectors);

		return descriptionResponseCreator.create(description);
		// return description;
		/*ObjectMapper mapper = new ObjectMapper();
		AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
		AnnotationIntrospector secondary = new JacksonAnnotationIntrospector();
		mapper.setAnnotationIntrospector(new AnnotationIntrospectorPair(introspector, secondary));
		return mapper.writeValueAsString(description);*/
	}
}