(ns workshop-app.handlers.concurrency
  (:require [workshop-app.db.redis :as redis]
            [workshop-app.db.mongo :as mongo]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Concurrency
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; A way to get safe access to limited resources by multiple
;; actors/threads

;; There are multiple paradigms models to do concurrency

;; Locks are one way of doing it

;; Clojure does an amazing job where there is immutable data by means of
;; persistent data structures provided by Clojure core

;; But what about mutating things?

;; There are four ways,

;; vars
;; refs
;; agents
;; atoms

;; But first a brief introduction to running things in different threads


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Future
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;; This will run on a different thread
(comment
  (future 1)

  ;; This will block and wait till it's complete
  (deref (future 1))

  ;; A short hand for deref
  @(future 1)

  (def f (future (println "Running thread")
                 1))

  ;; Guess the output of second deref
  @f
  @f)



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Promise
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Different futures can pass on values with futures as well.

(comment
  (def p (promise))

  ;; This will block and execute
  (future (Thread/sleep 5000) (deliver p 1))

  @p)




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; vars
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(def x 1)

(comment
  (def a 1)
  (future (println a))
  (future (println a)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Atoms
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Use it to represent Shared state

;; But first
;; Problems with shared state and why it's messy

;; int b = 1
;; new Thread(() -> b = 3
;;            Thread.sleep (new Random().nextInt(1000))
;;            System.out.println(b)).start();
;; new Thread(() -> b = 4
;;            Thread.sleep (new Random().nextInt(1000))
;;            System.out.println(b)).start();

;; https://aphyr.com/posts/306-clojure-from-the-ground-up-state

;; A counterpart in Clojure


(comment
  (do
    (def b [])

    (doseq [n (range 2000)]
      (future (def b (conj b
                           n))))
    (println (count b))))


;; Let's slow things down to understand

(comment
  (do
    (def b [])

    (doseq [n (range 2000)]
      (future (def b (conj b
                           (do (when (= n 5)
                                 (println (count b))
                                 (Thread/sleep 100))
                               n)))))
    (Thread/sleep 100)
    (println (count b))))

;; We need transformation of state with stronger gaurantees


;; Variables mix State + Identitiy


;; A symbol in clojure is just an identity which points to a value / state

(def a 1)

;; It can point to an atom

(def a (atom 1))

;; Since atom's value can change we need to deref it to access it

(deref a)

;; A short hand to deref is @

@a

;; @TODO add example of swap! and reset!

;; Let's try this with an atom

(comment
  (do
    (def b (atom []))
    (doseq [n (range 2000)]
      (future (swap! b conj n)))
    (println (count @b))))

(comment
  (do
    (def b (atom []))

    (doseq [n (range 2000)]
      (future (swap! b
                     conj
                     (do (when (= n 5)
                           (println (count @b))
                           (Thread/sleep 100))
                         n))))
    (Thread/sleep 100)
    (println (count @b))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; refs
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Refs ensure safe mutation of multiple shared states using STM

;; STM to me looks something like this

;; https://vimeo.com/106226560#t=10s

;; dosync is macro to start transaction

;; ref-set sets it to a value

;; alter runs a function on the value

;; Values held by refs must be immutable preferably clojure persistent
;; structures

(def a-ref-num (ref 0))
(def b-ref-num (ref 0))

(comment
  (doseq [n (range 10)]
    (future
      (dosync
       (println "Transaction - " n)
       (ref-set a-ref-num n)
       (Thread/sleep (rand-int 20))
       (ref-set b-ref-num n)))))

[@a-ref-num @b-ref-num]


(do
  (def a-ref (ref 0))
  (def b-ref (ref 0))
  (def a-atom (atom 0))
  (def b-atom (atom 0)))

(comment
  (do
    (doseq [n (range 100)]
      (future
        (dosync
         (ref-set a-ref n)
         (Thread/sleep (rand-int 200))
         (ref-set b-ref n))

        (reset! a-atom n)
        (Thread/sleep (rand-int 200))
        (reset! b-atom n)))

    (doseq [n (range 5)]
      (Thread/sleep 1000)
      (println (format "Ref values A - %s,  B - %s\nAtom values A - %s,  B - %s\n"
                       @a-ref
                       @b-ref
                       @a-atom
                       @b-atom)))))


(def a-alter (ref []))
(def b-alter (ref []))

(comment
  (doseq [n (range 10)]
    (future
      (dosync
       (println "Transaction - " n)
       (alter a-alter conj n)
       (Thread/sleep (rand-int 20))
       (alter b-alter conj n)))))

[@a-alter @b-alter]



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Thundering herd
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



;; Thundering herd

;; When many readers simultaneously request the same data element, there
;; can be a database read overload,
;; sometimes called the “Thundering Herd” problem.
;; https://www.ehcache.org/documentation/2.8/recipes/thunderingherd.html


;; Read from cache if not present read from database and update cache

(defn read+update-1
  [k]
  (if-let [v (redis/fetch :foo)]
    [v :redis]
    (do
      (let [v (mongo/fetch k)]
        (redis/set k v)
        [v :mongo]))))

(comment

  (do
    (reset! redis/r-db {})

   (doall
    (frequencies (map second (map (fn [_]
                                    (read+update-1 :foo))
                                  (range 10))))))



  ;; For 1000 concurrent requests there is a possibility of requests
  ;; going to MongoDB

  (do
    (reset! redis/r-db {})

    (def t-h-1
      (doall
       (map (fn [_]
              (future (read+update-1 :foo)))
            (range 1000))))

    ;; To analyze this data first we need to get the values from futures

    (frequencies (map second (map #_FIXME t-h-1)))))



;;; Let's introduce atom!

(def ongoing-updates-a (#_FIXME #{}))

(defn read+update-2
  [k]
  (if-let [v (redis/fetch :foo)]
    [v :redis]
    (let [update-ongoing? (#_FIXME ongoing-updates-a)]
      (if-not (update-ongoing? k)
        (do
          (#_FIXME ongoing-updates-a conj k)
          (let [v (mongo/fetch k)]
            (redis/set k v)
            (#_FIXME ongoing-updates-a disj k)
            [v :mongo]))
        [1 :default]))))

(comment

  (do
    (reset! redis/r-db {})

    (def t-h-1
      (doall
       (map (fn [_]
              (future (read+update-2 :foo)))
            (range 10000))))


    (frequencies (map second (map deref t-h-1)))))


;; Let's use ref instead

(def ongoing-updates-ref (#_FIXME #{}))

(defn read+update-3
  [k]
  (if-let [v (redis/fetch :foo)]
    [v :redis]
    (let [update-ongoing? (#_FIXME (when-not (get @ongoing-updates-ref k)
                                     (#_FIXME ongoing-updates-ref conj k)))]
      (if update-ongoing?
        (let [v (mongo/fetch k)]
          (redis/set k v)
          (#_FIXME (#_FIXME ongoing-updates-ref disj k))
          [v :mongo])
        [1 :default]))))



(comment

  (do
    (reset! redis/r-db {})

    (def t-h-2
      (doall
       (map (fn [_]
              (future (read+update-3 :foo)))
            (range 10000))))


    (frequencies (map second (map deref t-h-2)))))



;; But how do we fix the nils?

(def ongoing-updates-ref-p (ref #_FIXME))


(defn read+update-4
  [k]
  (if-let [v (redis/fetch :foo)]
    [v :redis]
    (let [[action p] (dosync (if-let [p (get @ongoing-updates-ref-p k)]
                               [:wait (get @ongoing-updates-ref-p k)]
                               (let [p #_FIXME]
                                 (alter ongoing-updates-ref-p #_FIXME k p)
                                 [:update p])))]
      (case action
        :update (let [v (mongo/fetch k)]
                  (redis/set k v)
                  (dosync (alter ongoing-updates-ref-p #_FIXME k))
                  (#_FIXME p v)
                  [v :mongo])
        :wait [(#_FIXME p) :redis]))))


(defn read+update-4
  [k]
  (if-let [v (redis/fetch :foo)]
    [v :redis]
    (let [[action p] (dosync (if-let [p (get @ongoing-updates-ref-p k)]
                               [:wait (get @ongoing-updates-ref-p k)]
                               (let [p (promise)]
                                 (alter ongoing-updates-ref-p assoc k p)
                                 [:update p])))]
      (case action
        :update (let [v (mongo/fetch k)]
                  (redis/set k v)
                  (dosync (alter ongoing-updates-ref-p dissoc k))
                  (deliver p v)
                  [v :mongo])
        :wait [(deref p) :redis]))))


(comment

  (do
    (reset! redis/r-db {})

    (def t-h-3
      (doall
       (map (fn [_]
              (future (read+update-4 :foo)))
            (range 10000))))


    (frequencies (map second (map deref t-h-3)))))
