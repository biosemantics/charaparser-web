package edu.arizona.biosemantics.author.parse;

import java.util.HashSet;
import java.util.Set;

import edu.arizona.biosemantics.semanticmarkup.enhance.know.KnowsSynonyms;

public class DummyKnowsSynonyms implements KnowsSynonyms {

	@Override
	public Set<SynonymSet> getSynonyms(String term, String category) {
		return new HashSet<SynonymSet>();
	}

}
