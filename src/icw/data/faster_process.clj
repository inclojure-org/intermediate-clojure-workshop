(ns icw.data.faster-process
  (:require [icw.java-interop.jdbc :as jdbc]
            [icw.data.process :as idp]))


;; Deref and how it's useful

;; Future

;; This will run on a different thread
(future 1)

;; This will block and wait till it's complete
(deref (future 1))

;; A short hand for deref
@(future 1)

;; What's the point of deref then? Is it to just make threds block?

;; Promise

;; Deliver works only once on a promise
;; Deref works on promise as well
(let [p1 (promise)]
  (future (Thread/sleep (rand-int 100))
          (deliver p1 "Thread 1"))

  (future (Thread/sleep (rand-int 100))
          (deliver p1 "Thread 2"))

  @p1)

;; Promises are good to collect the first result from multiple threads/sources

;; Delay

;; A very subtle difference than future. The body runs only after it's
;; deref and it only runs ons

;; How many times "hello" will be printed?
(let [a (delay (println "hello")
               1)]
  @a
  @a)


(defn populate-db
  []
  (jdbc/init-db)

  (with-open [rdr (clojure.java.io/reader "resources/data/albumlist.csv")]
    (let [lines (line-seq rdr)
          albums (idp/source->album-xs idp/album-source)]
      (doseq [album albums]
        (jdbc/insert! album)))))
