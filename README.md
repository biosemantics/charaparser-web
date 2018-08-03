# charaparser-web

## setup / how to run
1. Configure as Maven project
2. Make sure all the charaparser [dependencies](https://github.com/biosemantics/charaparser) are met
3. Start the container with services by running the [Application](https://github.com/biosemantics/charaparser-web/blob/master/src/main/java/edu/arizona/biosemantics/semanticmarkup/web/Application.java)

## service endpoints
* /parse: *Parses morphological descriptive text*
  * Single sentence
    * HTTP GET http://{host}/parse?sentence={URL_encoded_sentence}
    * {URL_encoded_sentence}: The sentence to be parsed
    * Example: GET http://shark.sbs.arizona.edu:8080/parse?sentence=leaf-blade%20orbicular,%206%E2%80%9310%20%C3%97%206%E2%80%9310%20cm
  * Multi sentence
    * HTTP GET http://{host}/parse?description={URL_encoded_description}
    * {URL_encoded_description}: The description to be parsed. A description can contain of multiple sentences.
    * Example: GET http://shark.sbs.arizona.edu:8080/parse?description=Herbs%2C%20perennial%2C%20cespitose%20or%20not%2C%20rhizomatous%2C%20rarely%20stoloniferous.%20Culms%20usually%20trigonous%2C%20sometimes%20round.%20Leaves%20basal%20and%20cauline%2C%20sometimes%20all%20basal%3B
  * The service will respond with a JSON body based on charaparser's [XML output schema](https://github.com/biosemantics/schemas/blob/master/semanticMarkupOutput.xsd). An example follows.
    ```json
    {
      "statements": [
        {
          "id": "d1_s0",
          "text": "leaf-blade orbicular, 6–10 × 6–10 cm",
          "biologicalEntities": [
            {
              "characters": [
                {
                  "isModifier": "false",
                  "name": "shape",
                  "ontologyId": "http://purl.obolibrary.org/obo/PATO_0001934[orbicular:circular/orbicular:1.0]",
                  "src": "d1_s0",
                  "value": "orbicular"
                },
                {
                  "charType": "range_value",
                  "name": "length",
                  "src": "d1_s0",
                  "value": "6 cm - 10 cm"
                },
                {
                  "charType": "range_value",
                  "name": "width",
                  "src": "d1_s0",
                  "value": "6 cm - 10 cm"
                }
              ],
              "id": "o154",
              "name": "leaf-blade",
              "nameOriginal": "leaf-blade",
              "src": "d1_s0",
              "type": "structure"
            }
          ],
          "relations": []
        }
      ]
    }
    ```

* /{ontology}/search: *Searches an ontology for a term*
  * HTTP GET http://{host}/{ontology}/search?term={term}&parent={optional_parent}&relation={optional_relation}
  * {ontology}: The ontology to search for the {term}. Ontology can currenlty be one of PO, PATO, CAREX
  * {term}: The term to search for
  * {optional_parent}: The optional parent the {term} is required to have
  * {optional_relation}: The optional relation the {term} is required to have
  * Example: GET http://shark.sbs.arizona.edu:8080/PO/search?term=quaternary%20leaf%20vein
  * Response body:
    ```json
    {
      "entries": [
        {
          "score": 1,
          "term": "quaternary leaf vein",
          "parentTerm": "leaf lamina vein",
          "resultAnnotations": [
            {
              "property": "http://www.geneontology.org/formats/oboInOwl#hasDbXref",
              "value": "FNA:dba43715-e71f-4192-87a2-489f5b9b4c82"
            },
            {
              "property": "http://www.geneontology.org/formats/oboInOwl#hasDbXref",
              "value": "Gramene:Chih-Wei_Tung"
            },
            {
              "property": "http://www.geneontology.org/formats/oboInOwl#id",
              "value": "PO:0008022"
            },
            {
              "property": "http://www.geneontology.org/formats/oboInOwl#hasDbXref",
              "value": "PO_GIT:435"
            },
            {
              "property": "http://purl.obolibrary.org/obo/IAO_0000115",
              "value": "A leaf lamina vein (PO:0020138) arising from a tertiary leaf vein (PO:0008021)."
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
              "property": "http://www.geneontology.org/formats/oboInOwl#hasExactSynonym",
              "value": "vena cuaternaria (Spanish, exact)"
            },
            {
              "property": "http://www.geneontology.org/formats/oboInOwl#hasSynonymType",
              "value": ""
            },
            {
              "property": "http://www.w3.org/2000/01/rdf-schema#label",
              "value": "quaternary leaf vein"
            },
           {
              "property": "http://www.geneontology.org/formats/oboInOwl#hasDbXref",
              "value": "FNA:e3ab3fff-3015-4b76-af51-fc69ee9396d8"
            },
            {
              "property": "http://www.w3.org/2000/01/rdf-schema#comment",
              "value": "Vein orders only apply to hierarchically branching vein patterns, not to dichotomously branching vein patterns, as found in some ferns and gymnosperms."
            },
            {
              "property": "http://www.geneontology.org/formats/oboInOwl#hasNarrowSynonym",
              "value": "veinlet (narrow)"
            },
            {
              "property": "http://www.geneontology.org/formats/oboInOwl#hasDbXref",
              "value": "POC:Maria_Alejandra_Gandolfo"
            },
            {
              "property": "http://www.geneontology.org/formats/oboInOwl#hasOBONamespace",
              "value": "plant_anatomy"
            },
            {
              "property": "http://www.geneontology.org/formats/oboInOwl#hasNarrowSynonym",
              "value": "cross-vein (narrow)"
            },
            {
              "property": "http://www.geneontology.org/formats/oboInOwl#hasRelatedSynonym",
              "value": "fourth order leaf vein (related)"
            },
            {
              "property": "http://www.geneontology.org/formats/oboInOwl#hasExactSynonym",
              "value": "四次葉脈 (Japanese, exact)"
            }
          ]
        }
      ]
    }
    ```

* /class: *Creates a class in the carex ontology*
  * HTTP POST http://{host}/class
  * Request body:
    ```json
    {
      "term": "root-apex",
      "superclassIRI": "http://biosemantics.arizona.edu/ontologies/carex#apex",
      "definition": "the apex of the root",
      "elucidation": "http://some.illustration.of/the/apex-root.jpg"
    }
    ```

  * The response body will be either 
    * IRI of the newly created clas
    * UNSUCCESSFULLY
    * NO_OPERATION
  * Response Body:
    ```json
    {IRI}|UNSUCCESSFULLY|NO_OPERATION
    ```

* /esynonym: *Creates an exact synonym in the carex ontology*
  * HTTP POST http://{host}/esynonym
  * Request body:
    ```json
    {
      "term": "root-tip",
      "classIRI": "http://biosemantics.arizona.edu/ontologies/carex#root-apex"
    }
    ```

  * Response Body:
    ```json
    SUCCESSFULLY|UNSUCCESSFULLY|NO_OPERATION
    ```

* /bsynonym: *Creates a broader synonym in the carex ontology*
  * HTTP POST http://{host}/bsynonym
  * Request body:
    ```json
    {
      "term": "root-tip",
      "classIRI": "http://biosemantics.arizona.edu/ontologies/carex#root-apex"
    }
    ```

  * Response Body:
    ```json
    SUCCESSFULLY|UNSUCCESSFULLY|NO_OPERATION
    ```

* /partOf: *Creates a part-of relation in the carex ontology*
  * HTTP POST http://{host}/partOf
  * Request body:
    ```json
    {
      "bearerIRI": "http://biosemantics.arizona.edu/ontologies/carex#root",
      "partIRI": "http://biosemantics.arizona.edu/ontologies/carex#apex"
    }
    ```

  * The response body will be either 
    * IRI of the newly created clas
    * UNSUCCESSFULLY
    * NO_OPERATION
  * Response Body:
    ```json
    SUCCESSFULLY|UNSUCCESSFULLY|NO_OPERATION
    ```

* /hasPart: *Creates a has-part relation in the carex ontology*
  * HTTP POST <host>/hasPart
  * Request body:
 
    ```json
    {
      "bearerIRI": "http://biosemantics.arizona.edu/ontologies/carex#root",
      "partIRI": "http://biosemantics.arizona.edu/ontologies/carex#apex"
    }
    ```
 
  * The response body will be either 
    * IRI of the newly created clas
    * UNSUCCESSFULLY
    * NO_OPERATION
  * Response Body:
    ```json
    SUCCESSFULLY|UNSUCCESSFULLY|NO_OPERATION
    ``` 
* /save: *Persists the current state of the carex ontology to the file system*
  * HTTP POST <host>/save
