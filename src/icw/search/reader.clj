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

;; [[file:~/github/intermediate-clojure-workshop/content/search/reader.org::*Solution][Solution:1]]
(defn index-reader [directory]
  (DirectoryReader/open directory))

(defn searcher [directory]
  (IndexSearcher. (index-reader directory)))

(defn field->kv [f]
  [(-> f .name keyword)
   (.stringValue f)])

(defn doc->map [d]
  (into
   {}
   (map field->kv d)))

(defn query [field term analyzer]
  (let [qb (QueryBuilder. analyzer)
        field (name field)]
    (. qb
       (createBooleanQuery field term))))

(defn score-docs->ids [^"[Lorg.apache.lucene.search.ScoreDoc;" score-docs]
  (map (fn [score-doc] (.doc score-doc)) score-docs))

(defn doc-ids->docs [searcher doc-ids]
  (map (fn [doc-id] (.doc searcher doc-id)) doc-ids))

(defn search [directory field search-term analyzer]
  (let [q (query field search-term analyzer)
        searcher (searcher directory)
        search-results (.search searcher q 10)
        score-docs (.scoreDocs search-results)
        doc-ids (score-docs->ids score-docs)
        docs (doc-ids->docs searcher doc-ids)]
    (map doc->map docs)))
;; Solution:1 ends here
