package edu.arizona.biosemantics.semanticmarkup.web;

import java.util.Arrays;
import java.util.List;

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
import edu.arizona.biosemantics.semanticmarkup.markupelement.description.model.Description;
import edu.arizona.biosemantics.semanticmarkup.markupelement.description.transform.SentenceChunkerRun;

@RestController
public class MarkupController {

	private MarkupCreator markupCreator;

	public MarkupController() throws Exception {
		this.markupCreator = new MarkupCreator();
	}

	@RequestMapping(value = "/parse", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public String parse(@RequestParam String sentence) throws Exception {
		SentenceChunkerRun chunkerRun = markupCreator.createChunkerRun(sentence);
		ChunkCollector chunkCollector = chunkerRun.call();
		System.out.println(chunkCollector.toString());

		IDescriptionExtractor descriptionExtractor = markupCreator.createDescriptionExtractor();
		List<ChunkCollector> chunkCollectors = Arrays.asList(chunkCollector);
		Description description = new Description();
		descriptionExtractor.extract(description, 1, chunkCollectors);

		// return description;
		ObjectMapper mapper = new ObjectMapper();
		AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
		AnnotationIntrospector secondary = new JacksonAnnotationIntrospector();
		mapper.setAnnotationIntrospector(new AnnotationIntrospectorPair(introspector, secondary));
		return mapper.writeValueAsString(description);
	}
}