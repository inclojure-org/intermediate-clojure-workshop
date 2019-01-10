;; [[file:~/github/intermediate-clojure-workshop/content/common.org::*Common%20utils][Common utils:1]]
(ns icw.common
  (:require [clojure.pprint :as pp]
            [clojure.datafy :refer [datafy]]))

(defn thread-name []
  (-> (Thread/currentThread)
      .getName))

(let [pp-lock (Object.)]
  (defn pprint
    [& s]
    (locking pp-lock
      (pp/pprint (apply str "[" (thread-name) "] " s)))))
;; Common utils:1 ends here
