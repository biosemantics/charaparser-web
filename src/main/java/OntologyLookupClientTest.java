import java.util.HashSet;

import edu.arizona.biosemantics.oto.common.ontologylookup.search.OntologyLookupClient;

public class OntologyLookupClientTest {

	public static void main(String[] args) {
		HashSet<String> entityOntologies = new HashSet<String>();
		HashSet<String> qualityOntologies = new HashSet<String>();
		OntologyLookupClient client = new OntologyLookupClient(entityOntologies, qualityOntologies, "ontologies", "wordnet/wn31/dict");
		client.searchStructure("leaf", "", "");
	}
}
