(ns icw.data.faster-process
  (:require [icw.java-interop.jdbc :as jdbc]
            [icw.data.process :as idp]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Future
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;; This will run on a different thread
(future 1)

;; This will block and wait till it's complete
(deref (future 1))

;; A short hand for deref
@(future 1)

;; What's the point of deref then? Is it to just make threds block?

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Promise
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Deliver works only once on a promise
;; Deref works on promise as well
(let [p1 (promise)]
  (future (Thread/sleep (rand-int 100))
          (deliver p1 "Thread 1"))

  (future (Thread/sleep (rand-int 100))
          (deliver p1 "Thread 2"))

  @p1)

;; Promises are good to collect the first result from multiple threads/sources


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Delay
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; A very subtle difference than future. The body runs only after it's
;; deref and it only runs ons

;; How many times "hello" will be printed?
(let [a (delay (println "hello")
               1)]
  @a
  @a)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Atoms
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(def state (atom {}))

;; You can change the information of an atom using swap! or reset!

(swap! state assoc :a 1)

;; Derefing the state will give you the current value
@state


;; @TODO add exercises for using future
;; @TODO add exercises that use delay
;; @TODO add exercises that use promise

;; @TODO add exercise that uses agent

;; @TODO add exercises that use future, delay and promise

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Rock scissor paper
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def BEATS {:rock :scissors, :paper :rock, :scissors :paper})

(defn winner
  [[name-1 move-1] [name-2 move-2]]
  (cond
   (= move-1 move-2) "no one"
   (= move-2 (BEATS move-1)) name-1
   :else name-2))

(defn judge
  [p1 p2]
  (future [@p1 @p2 (winner @p1 @p2)]))

(defn populate-db
  []
  (jdbc/init-db)

  (with-open [rdr (clojure.java.io/reader "resources/data/albumlist.csv")]
    (let [lines (line-seq rdr)
          albums (idp/source->album-xs idp/album-source)]
      (doseq [album albums]
        (jdbc/insert! album)))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Caveats
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
