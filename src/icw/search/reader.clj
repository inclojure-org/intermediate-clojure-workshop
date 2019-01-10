;; [[file:~/github/intermediate-clojure-workshop/content/search/reader.org::*Code%20Template][Code Template:1]]
(ns icw.search.reader
  (:require [icw.search.common :refer :all])
  (:import [org.apache.lucene.store Directory]
           [org.apache.lucene.analysis Analyzer]
           [org.apache.lucene.index IndexReader DirectoryReader]
           [org.apache.lucene.document Field Document]
           [org.apache.lucene.search Query IndexSearcher ScoreDoc]
           [org.apache.lucene.util QueryBuilder]))
;; Code Template:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/search/reader.org::*Outline][Outline:1]]
(defn ^IndexReader index-reader [^Directory directory])

(defn ^IndexSearcher searcher [^Directory directory])

(defn field->kv [^Field f])

(defn doc->map [^Document doc])

(defn ^Query query [^clojure.lang.Keyword field
                    ^String term
                    ^Analyzer analyzer])

(defn score-docs->ids [^"[Lorg.apache.lucene.search.ScoreDoc;" score-docs])

(defn doc-ids->docs [^IndexSearcher searcher doc-ids])

; Create IndexSearcher given the index (Directory instance)
; Create a Query object given the field, term and analyzer
; .search on the IndexSearcher the generated Query
; Get .scoreDocs from the response
;  ScoreDoc instances give you document-ids as handles
;  Using the document-ids, get the Document instances from the IndexSearcher
; Convert the collection to maps with doc->map
;  Enjoy the convenience of Clojure's support for Iterable
(defn search [^Directory directory
              ^clojure.lang.Keyword field
              ^String search-term
              ^Analyzer analyzer])
;; Outline:1 ends here
