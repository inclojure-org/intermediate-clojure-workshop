;; [[file:~/github/intermediate-clojure-workshop/content/java-interop/types.org::*The%20Preamble][The Preamble:1]]
(ns icw.java-interop.types
  (:require [icw.common :refer :all]
            [clojure.datafy :refer [datafy]])
  (:import [java.util ArrayList Collections]
           [java.util.concurrent Callable ForkJoinPool]))
;; The Preamble:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/java-interop/types.org::*A%20core%20function][A core function:1]]
(-> + class ancestors)
;; A core function:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/java-interop/types.org::*Map][Map:1]]
(-> {:a :A :b :B} class ancestors)
;; Map:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/java-interop/types.org::*Set][Set:1]]
(-> #{:a :A :b :B} class ancestors)
;; Set:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/java-interop/types.org::*Vector][Vector:1]]
(-> [1 2 3 4 5] class ancestors)
;; Vector:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/java-interop/types.org::*List][List:1]]
(-> (list 1 2 3 4 5) class ancestors)
;; List:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/java-interop/types.org::*Record][Record:1]]
(defrecord FooBarBaz [foo bar baz])
(-> FooBarBaz ancestors)
;; Record:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/java-interop/types.org::*Comparable][Comparable:1]]
(let [a (ArrayList. [2 1 2 3 4 10 11 15 9])]
  (Collections/sort a <)
  ;; Hello, mutable-land!
  a)
;; Comparable:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/java-interop/types.org::*Search][Search:1]]
; You can directly use Clojure vectors with java.util.Collections
(Collections/binarySearch [1 2 3 4 5] 3)
;; Search:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/java-interop/types.org::*General%20ops][General ops:1]]
; Collections works well with vectors and lists
(Collections/min [1 2 3 4 5])
(Collections/max '(1 2 3 4 5))
(Collections/max [1 2 3 4 5])
(Collections/reverse [1 2 3 4 5])
;; General ops:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/java-interop/types.org::*General%20ops][General ops:2]]
;; And, does it work with sets?
(Collections/min #{3 4 1 7 10 Integer/MIN_VALUE})
;; General ops:2 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/java-interop/types.org::*General%20ops][General ops:3]]
;; What about maps?
(Collections/min {1 :one 2 :two 3 :three})
;; General ops:3 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/java-interop/types.org::*Threads][Threads:1]]
; "Functions are first-class" should mean something, no?
(let [f (fn [] (println "Hello, from a runnable function."))
      t (Thread. f)]
  ; Yes, IFn is Runnable!
  (.run t))
;; Threads:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/java-interop/types.org::*Executors][Executors:1]]
; Runnable is a sad little thing. We want something happy!
(let [f (fn [] :woo-hoo!)
      p (ForkJoinPool/commonPool)
      ; Yes, IFn is Callable!
      res (.submit p ^Callable f)]
  ; Which means, we should be "get"ting results of our execution.
  (.get res))

;; And the result is a... happy one! :woo-hoo!
;; Executors:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/java-interop/types.org::*Records%20and%20Interfaces][Records and Interfaces:1]]
(clojure.pprint/pprint (do (do
  "Yes, in Java, *everything* is an Object. So are our records"
  (defrecord FooRecord [foo bar]
    ; Which means, we make our toString a pretty one.
    Object
    (toString [_] (str "I am a holder of foo and bar. They are: " foo " and " bar)))

  ; Who are FooRecord's ancestors?
  (ancestors FooRecord)

  ; Let's invoke our over-ridden method above...
  (str (->FooRecord "foo" "bar"))
  ; OR
  (.toString (->FooRecord "foo" "bar")))))
;; Records and Interfaces:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/java-interop/types.org::*Records%20and%20Interfaces][Records and Interfaces:2]]
"I am a holder of foo and bar. They are: foo and bar"
;; Records and Interfaces:2 ends here
