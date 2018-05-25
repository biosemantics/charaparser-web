# charaparser-web

## setup / how to run
1. Configure as Maven project
2. Make sure all the charaparser [dependencies](https://github.com/biosemantics/charaparser) are met
3. Start the container with services by running the [Application](https://github.com/biosemantics/charaparser-web/blob/master/src/main/java/edu/arizona/biosemantics/semanticmarkup/web/Application.java)

## service endpoints
 [![(https://run.pstmn.io/button.svg)](https://app.getpostman.com/run-collection/9b1918b76cb7380d8a5f)

* /parse
  * HTTP GET http://localhost:8080/parse?sentence={URL_encoded_sentence}
  * {URL_encoded_sentence}: The sentence to be parsed
  * The service will currently output a JSON representation based on charaparser's [XML output schema](https://github.com/biosemantics/schemas/blob/master/semanticMarkupOutput.xsd). An example follows.
  * Response body:
```
{
    "statements": [
        {
            "id": "d1_s0",
            "notes": null,
            "provenance": null,
            "text": " Petals mostly 5, deciduous.",
            "biologicalEntities": [
                {
                    "alterName": null,
                    "characters": [
                        {
                            "charType": null,
                            "constraint": null,
                            "constraintId": null,
                            "establishedMeans": null,
                            "from": null,
                            "fromInclusive": null,
                            "fromModifier": null,
                            "geographicalConstraint": null,
                            "inBrackets": null,
                            "isModifier": null,
                            "modifier": "mostly",
                            "name": "count",
                            "notes": null,
                            "ontologyId": null,
                            "organConstraint": null,
                            "otherConstraint": null,
                            "parallelismConstraint": null,
                            "provenance": null,
                            "taxonConstraint": null,
                            "to": null,
                            "toInclusive": null,
                            "toModifier": null,
                            "toUnit": null,
                            "type": null,
                            "src": "d1_s0",
                            "unit": null,
                            "upperRestricted": null,
                            "value": "5"
                        }
                    ],
                    "constraint": null,
                    "constraintId": null,
                    "constraintOriginal": null,
                    "geographicalConstraint": null,
                    "id": "o4",
                    "inBrackets": null,
                    "name": "whole_organism",
                    "nameOriginal": "",
                    "notes": null,
                    "ontologyId": null,
                    "paralellismConstraint": null,
                    "provenance": null,
                    "src": "d1_s0",
                    "taxonConstraint": null,
                    "type": "structure"
                }
            ],
            "relations": []
        }
    ],
    "text": null
}
```

* /{ontology}/search
  * HTTP GET http://localhost:8080/{ontology}/search?term={term}&parent={optional_parent}&relation={optional_relation}
  * {ontology}: The ontology to search for the {term}. Ontology can currenlty be one of PO, PATO, CAREX
  * {term}: The term to search for
  * {optional_parent}: The optional parent the {term} is required to have
  * {optional_relation}: The optional relation the {term} is required to have
  * Response body:
```
{
    "entries": [
        {
            "score": 1,
            "term": "nucellar epidermis",
            "parentTerm": "megasporangium exothecium",
            "resultAnnotations": [
                {
                    "property": "http://www.w3.org/2000/01/rdf-schema#label",
                    "value": "nucellar epidermis"
                },
                {
                    "property": "http://www.geneontology.org/formats/oboInOwl#hasExactSynonym",
                    "value": "珠心 表皮 (Japanese, exact)"
                },
                {
                    "property": "http://www.geneontology.org/formats/oboInOwl#hasDbXref",
                    "value": "Gramene:Anuradha_Pujar"
                },
                {
                    "property": "http://www.geneontology.org/formats/oboInOwl#hasSynonymType",
                    "value": ""
                },
                {
                    "property": "http://www.geneontology.org/formats/oboInOwl#hasDbXref",
                    "value": "NIG:Yukiko_Yamazaki"
                },
                {
                    "property": "http://www.geneontology.org/formats/oboInOwl#hasSynonymType",
                    "value": ""
                },
                {
                    "property": "http://purl.obolibrary.org/obo/IAO_0000115",
                    "value": "A portion of plant tissue that is the morphologically distinct outer layer of the nucellus."
                },
                {
                    "property": "http://www.geneontology.org/formats/oboInOwl#hasDbXref",
                    "value": "POC:Maria_Alejandra_Gandolfo"
                },
                {
                    "property": "http://www.geneontology.org/formats/oboInOwl#inSubset",
                    "value": ""
                },
                {
                    "property": "http://www.geneontology.org/formats/oboInOwl#hasExactSynonym",
                    "value": "epidermis nucelar (Spanish, exact)"
                },
                {
                    "property": "http://www.geneontology.org/formats/oboInOwl#inSubset",
                    "value": ""
                },
                {
                    "property": "http://www.geneontology.org/formats/oboInOwl#hasOBONamespace",
                    "value": "plant_anatomy"
                },
                {
                    "property": "http://www.geneontology.org/formats/oboInOwl#hasExactSynonym",
                    "value": "portion of nucellar epidermis (exact)"
                },
                {
                    "property": "http://www.geneontology.org/formats/oboInOwl#id",
                    "value": "PO:0008006"
                }
            ]
        }
    ]
}
```

* /submit
  * HTTP POST http://localhost:8080/submit
  * Request body
```
{
    "term": "red",
    "parentTerm": "parentterm",
    "definition": "definition",
    "sentence": "sentence",
    "author": "author",
    "relatedTaxon": "o1",
    "submissionTime": 123456
}
```  
  * Response Body:
```  
{
    "term": "red"
}
```
