# charaparser-web

Configure as Maven project. Make sure all the charaparser [dependencies](https://github.com/biosemantics/charaparser) are met.

Start the container with services by running [Application](https://github.com/biosemantics/charaparser-web/blob/master/src/main/java/edu/arizona/biosemantics/semanticmarkup/web/Application.java)

Parse a sentence by using a HTTP GET http://localhost:8080/parse?sentence=this%20is%20my%20sentence

A [URL encoder](https://meyerweb.com/eric/tools/dencoder/) will be useful to encode sentences to be passed as parameter.
