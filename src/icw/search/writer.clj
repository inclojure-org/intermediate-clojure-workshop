;; [[file:~/github/intermediate-clojure-workshop/content/search/writer.org::*The%20Preamble][The Preamble:1]]
(ns icw.search.writer
  (:require [icw.search.common :refer :all])
  (:import [org.apache.lucene.store Directory ByteBuffersDirectory]
           [org.apache.lucene.analysis.standard StandardAnalyzer]
           [org.apache.lucene.index IndexWriter IndexWriterConfig IndexOptions]
           [org.apache.lucene.document Field FieldType Document]))

(defn index-writer-config [analyzer]
  (IndexWriterConfig. analyzer))

(defn index-writer [directory index-writer-config]
  (IndexWriter. directory index-writer-config))

(defn directory [] (ByteBuffersDirectory.))

(defn field-type [{:keys [tokenize?]}]
  (doto (FieldType.)
    (.setIndexOptions IndexOptions/DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS)
    (.setStored true)
    (.setTokenized tokenize?)))

(defn field [k v opts]
  (let [ft (field-type opts)
        v (str v)]
    (Field. (name k) v ft)))

(defn map->doc [m {:keys [tokenized-fields]}]
  (let [ks (keys m)
        d (Document.)]
    (doseq [k ks]
      (.add d (field k
                     (get m k)
                     {:tokenize? (contains? tokenized-fields k)})))
    d))

(defn index! [index-writer doc-maps opts]
  (println (str "Indexing " (count doc-maps) " documents."))
  (doseq [doc-map doc-maps]
    (.addDocument index-writer (map->doc doc-map opts))))

(comment
  (def albums (:data/db.albums @icw.system/system))
  (first albums)
  (def d (directory))
  (def analyzer (standard-analyzer))
  (def iwc (index-writer-config analyzer))
  (def iw (index-writer d iwc))
  (def opts {:tokenized-fields #{:album :artist :genre :subgenre}})
  (index! iw albums opts)
  (.close iw)
  (map icw.search.reader/doc->map (icw.search.reader/search d :album "revolver" analyzer))
  )
;; The Preamble:1 ends here
