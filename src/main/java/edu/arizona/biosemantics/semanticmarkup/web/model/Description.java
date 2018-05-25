package edu.arizona.biosemantics.semanticmarkup.web.model;

import java.util.List;

public class Description {
	
	private List<Statement> statements;
	private String text;
	
	public Description(String text, List<Statement> statements) {
		this.text = text;
		this.statements = statements;
	}

	public List<Statement> getStatements() {
		return statements;
	}

	public String getText() {
		return text;
	}

	
}
