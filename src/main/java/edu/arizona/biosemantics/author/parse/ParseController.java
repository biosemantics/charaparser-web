package edu.arizona.biosemantics.author.parse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import org.jdom2.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import edu.arizona.biosemantics.author.parse.model.BiologicalEntity;
import edu.arizona.biosemantics.author.parse.model.Description;
import edu.arizona.biosemantics.author.parse.model.Relation;
import edu.arizona.biosemantics.author.parse.model.Statement;
import edu.arizona.biosemantics.semanticmarkup.ling.chunk.ChunkCollector;
import edu.arizona.biosemantics.semanticmarkup.markupelement.description.ling.extract.IDescriptionExtractor;
import edu.arizona.biosemantics.semanticmarkup.markupelement.description.transform.SentenceChunkerRun;

@RestController
public class ParseController {

	private MarkupCreator markupCreator;
	private DescriptionResponseCreator descriptionResponseCreator;
	private DocumentCreator documentCreator;
	private EnhanceRun enhanceRun;
	private SentenceSplitter sentenceSplitter;

	@Autowired
	public ParseController(MarkupCreator markupCreator, DocumentCreator documentCreator,
			EnhanceRun enhanceRun, DescriptionResponseCreator descriptionResponseCreator, 
			SentenceSplitter sentenceSplitter) throws Exception {
		this.markupCreator = markupCreator;
		this.documentCreator = documentCreator;
		this.enhanceRun = enhanceRun;
		this.descriptionResponseCreator = descriptionResponseCreator;
		this.sentenceSplitter = sentenceSplitter;
	}

	@GetMapping(value = "/parse", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Description parse(@RequestParam Optional<String> sentence, @RequestParam Optional<String> description) throws Exception {
		List<ChunkCollector> chunkCollectors = new ArrayList<ChunkCollector>();
		if(description.isPresent()) {
			List<String> sentences = sentenceSplitter.split(description.get());	
			for(String s : sentences) {
				SentenceChunkerRun chunkerRun = markupCreator.createChunkerRun(s);
				ChunkCollector chunkCollector = chunkerRun.call();
				System.out.println(chunkCollector.toString());
				chunkCollectors.add(chunkCollector);
			}
		} else if(sentence.isPresent()) {
			SentenceChunkerRun chunkerRun = markupCreator.createChunkerRun(sentence.get());
			ChunkCollector chunkCollector = chunkerRun.call();
			System.out.println(chunkCollector.toString());
			chunkCollectors.add(chunkCollector);
		}
		if(chunkCollectors.isEmpty())
			throw new IllegalArgumentException();
		
		return createDescription(chunkCollectors);
	}
	
	private Description createDescription(List<ChunkCollector> chunkCollectors) throws IOException {
		IDescriptionExtractor descriptionExtractor = markupCreator.createDescriptionExtractor();
		edu.arizona.biosemantics.semanticmarkup.markupelement.description.model.Description description = 
				new edu.arizona.biosemantics.semanticmarkup.markupelement.description.model.Description();
		descriptionExtractor.extract(description, 1, chunkCollectors);

		Document document = documentCreator.create(description);
		enhanceRun.run(document);
		
		return descriptionResponseCreator.create(document);
	}
}