# charaparser-web

Configure as Maven project. Make sure all the charaparser [dependencies](https://github.com/biosemantics/charaparser) are met.

Start the container with services by running [Application](https://github.com/biosemantics/charaparser-web/blob/master/src/main/java/edu/arizona/biosemantics/semanticmarkup/web/Application.java)

Parse a sentence by using a HTTP GET http://localhost:8080/parse?sentence=this%20is%20my%20sentence

A [URL encoder](https://meyerweb.com/eric/tools/dencoder/) will be useful to encode sentences to be passed as parameter.


# Parsed sentence output format

The service will currently output a JSON representation that from the structure resembles charaparser's XML output. Charaparser's XML output is defined with [this XML schema](https://github.com/biosemantics/schemas/blob/master/semanticMarkupOutput.xsd)

Below is an example output:

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