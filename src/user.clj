(ns user
    (:require [clojure.pprint :as pp]
              [clojure.datafy :refer [datafy]]
              [integrant.core :as ig]
              [integrant.repl :refer [clear go halt prep init reset reset-all]]
              [icw.system :refer [start! stop!]]))

(comment
  (start!))
