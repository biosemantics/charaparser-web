package edu.arizona.biosemantics.author.ontology.submission;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.arizona.biosemantics.author.ontology.submission.model.OntologySubmission;
import edu.arizona.biosemantics.author.ontology.submission.model.OntologySubmissionResponse;

@RestController
public class OntologySubmissionController {

	@PostMapping(value = "/submit", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public OntologySubmissionResponse submit(OntologySubmission submission) {
		return new OntologySubmissionResponse(submission.getTerm());
	}
	
}
