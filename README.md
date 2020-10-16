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
* /createUserOntology: *Make a (set) of ontology ready. This service needs to be called before any of the requests listed below can be used*
  * HTTP POST http://{host}/createUserOntology
  * Request body: user can take an empty string as its value, in this case, a shared ontology will be made ready for all requests with an empty user. If user has a non-empty value, such as an id, a copy of the ontology will be made ready for this specific user. Subsequent calls to access the ontology will need to use user field with the id. Ontologies can be exp or exp.
  ```json
  {
	  "user":"2",
	   "ontologies":"exp"
  }
  ```
  
* /{ontology}/search: *Searches an ontology for a term*
  * HTTP GET http://{host}/{ontology}/search?user={optional_user}&term={term}&ancestorIRI={ancestorIRI}&parent={optional_parent}&relation={optional_relation}
  * {ontology}: The ontology to search for the {term}. Ontology must be in lower case, e.g., exp.
  * {optional_user}: If present, the user specific version of the ontology will be used for the search. Otherwise, a shared version of the ontology will be used (See /createUserOntology).
  * {term}: The term to search for
  * {optional_ancestorIRI}: The ancestor the search term must have. Use %23 for # in the IRI.
  * {optional_parent}: The optional parent the {term} is required to have
  * {optional_relation}: The optional relation the {term} is required to have
  * Example: 
  GET http://shark.sbs.arizona.edu:8080/exp/search?term=reddish&ancestorIRI=http://biosemantics.arizona.edu/ontologies/exp%23colored
  GET http://shark.sbs.arizona.edu:8080/exp/search?term=quaternary%20leaf%20vein (this works only after a call to /createUserOntology with an empty user and exp ontology as parameters)
  * Response body: returns classes related to the term in someway, such as a synonym, or other relationships.  
    ```json
    {
      "entries": [
        {
          "score": 1,
          "term": "quaternary leaf vein",
          "parentTerm": "leaf lamina vein",
          "resultAnnotations": [
            {
              "property": "elucidation",
              "value": "http://googledrive.com/image.jpg"
            },
            {
              "property": "part of",
              "value": "PO:0025034"
            },
            
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
* /getDefinition: *retrieve the defintion string of a matching class in the named ontology in /parse* 
  * HTTP GET  http://{host}/{ontology}/search?user={optional_user}&term={term}&baseIri={baseIri}
  * {ontology}: The ontology to search for the {term}. Ontology must be in lower case, e.g., exp.
  * {optional_user}: If present, the user specific version of the ontology will be used for the search. Otherwise, a shared version of the ontology will be used (See /createUserOntology).
  * {term}: The term to search the definition for
  * {baseIri}: The base iri of the ontology id for the term. The complete ontology id=base_iri#term.
  * Example: GET http://shark.sbs.arizona.edu:8080/exp/getDefinition?baseIri=http://biosemantics.arizona.edu/ontologies/exp&term=apex (this works only after a call to /parse with an empty user and exp ontology as parameters, and term in /parse has an ontology id)
  * Response Body: the definition as a tring
  
 
* /class: *Creates a class in the named ontology*
  * HTTP POST http://{host}/class
  * Request body: If user value is empty, a shared ontology will be used. Otherwise, a user-specific version of the ontology will be used (See /createUserOntology). Fields elucidation and logicDefintion are optional. All other fields are required of a non-empty value.
    ```json
    {
     	"user":"",
     	"ontology":"exp",
      "term": "root-apex",
      "superclassIRI": "http://biosemantics.arizona.edu/ontologies/exp#apex",
      "definition": "the apex of the root",
      "elucidation": "http://some.illustration.of/the/apex-root.jpg",
      "createdBy": "hongcui",
      "creationDate": "09-18-2017",
      "definitionSrc": "hongcui",
      "examples": "root apex blah blah blah, used in taxon xyz",
      "logicDefinition": "'root apex' and 'part of' some root"
    }
    ```

  * The response body will be either 
    * IRI of the newly created clas
    * UNSUCCESSFULLY
    * NO_OPERATION (NO_OPERATION means the class already exists and nothing need to be done)
    * Error message in case of logic definition parsing failure.

  * Response Body:
    ```json
    {IRI}|UNSUCCESSFULLY|NO_OPERATION
    ```

* /esynonym: *Creates an exact synonym to the class in the named ontology if term is not already a class. Otherwise, make two classes equivalent classes*
  * HTTP POST http://{host}/esynonym
  * Request body: If user value is empty, a shared ontology will be used. Otherwise, a user-specific version of the ontology will be used (See /createUserOntology). All other fields are required of a non-empty value.
    ```json
    {
      "user":"",
      "ontology":"exp",
      "term": "root-tip",
      "classIRI": "http://biosemantics.arizona.edu/ontologies/exp#root-apex",
      "decisionExperts": "hong;bruce",
      "decisionDate": "2020-01-15"
    }
    ```

  * Response Body:
    ```json
    SUCCESSFULLY|UNSUCCESSFULLY|NO_OPERATION
    ```

* /bsynonym: *Creates a broader synonym to the class in the named ontology if term is not already a class, otherwise, no-operation*
  * HTTP POST http://{host}/bsynonym
  * Request body:If user value is empty, a shared ontology will be used. Otherwise, a user-specific version of the ontology will be used (See /createUserOntology). All other fields are required of a non-empty value.
    ```json
    {
     	"user":"",
     	"ontology":"exp",
      "term": "root-tip",
      "classIRI": "http://biosemantics.arizona.edu/ontologies/exp#root-apex",
      "decisionExperts": "hong;bruce",
      "decisionDate": "2020-01-15"
    }
    ```

  * Response Body:
    ```json
    SUCCESSFULLY|UNSUCCESSFULLY|NO_OPERATION
    ```
    
* /definition: *add a defintion property to the class in the named ontology*
  * HTTP POST http://{host}/definition
  * Request body:If user value is empty, a shared ontology will be used. Otherwise, a user-specific version of the ontology will be used (See /createUserOntology). Fields "decisionExperts" and "decisionDate" are for Conflict Resolver user only. All other fields are required of a non-empty value.
    ```json
    {
     	"user":"",
     	"ontology":"exp",
      "definition": "the summit of a root",
      "providedBy": "hongcui",
      "exampleSentence": "root apex rounded",
      "classIRI": "http://biosemantics.arizona.edu/ontologies/exp#root-apex",
      	"decisionExperts": "hongcui via Conflict Resolver",
	"decisionDate": "2020-05-11"
    }
    ```

  * Response Body:
    ```json
    SUCCESSFULLY|UNSUCCESSFULLY|NO_OPERATION
    ```

* /comment: *add a rdfs:comment property to the class in the named ontology*
  * HTTP POST http://{host}/comment
  * Request body:If user value is empty, a shared ontology will be used. Otherwise, a user-specific version of the ontology will be used (See /createUserOntology). All other fields are required of a non-empty value.
    ```json
    {
     	"user":"",
     	"ontology":"exp",
      "comment": "not sure this term covers my example",
      "providedBy": "hongcui",
      "exampleSentence": "root ends rounded",
      "classIRI": "http://biosemantics.arizona.edu/ontologies/exp#root-apex"
    }
    ```

  * Response Body:
    ```json
    SUCCESSFULLY|UNSUCCESSFULLY|NO_OPERATION
    ```

* /partOf: *Creates a part-of relation between the part and the bearer (part is 'part_of' bearer) in the named ontology*
  * HTTP POST http://{host}/partOf
  * Request body: If user value is empty, a shared ontology will be used. Otherwise, a user-specific version of the ontology will be used (See /createUserOntology). All other fields are required of a non-empty value.
    ```json
    {
    	 "user":"",
	 "ontology":"exp",
      "bearerIRI": "http://biosemantics.arizona.edu/ontologies/exp#root",
      "partIRI": "http://biosemantics.arizona.edu/ontologies/exp#apex",
      "decisionExperts": "hong;bruce",
      "decisionDate": "2020-01-15"
    }
    ```

  * Response Body:
    ```json
    SUCCESSFULLY|UNSUCCESSFULLY|NO_OPERATION
    ```
* /maybePartOf: *Creates a maybepart-of relation between the part and the bearer (part is 'part_of' bearer) in the named ontology*
  * HTTP POST http://{host}/maybePartOf
  * Request body: If user value is empty, a shared ontology will be used. Otherwise, a user-specific version of the ontology will be used (See /createUserOntology). All other fields are required of a non-empty value.
    ```json
    {
    	 "user":"",
	 "ontology":"exp",
      "bearerIRI": "http://biosemantics.arizona.edu/ontologies/exp#root",
      "partIRI": "http://biosemantics.arizona.edu/ontologies/exp#apex",
      "decisionExperts": "hong;bruce",
      "decisionDate": "2020-01-15"
    }
    ```

  * Response Body:
    ```json
    SUCCESSFULLY|UNSUCCESSFULLY|NO_OPERATION
    ```
    
* /hasPart: *Creates a has-part relation between the bearer and the part (bearer 'has part' part) in the named ontology. *
  * HTTP POST <host>/hasPart
  * Request body:If user value is empty, a shared ontology will be used. Otherwise, a user-specific version of the ontology will be used (See /createUserOntology). All other fields are required of a non-empty value.
 
    ```json
    {
      "user":"",
      "ontology":"exp",
      "bearerIRI": "http://biosemantics.arizona.edu/ontologies/exp#root",
      "partIRI": "http://biosemantics.arizona.edu/ontologies/exp#apex",
      "decisionExperts": "bruce:hong",
      "decisionDate": "2020-01-15"
    }
    ```
 
  * Response Body:
    ```json
    SUCCESSFULLY|UNSUCCESSFULLY|NO_OPERATION
    ``` 
* /save: *Persists the current state of the named ontology to the file system*
  * HTTP POST <host>/save
  * Request body:If user value is empty, the shared ontology will be saved. Otherwise, a user-specific version of the ontology will be saved (See /createUserOntology).
 
    ```json
    {
      "user":"",
      "ontology":"exp",
    }
    ```
   * Response Body:
    ```json
    SUCCESSFULLY|UNSUCCESSFULLY|NO_OPERATION
      ``` 
* /{ontology}/getSubclasses: *Obtain the subclasses of the term as a JSON object*
  * HTTP  http://{host}/{ontology}/getSubclasses?user={optional_user}&baseIri={baseIri}&term={term}
  * {ontology}: The ontology to search for the {term}. Ontology must be in lower case, e.g., exp.
  * {optional_user}: If present, the user specific version of the ontology will be used for the search. Otherwise, a shared version of the ontology will be used (See /createUserOntology).
  * {term}: The term for which to find its subclasses
  * {baseIri}: The base iri of the ontology id for the term. The complete ontology id of the term =base_iri#term.
  * Example: GET http://shark.sbs.arizona.edu:8080/exp/getSubclasses?baseIri=http://biosemantics.arizona.edu/ontologies/exp&term=coloration 
  * Response body:
  ```json
  {
    "data": {
        "details": [
            {
                "IRI": "http://biosemantics.arizona.edu/ontologies/exp#coloration"
            }
        ]
    },
    "children": [
        {
            "data": {
                "details": [
                    {
                        "IRI": "http://biosemantics.arizona.edu/ontologies/exp#reddish"
                    }
                ]
            },
            "children": [
                {
                    "data": {
                        "details": [
                            {
                                "IRI": "http://biosemantics.arizona.edu/ontologies/exp#dotted-reddish"
                            }
                        ]
                    },
                    "text": "dotted reddish"
                },
  ```

* /{ontology}/getTree: *Obtain the entire ontology as a JSON object*
  * HTTP GET http://{host}/{ontology}/getTree?user={optional_user}
  * {ontology}: The ontology content to obtain. Ontology name must be in lower case, e.g., exp.
  * {user}: If present, the user-specific version of the ontology will be used. Otherwise, a shared version of the ontology will be used (See /createUserOntology).
  * Example: GET http://shark.sbs.arizona.edu:8080/exp/getTree (this works only after a call to /createUserOntology with an empty user and exp ontology as parameters)
  * Response body: 
    ```json
    {
    "data": {
        "details": [
            {
                "IRI": "http://www.w3.org/2002/07/owl#Thing"
            }
        ]
    },
    "children": [
        {
            "data": {
                "details": [
                    {
                        "IRI": "http://purl.obolibrary.org/obo/UBERON_0001062"
                    }
     ```
    
    
* /{ontology}/getStandardCollection: *Obtain the standard/recommended characters*
  * HTTP GET http://{host}/{ontology}/getStandardCollection?user={optional_user}
  * {ontology}: The ontology content to obtain. Ontology name must be in lower case, e.g., exp.
  * {user}: If present, the user-specific version of the ontology will be used. Otherwise, a shared version of the ontology will be used (See /createUserOntology).
  * Example: GET http://shark.sbs.arizona.edu:8080/exp/getStandardCollection? (this works only after a call to /createUserOntology with an empty user and exp ontology as parameters)
  * Response body: 
    ```json
    {
    "entries": [
        {
            "score": 1.0,
            "term": "number of staminate flowers",
            "parentTerm": "perceived quantity",
            "resultAnnotations": [
                {
                    "property": "http://www.geneontology.org/formats/oboInOwl#id",
                    "value": "http://biosemantics.arizona.edu/ontologies/exp#number_of_staminate_flowers"
                },
                {
                    "property": "http://biosemantics.arizona.edu/ontologies/exp#quality_of",
                    "value": "http://biosemantics.arizona.edu/ontologies/exp#staminate_flower"
                },
                {
                    "property": "http://biosemantics.arizona.edu/ontologies/exp#in_collection",
                    "value": "http://biosemantics.arizona.edu/ontologies/exp#exp_standard_character_set"
                },
                {
                    "property": "http://www.w3.org/2000/01/rdf-schema#label",
                    "value": "number of staminate flowers"
                }
            ]
        },
	```
* /moveFromToreviewToSuperclass: *add the term as a subclass of the superclass, deprecate the old term (subclass of toreview), remove (category) from the subclassTerm*
  * HTTP POST <host>/moveFromToreviewToSuperclass
  * Request body:If user value is empty, the shared ontology will be used. Otherwise, a user-specific version of the ontology will be used (See /createUserOntology). All other fields are required of a non-empty value.
 
    ```json
     {
	"user": "",
	"ontology": "exp",
	"subclassIRI": "http://biosemantics.arizona.edu/ontologies/exp#attachment_%28structure%29", 
	"superclassIRI": "http://biosemantics.arizona.edu/ontologies/exp#anatomical_structure",
	"subclassTerm": "attachment (structure)",
	"decisionExperts": "hong;bruce",
	"decisionDate": "2020-01-15"
     }
    ```
   * Response Body:
    ```json
    SUCCESSFULLY|UNSUCCESSFULLY|NO_OPERATION
      ``` 
    
* /{ontology}/getClassesWMSuperclasses: *Conflict type 1: Obtain classes with multiple superclasses and with at least one example sentence*
  * HTTP GET http://{host}/{ontology}/getClassesWMSuperclasses?user={optional_user}
  * {ontology}: The ontology content to obtain. Ontology name must be in lower case, e.g., exp.
  * {user}: If present, the user-specific version of the ontology will be used. Otherwise, a shared version of the ontology will be used (See /createUserOntology).
  * Example: GET http://shark.sbs.arizona.edu:8080/exp/getClassesWMSuperclasses? (this works only after a call to /createUserOntology with an empty user and exp ontology as parameters)
  * Response body: 
    ```json
    {
    "terms": {
        "term 80": {
            "iri": "http://biosemantics.arizona.edu/ontologies/exp#purple_banded",
            "sentences": [],
            "label": "purple banded",
            "categories": {
                "category 2": {
                    "iri": "http://biosemantics.arizona.edu/ontologies/exp#purple",
                    "elucidation": [],
                    "name": "purple",
                    "definition": [
                        "\"A color that falls about midway between red and blue in hue\""
                    ]
                },
                "category 1": {
                    "iri": "http://biosemantics.arizona.edu/ontologies/exp#banded",
                    "elucidation": [],
                    "name": "banded",
                    "definition": [
                        "horizontal ring on a vertical structure."
                    ]
                }
            }
        },
	```
* /{ontology}/getClassesWMZdefinitions: *Conflict type 2: Obtain classes with zero or more than one defintions*
  * HTTP GET http://{host}/{ontology}/getClassesWMZdefinitions?user={optional_user}
  * {ontology}: The ontology content to obtain. Ontology name must be in lower case, e.g., exp.
  * {user}: If present, the user-specific version of the ontology will be used. Otherwise, a shared version of the ontology will be used (See /createUserOntology).
  * Example: GET http://shark.sbs.arizona.edu:8080/exp/getClassesWMZdefinitions? (this works only after a call to /createUserOntology with an empty user and exp ontology as parameters)
  * Response body: 
    ```json
    {
    "terms": {
        "term 80": {
            "iri": "http://biosemantics.arizona.edu/ontologies/exp#sheath_front_apex",
            "elucidations": [],
            "sentences": [],
            "label": "sheath front apex",
            "superclass label": [
                "anatomical structure"
            ],
            "definitions": []
        },
	
	```
	
* /{ontology}/getToreviewClasses: *Conflict type 3: Obtain classes that are subclasses of toreview and have exactly one definition*
  * HTTP GET http://{host}/{ontology}/getToreviewClasses?user={optional_user}
  * {ontology}: The ontology content to obtain. Ontology name must be in lower case, e.g., exp.
  * {user}: If present, the user-specific version of the ontology will be used. Otherwise, a shared version of the ontology will be used (See /createUserOntology).
  * Example: GET http://shark.sbs.arizona.edu:8080/exp/getToreviewClasses? (this works only after a call to /createUserOntology with an empty user and exp ontology as parameters)
  * Response body: 
    ```json
    {
    "terms": {
        "term 2": {
            "iri": "http://biosemantics.arizona.edu/ontologies/exp#apical_tooth",
            "elucidations": [],
            "sentences": [
                "beak straight , pale green , not strongly 2_edged , 0 . 6 – 1 . 6 mm , ciliate_serrulate , apical teeth 0 . 2 – 0 . 5 mm ."
            ],
            "label": "apical tooth",
            "definitions": [
                "Term is unclear but could refer to one of two projections (teeth) that are associaed with the beak of the perigynium or projections along the edge of a perigynium body"
            ]
        },
	```
* /deprecate: *add deprecate annotation to the class*
  * HTTP POST <host>/deprecate
  * Request body:If user value is empty, the shared ontology will be used. Otherwise, a user-specific version of the ontology will be used (See /createUserOntology).
 
    ```json
    {
      "user":"",
      "ontology":"exp",
      "decisionDate": "2020-01-15",
      "classIRI": "http://biosemantics.arizona.edu/ontologies/exp#front_apex",
      "decisionExperts": "hong",
      "reasons": "bad term",
      "alternativeTerm": "http://biosemantics.arizona.edu/ontologies/exp#apex"
    }
    ```	
   * Response Body:
    ```json
    SUCCESSFULLY|UNSUCCESSFULLY|NO_OPERATION
      ``` 
* /detachFromSuperclass: *move a subclass term from its superclass term*
  * HTTP POST <host>/detachFromSuperclass
  * Request body:If user value is empty, the shared ontology will be used. Otherwise, a user-specific version of the ontology will be used (See /createUserOntology).
 
    ```json
    {
      "user":"",
      "ontology":"exp",
      "superclassIRI": "http://biosemantics.arizona.edu/ontologies/exp#toreview",
      "subclassIRI": "http://biosemantics.arizona.edu/ontologies/exp#front_apex",
      "decisionDate": "2020-01-15",
      "decisionExperts": "hong"
       }
    ```	
   * Response Body:
    ```json
    SUCCESSFULLY|UNSUCCESSFULLY|NO_OPERATION
      ``` 
    
* /setSuperclass: *add a subclassof relation beween subclass and superclass*
  * HTTP POST <host>/setSuperclass
  * Request body:If user value is empty, the shared ontology will be used. Otherwise, a user-specific version of the ontology will be used (See /createUserOntology).    
    
    ```
    {
	"user": "",
	"ontology": "exp",
	"superclassIRI": "http://biosemantics.arizona.edu/ontologies/exp#reflectance",
	"decisionExperts": "hong",
	"decisionDate": "2020-05-25",
	"subclassIRI": "http://biosemantics.arizona.edu/ontologies/exp#pale_hyaline"
	}
	```
   * Response Body:
    ```json
    SUCCESSFULLY|UNSUCCESSFULLY|NO_OPERATION
      ``` 
* /{ontology}/getDeprecatedClasses: *Obtain current set of classes with owl:deprecated true.
  * HTTP GET http://{host}/{ontology}/getDeprecatedClasses?user={optional_user}
  * {ontology}: The ontology to search for the {term}. Ontology must be in lower case, e.g., exp.
  * {optional_user}: If present, the user specific version of the ontology will be used for the search. Otherwise, a shared version of the ontology will be used (See /createUserOntology).

  * Example: GET http://shark.sbs.arizona.edu:8080/exp/getDeprecatedClasses?
  * Response body: response include at least deprecated IRI and term. May also include replament term/IRI and deprecated reason 
      ```
	{
    "deprecated classes": [
        {
            "deprecated IRI": "http://biosemantics.arizona.edu/ontologies/exp#flattened",
            "deprecate term": "flattened",
            "deprecated reason": "The same as compressed. AntonReznicek 2020-04-30"
        },
        {
            "deprecated IRI": "http://biosemantics.arizona.edu/ontologies/exp#inrolled",
            "deprecate term": "inrolled",
            "replacement IRI": "http://biosemantics.arizona.edu/ontologies/exp#involute",
            "replacement term": "involute",
            "deprecated reason": "same as involute. AntonReznicek 2020-04-30"
        }]
     }
	```
	
	
* /{ontology}/getClassesWithNewDefinition: *Obtain current set of classes that have a new definition since a date.
  * HTTP GET http://{host}/{ontology}/getDeprecatedClasses?user={optional_user}&dateString={date}
  * {ontology}: The ontology to search for the {term}. Ontology must be in lower case, e.g., exp.
  * {optional_user}: If present, the user specific version of the ontology will be used for the search. Otherwise, a shared version of the ontology will be used (See /createUserOntology).
  * {dateString}: in yyyy-mm-dd format

  * Example: GET http://shark.sbs.arizona.edu:8080/exp/getClassesWithNewDefinition?dateString=2020-01-01
  * Response body: response include labels and IRIs of such classes, along with any annotation the class may have, such as synonym and notes. 
      ```
	{
    "classes with new definition": [
        {
            "example_of_usage": "beak straight , pale green , not strongly 2_edged , 0 . 6 – 1 . 6 mm , ciliate_serrulate , apical teeth 0 . 2 – 0 . 5 mm .",
            "definition_source": "AR",
            "term with new definition": "apical tooth",
            "term with new def IRI": "http://biosemantics.arizona.edu/ontologies/carex#apical_tooth",
            "definition": "Term is unclear but could refer to one of two projections (teeth) that are associaed with the beak of the perigynium or projections along the edge of a perigynium body",
            "label": "apical tooth",
            "editor_note": "Definition \"term unclear\" provided by Tony on date 2020-04-30.",
            "creation_date": "2018/02/01"
        }
    ]
	}
	```	
	
* /{ontology}/getMovedClasses: *Obtain current set of classes that have moved to a new superclass since a date.
  * HTTP GET http://{host}/{ontology}/getMovedClasses?user={optional_user}&dateString={dateString}
  * {ontology}: The ontology to search for the {term}. Ontology must be in lower case, e.g., exp.
  * {optional_user}: If present, the user specific version of the ontology will be used for the search. Otherwise, a shared version of the ontology will be used (See /createUserOntology).
  * {dateString}: in yyyy-mm-dd format

  * Example: GET http://shark.sbs.arizona.edu:8080/exp/getMovedClasses?dateString=2020-01-01
  * Response body: response include labels and IRIs of such classes, along with any annotation the class may have, such as synonym and notes. 
      ```
      {
    "moved classes": [
        {
            "example_of_usage": "beak straight , pale green , not strongly 2_edged , 0 . 6 – 1 . 6 mm , ciliate_serrulate , apical teeth 0 . 2 – 0 . 5 mm .",
            "definition_source": "AR",
            "moved term": "apical tooth",
            "moved term IRI": "http://biosemantics.arizona.edu/ontologies/carex#apical_tooth",
            "definition": "Term is unclear but could refer to one of two projections (teeth) that are associaed with the beak of the perigynium or projections along the edge of a perigynium body",
            "label": "apical tooth",
            "editor_note": "Moved class to be a subclass of tooth by hong on date 2020-06-11",
            "creation_date": "2018/02/01"
        }
    ]
	}
	
	```	

* /{ontology}/getSynonymConflicts: *Obtain phrases that are exact synonyms to multiple classes, and classes involved in equivalent class relations.
  * HTTP GET http://{host}/{ontology}/getSynonymConflicts?user={optional_user}
  * {ontology}: The ontology to search for the {term}. Ontology must be in lower case, e.g., exp.
  * {optional_user}: If present, the user specific version of the ontology will be used for the search. Otherwise, a shared version of the ontology will be used (See /createUserOntology).
  * Example: GET http://shark.sbs.arizona.edu:8080/exp/getSynonymConflicts?
  * Response body: 
      ```
      {
    "synonym conflicts": [
        {
            "exact synonym 1": "apexes",
            "classes": [
                {
                    "iri": "http://biosemantics.arizona.edu/ontologies/carex#leaf",
                    "elucidations": [],
                    "sentences": [],
                    "definition": "A phyllome (PO:0006001) that is not associated with a reproductive structure.",
                    "label": "leaf",
                    "termCreator": "carex team"
                },
                {
                    "iri": "http://biosemantics.arizona.edu/ontologies/carex#apex",
                    "elucidations": [],
                    "sentences": [
                        "terminal spike gynecandrous or staminate, 8–25 mm. Pistillate scales lanceolate, shorter and narrower than perigynia, apex acute or mucronate, spinulose. [FNA, Carex aboriginum]\n\nPerygynia veined, broadly ovate, (4.7–)5–6.6 × 2.2–3.4 mm, distal margins serrulate, spinulose, apex gradually beaked; beak 0.5–1 mm, bidentate, teeth spreading, spinulose. [FNA, Carex aboriginum]"
                    ],
                    "definition": "the upper surface of a structure",
                    "label": "apex",
                    "termCreator": "carex team"
                }
            ]
        },
        {
            "exact synonym 2": "concealing",
            "classes": [
                {
                    "iri": "http://biosemantics.arizona.edu/ontologies/carex#concealed",
                    "elucidations": [],
                    "sentences": [],
                    "definition": "\"A positional quality inhering in a bearer by virtue of the bearer being hidden from view\"",
                    "label": "concealed",
                    "termCreator": "carex team"
                },
                {
                    "iri": "http://biosemantics.arizona.edu/ontologies/carex#enclosing",
                    "elucidations": [],
                    "sentences": [],
                    "definition": "\"A spatial quality inhering in a bearer by virtue of the bearer's being extended on all sides of another entity simultaneously\"",
                    "label": "enclosing",
                    "termCreator": "carex team"
                }
            ]
        },
        {
            "exact synonym 3": "leaf sheath",
            "classes": [
                {
                    "iri": "http://biosemantics.arizona.edu/ontologies/carex#leaf_sheath",
                    "elucidations": [],
                    "sentences": [],
                    "definition": "The tubular portion of the leaf, wrapping the shoot and to which the blade is joined distally",
                    "label": "leaf_sheath",
                    "termCreator": "carex team"
                },
                {
                    "iri": "http://biosemantics.arizona.edu/ontologies/carex#sheath",
                    "elucidations": [],
                    "sentences": [],
                    "definition": "The tubular portion of the leaf, between the node and the blade",
                    "label": "sheath",
                    "termCreator": "carex team"
                }
            ]
        },
        {
            "equ class 1": "http://biosemantics.arizona.edu/ontologies/carex#apical_tooth",
            "elucidations": [],
            "sentences": [
                "beak straight , pale green , not strongly 2_edged , 0 . 6 – 1 . 6 mm , ciliate_serrulate , apical teeth 0 . 2 – 0 . 5 mm ."
            ],
            "equivalent classes": [
                {
                    "iri": "http://biosemantics.arizona.edu/ontologies/carex#apex",
                    "elucidations": [],
                    "sentences": [
                        "terminal spike gynecandrous or staminate, 8–25 mm. Pistillate scales lanceolate, shorter and narrower than perigynia, apex acute or mucronate, spinulose. [FNA, Carex aboriginum]\n\nPerygynia veined, broadly ovate, (4.7–)5–6.6 × 2.2–3.4 mm, distal margins serrulate, spinulose, apex gradually beaked; beak 0.5–1 mm, bidentate, teeth spreading, spinulose. [FNA, Carex aboriginum]"
                    ],
                    "definition": "the upper surface of a structure",
                    "label": "apex",
                    "termCreator": "carex team"
                }
            ],
            "definition": "Term is unclear but could refer to one of two projections (teeth) that are associaed with the beak of the perigynium or projections along the edge of a perigynium body",
            "label": "apical tooth",
            "termCreator": "carex team"
        }
    ]
}
	
	```	
* /makeEquivalent: *add equivalent class axiom between the two classes*
  * HTTP POST <host>/makeEquivalent
  * Request body:If user value is empty, the shared ontology will be used. Otherwise, a user-specific version of the ontology will be used (See /createUserOntology).    
    
    ```
	{
	"user":"", 
	"classIRI1":"http://biosemantics.arizona.edu/ontologies/exp#mixed",
	"classIRI2":"http://biosemantics.arizona.edu/ontologies/exp#tinge",
	"ontology":"exp", 
	"decisionExperts":"hong",
	"decisionDate":"07-07-20",
	"reason":""
	}
	```
  * Response Body:
    ```json
    SUCCESSFULLY|UNSUCCESSFULLY|NO_OPERATION
      ``` 
	
	
* /breakEquivalent: *move equivalent class axiom between the two classes*
  * HTTP POST <host>/breakEquivalent
  * Request body:If user value is empty, the shared ontology will be used. Otherwise, a user-specific version of the ontology will be used (See /createUserOntology).    
    
    ```
	{
	"user":"", 
	"classIRI1":"http://biosemantics.arizona.edu/ontologies/exp#mixed",
	"classIRI2":"http://biosemantics.arizona.edu/ontologies/exp#tinge",
	"ontology":"exp", 
	"decisionExperts":"hong",
	"decisionDate":"07-07-20",
	"reason":""
	}
	```
   * Response Body:
    ```json
    SUCCESSFULLY|UNSUCCESSFULLY|NO_OPERATION
      ``` 
      
* /removeESynonym: *move exact synonym annotation from the class*
  * HTTP POST <host>/removeESynonym
  * Request body:If user value is empty, the shared ontology will be used. Otherwise, a user-specific version of the ontology will be used (See /createUserOntology).    
    
    ```
    {
	"user":"", 
	"classIRI":"http://biosemantics.arizona.edu/ontologies/carex#leaflike",
	"ontology":"carex", 
	"term":"leaf-like", 
	"decisionExperts":"hong",
	"decisionDate":"07-07-20",
	"reason": "spelling variance"
    }
	```
   * Response Body:
    ```json
    SUCCESSFULLY|UNSUCCESSFULLY|NO_OPERATION
      ``` 
