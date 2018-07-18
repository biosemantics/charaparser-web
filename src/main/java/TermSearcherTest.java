import java.util.HashSet;

import edu.arizona.biosemantics.author.ontology.search.FileSearcher;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.OntologyLookupClient;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.search.TermSearcher;

public class TermSearcherTest {

	public static void main(String[] args) {
		HashSet<String> qualityOntologies = new HashSet<String>();
		qualityOntologies.add("CAREX");
		OntologyLookupClient olc = new OntologyLookupClient(
				new HashSet<String>(), 
				qualityOntologies, 
				"ontologies",
				"wordNet/wn31/dict");
		TermSearcher ts = new TermSearcher(olc);
		ts.searchTerm("test", "quality", 1.0f);
	}
	
}
