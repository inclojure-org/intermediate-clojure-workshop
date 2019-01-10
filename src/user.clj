;; [[file:~/github/intermediate-clojure-workshop/content/user.org::*User%20namespace%20-%20defaults][User namespace - defaults:1]]
(ns user
    (:require [clojure.pprint :as pp]
              [clojure.datafy :refer [datafy]]
              [integrant.core :as ig]
              [integrant.repl :refer [clear go halt prep init reset reset-all]]
              [icw.system :refer [start! stop!]]))

(comment
  (start!))
;; User namespace - defaults:1 ends here
