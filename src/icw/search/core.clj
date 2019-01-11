;; [[file:~/github/intermediate-clojure-workshop/content/search/core.org::*The%20Preamble][The Preamble:1]]
(ns icw.search.core
  (:require [icw.search
             [common :refer :all]
             [writer :as w]
             [reader :as r]]))

(defonce index (atom nil))

(defn reset-index! []
  (when @index
    (.close @index)
    (reset! index nil)))

(defn init! [docs tokenized-fields & [re-init?]]

  (when (or re-init? false)
    (reset-index!))

  (when (nil? @index)
    (let [d (w/directory)
          analyzer (standard-analyzer)
          iwc (w/index-writer-config analyzer)
          iw (w/index-writer d iwc)]
      (w/index! iw docs {:tokenized-fields tokenized-fields})
      (.close iw)
      (reset! index d))))

(defn search [field query-term]
  (r/search @index field query-term (standard-analyzer)))

(comment
  (search :album "pepper's"))
;; The Preamble:1 ends here
