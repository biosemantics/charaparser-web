# charaparser-web

## setup / how to run
1. Configure as Maven project
2. Make sure all the charaparser [dependencies](https://github.com/biosemantics/charaparser) are met
3. Start the container with services by running the [Application](https://github.com/biosemantics/charaparser-web/blob/master/src/main/java/edu/arizona/biosemantics/semanticmarkup/web/Application.java)

## service endpoints
* /parse
  * HTTP GET http://localhost:8080/parse?sentence={URL encoded sentence}
  * {URL encoded sentence}: The sentence to be parsed
  * Output format: The service will currently output a JSON representation based on charaparser's [XML output schema](https://github.com/biosemantics/schemas/blob/master/semanticMarkupOutput.xsd). An example follows.

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
  * HTTP GET http://localhost:8080/{ontology}/search?=term={term}&parent={optional parent}&relation={optional relation}
  * {ontology}: The ontology to search for the {term}. Ontology can currenlty be one of PO, PATO, CAREX
  * {term}: The term to search for
  * {optional parent}: The optional parent the {term} is required to have
  * {optional relation}: The optional relation the {term} is required to have
  * Output format:
