;; [[file:~/github/intermediate-clojure-workshop/content/data.org::*All%20Data][All Data:1]]
(ns icw.data
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as string]))

(defonce album-csv-file "data/albumlist.csv")

(defn load-album-csv-file [album-csv-file]
  (let [data (-> album-csv-file
                 io/resource
                 io/as-file
                 slurp)
        csv (csv/read-csv data)
        header (first csv)
        header (map string/lower-case header)
        data (rest csv)
        processed (map zipmap
                       (->> header
                            (map keyword)
                            repeat)
                       data)]
    processed))
;; All Data:1 ends here
