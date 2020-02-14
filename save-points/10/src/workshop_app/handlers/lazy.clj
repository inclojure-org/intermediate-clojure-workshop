(ns workshop-app.handlers.lazy)


;; 1 - Infinity

;; Simple example of range

(comment
  (range 10))

;; What happens when range is unbounded

;; DON'T EVALUATE THIS
(comment
  (range))


(comment
  (take 10 (range))

  (take 10 (map inc (range)))

  (take 10 (filter even? (map inc (range)))))


;; 2 - Maths

;; Let's calculate square roots by Newton-Raphson Square Roots method

;; a - approximation
;; n - Number
;; a = (a + n/a)/2
;; (/ (+ a (/ n a)) 2)


;; (f a0) = a1
;; (f a1) = a2
;; (f a2) = a3
;; (f a4) = a4

;; The algorithm is recursive
;; ((f (f (f a0) a1) a2)..)

;; But it can also be thought of a sequence
;; [a0, f a0, f (f a0), f (f (f a0)), . . . ]

;; We haven't talked about epsilon yet.

;; But if we look at it as lazy sequence

(def epsilon 1)

(defn sq-root*
  [a x]
  (let [a1 (/ (+ a (/ x a)) 2)]
    (cons [a a1]
          (lazy-seq (sq-root* a1
                             x)))))

(defn sq-root
  [x]
  (let [a 1]
    #_(??? (fn [[a1 a2]]
             (when (> epsilon (Math/abs (float (- a1 a2))))
               (int a1)))
           (sq-root* a x))))


;; Working with lazy sequences you basically deal with three things

;; - Generation
;; sq-root* generates a sequence (infinite) of approximations

;; - Processing
;; You may choose to process further on these sequences

;; - Realisation
;; You realise the result at this point defining how much
;; input is required is also necessary



;; 3 - Real world - Files

;; Size of the data does not matter as long as you are reducing it into
;; a small enough set
(with-open [r (clojure.java.io/reader "resources/data/albumlist.csv")]
  (let [l-xs (line-seq r)]
    (comment (count (filter #(= (:artist %)
                                "The Beatles")
                            (map parse-line l-xs))))))


;; 3 - Real world - Databases

;; You can create lazy sequences of IO operations as long as there is no
;; side effect and you are ok with lazy sequences caching the result

(defn- scroll-seq
  "Given a scroll-id, fetch scroll results"
  [num]
  (comment
    (lazy-seq
     (let [result (:body (http/get (format "_search/scroll?scroll=%s"
                                           num)))]
       (when (seq result)
         (cons result
               (scroll-seq (inc num))))))))

;; 4 - Gotchas

;; a. Open connections closing early


;; Always use with-open when generating lazy seq from external sources and realise the whole sequence inside with-open

(let [l-xs (with-open [r (clojure.java.io/reader "resources/data/albumlist.csv")]
             (line-seq r))]
  (count l-xs))

;; Creating lazy sequences from a database can bring complexities if
;; connections are not handled properly


;; b. Chunking - Well almost lazy

(comment (count (take 10 (map println (range 100)))))


;; c. Caching

(comment (def lz (doall (take 10 (map println (range 100)))))
         (count lz))

;; Some laziness in the wild

;; https://github.com/Factual/durable-queue/blob/master/src/durable_queue.clj#L741

;; https://github.com/s312569/clj-biosequence

;; https://github.com/Genscape/gregor/blob/master/src/gregor/core.clj#L241
