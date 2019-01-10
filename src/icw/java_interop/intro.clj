;; [[file:~/github/intermediate-clojure-workshop/content/java-interop/intro.org::*Preamble][Preamble:1]]
(ns icw.java-interop.intro
  (:require [clojure.datafy :refer [datafy]]))
;; Preamble:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/java-interop/intro.org::*Simple%20method%20calls][Simple method calls:1]]
(do
  ; Clojure strings are naturally Java String instances
  (def sample-string "Hello, IN/Clojure!")

  (.length sample-string)
  ; OR
  (. sample-string length)

  (.toUpperCase sample-string)
  ; OR
  (. sample-string toUpperCase)

  (ns-unmap *ns* 'sample-string)
  )
;; Simple method calls:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/java-interop/intro.org::*Constructing,%20and%20property%20access][Constructing, and property access:1]]
(do

  ;; Constructor - note the dot in Point.
  (def sample-point (java.awt.Point. 10 20))
  ; OR
  (def sample-point (new java.awt.Point 10 20))

  ;; Member access
  (.-x sample-point)
  ; OR
  (. sample-point -x)

  (.-y sample-point)
  ; OR
  (. sample-point -y)

  ; Assignment
  (set! (. sample-point y) 50)

  ;; Multiple, probably side-effecting, serial actions
  (doto sample-point
    (-> .-x println)
    (-> .-y println)) ; The doto expression evaluates to the
                      ; supplied object. It may have mutated along the way within the doto.

  ; Bean there, done that?
  (:x (bean sample-point))

  (ns-unmap *ns* 'sample-point)
  )
;; Constructing, and property access:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/java-interop/intro.org::*Static%20access][Static access:1]]
(do

  ;; Static methods
  (System/getProperty "java.vm.version")

  ;; Static values
  Math/PI

  ; Threading-equivalent
  (.. System getProperties (get "os.name")))
;; Static access:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/java-interop/intro.org::*Building%20fluidly][Building fluidly:1]]
(do
  (doto (java.util.HashMap.)
    (.put :a :A)
    (.put :b :B))

  (doto (java.util.HashSet.)
    (.add :a)
    (.add :b)
    (.add :a))

  (doto (java.util.ArrayList.)
    (.add 1)
    (.add 2)))
;; Building fluidly:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/java-interop/intro.org::*Objects][Objects:1]]
(clojure.pprint/pprint (do (datafy "hello world")))
;; Objects:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/java-interop/intro.org::*Objects][Objects:2]]
(comment (clojure.pprint/pprint (do (datafy sample-point))))
;; Objects:2 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/java-interop/intro.org::*Objects][Objects:3]]
(comment (clojure.pprint/pprint (do (bean sample-point))))
;; Objects:3 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/java-interop/intro.org::*Objects][Objects:4]]
(clojure.pprint/pprint (do (bean {:a :A :b :B})))
;; Objects:4 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/java-interop/intro.org::*Class][Class:1]]
(clojure.pprint/pprint (do (datafy java.awt.Point)))
;; Class:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/java-interop/intro.org::*Class][Class:2]]
(clojure.pprint/pprint (do (datafy java.util.Collections)))
;; Class:2 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/java-interop/intro.org::*Namespaces][Namespaces:1]]
(clojure.pprint/pprint (do (datafy *ns*)))
;; Namespaces:1 ends here
