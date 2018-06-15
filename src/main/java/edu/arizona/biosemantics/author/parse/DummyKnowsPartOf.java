package edu.arizona.biosemantics.author.parse;

import edu.arizona.biosemantics.semanticmarkup.enhance.know.KnowsPartOf;

public class DummyKnowsPartOf implements KnowsPartOf {

	@Override
	public boolean isPartOf(String part, String parent) {
		return false;
	}

}
