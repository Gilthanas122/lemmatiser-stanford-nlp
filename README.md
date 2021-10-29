# Java NLP Tool based on Stanford NLP

## Introduction

This tool was and is currently developed for supporting a German Linguistic PhD work.
It is based on the NLP modules developed by [Stanford NLP Group](https://nlp.stanford.edu/). 
The aim of this tool is be able to create a database filled with newsarticles and being able to research it. 
As start when provided with a newsarticles text,
it splits it into sentences and texttokens (connects the three newsarticle-sentence-texttoken)
and connects the text tokens with word stems (lemmatypes) and provided also a phrasetype for the given text token.
Since this one is made for a PhD research, its FrontEnd is quite minimalistic.

## Features

* Adding new newsarticle to database
* Counting occurences and returning the most common words
* Counting the occurences of other words that are within the same sentence of the most commmon words
* Showing sentences/newsarticles in which the most common words reside
* Error Handling - Learning: in case there are two or more word stems matching the text token or there are none then it is possible 
to provide a word stem or choose from two or more matching lemmatypes.
* Text classifincation - NOT IMPLEMENTED YET -: based on the number of occurences of some, for the research relevant keywords within the newsarticle text 
, it gives some point value for the given newsarticle


## Installation guide

* Run with Java 11+
* Download and setup environment variables in the application.properties
* Before adding new newsarticles to the DB, 
uncomment the codes in the StanfordNLPGermanApplication class (CommandlineRunner) and run it. 
This piece of code parses the file in resources/static/lemma/lemmatization-de.txt and adds the
the word stems (lemmatypes) and their different forms (lemmatokens) to the db
* After successfully running this, comment the lines out again 

## Endpoints (GET) for the functionalities
* (news-article/create) Adding new newsarticles to the database
* (news-article/get/{newsArticleId}): returns newsarticle belonging to a sentence
* (lemma-type/most-common/{pageNumber}): it can give back in table format the lemmas with the most occurences in the database with pagination
* (sentence/get/{lemmaTypeId}): returns all the sentences in which the given lemmatype occurs
* (sentence/context/{lemmaTypeId}): return the most common word in the given lemmatype's context - within the same sentence
* (text-token/invalid): returns all the texttokens which do not have a matching word stem (lemmatype)
* (text-token/change/{textTokenId}/{textTokenText}): returns the sentence in which the texttoken appeared and gives back two or lemmatypes (if there are matching word stem in the database)

## More about Stanford NLP
* [Stanford NLP Group Lectures](https://www.youtube.com/watch?v=oWsMIW-5xUc&list=PLLssT5z_DsK8HbD2sPcUIDfQ7zmBarMYv)


