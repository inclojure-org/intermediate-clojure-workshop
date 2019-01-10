;; [[file:~/github/intermediate-clojure-workshop/content/search/common.org::*Common%20Search%20Routines][Common Search Routines:1]]
(ns icw.search.common
  (:import [org.apache.lucene.store Directory]
           [org.apache.lucene.analysis.standard StandardAnalyzer]))

(defn standard-analyzer []
  (StandardAnalyzer.))
;; Common Search Routines:1 ends here
