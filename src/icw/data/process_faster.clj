(ns icw.data.process-faster
  (:require [icw.java-interop.jdbc :as jdbc]
            [clojure.string :as cs]
            [icw.data.process :as idp]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Concurrency
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; A way to get safe access to limited resources by multiple
;; actors/threads

;; There are multiple paradigms models to do concurrency

;; Locks are one way of doing it

;; Most accurate Visualization of using locks
;; https://twitter.com/valarauca1/status/921542529962557440


;; Clojure does an amazing job where there is immutable data by means of
;; persistant data structures provided by Clojure core

;; But what about mutating things?

;; There are four ways,

;; vars
;; refs
;; agents
;; atoms


;; But first a brief introduction to running things in different threads

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Future
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;; This will run on a different thread
(future 1)

;; This will block and wait till it's complete
(deref (future 1))

;; A short hand for deref
@(future 1)

(def f (future (println "hello")
               1))

;; Guess the output of second deref
@f
@f

;; What's the point of deref then?

;; Deref tries to get current value it
;; works on multiple things alongwith future


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; vars
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def x)

(def x 1)

;; Usually vars are static

;; But they can be dynamic and have different value per thread

(def ^:dynamic x  1)

x

(comment (binding [x 10]
           (println x)
           (future (println x))))

;; There are several reasons why dynamic variables are a bad idea
;; More on that later


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; refs
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Refs ensure safe mutation of shared state using STM

;; STM to me looks something like this

;; https://vimeo.com/106226560#t=10s

;; dosync is macro to start transaction

;; ref-set sets it to a value

;; alter runs a function on the value

;; Values held by refs must be immutable preferably clojure persistent
;; structures

(def a-set (ref []))
(def b-set (ref []))

(comment
  (doseq [n (range 10)]
    (future (dosync
             (println "Transaction - " n)
             (ref-set a-set n)
             (Thread/sleep (rand-int 20))
             (ref-set b-set n)))))

;; Why are there so many prints for just one run of 10 threads?
[@a-set @b-set]


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

;; There is commute as well but we will not into details

;; Exercise

;; We want to keep record of last five database insertions that failed

(comment
  (let [failures-ref (ref [])
        failure-num (ref 0)]
    (doseq [n (range 10)]
      (future (let [failure? (> (rand-int 100)
                                30)]
                (when failure?
                  (dosync
                   ;; Just to add randomness
                   (Thread/sleep (rand-int 20))

                   (#_FIME failures-ref
                           #_FIXME
                           {:thread-id (str "Thread-" n)
                            :failure-num @failure-num})

                   (#_FIME failure-num #_FIXME)
                   (when (> (count @failures-ref) 5)
                     (#_FIXME failures-ref
                              (into []
                                    (drop (- (count @failures-ref)
                                             5)
                                          @failures-ref)))))))))
    (Thread/sleep 1000)
    (println @failures-ref)
    (not= @failures-ref [])))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; agent
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def agent-a (agent 1))

(send agent-a inc)
@agent-a

(comment
  (send agent-a (fn [n]
                  (/ n 0)))

  (agent-error agent-a)
  (restart-agent agent-a 1)

  @agent-a
  (send agent-a inc))

;; Agents are part of STM and send is parked work unless transaction is
;; complete

(do (future (dosync (send agent-a inc)
                    (Thread/sleep 400)))
    (println "1. Printing value of agent - " @agent-a)
    (Thread/sleep 400)
    (println "2. Printing value of agent - " @agent-a))

;; Agents are a great way to do async work on a value
;; Another good use case is to convert an thread unsafe API into a
;; thread safe one

(defn thread-unsafe-api
  [a x]
  (println "Thread unsafe API - " x)
  x)

(def api-agent (agent "Message"))

(send api-agent thread-unsafe-api "New message")


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Atoms
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Atoms are also meant to hold immutable values. Preferably Clojure's
;; persistent data structures

(def state (atom {}))

;; You can change the information of an atom using swap! or reset!

(swap! state assoc :a 1)

;; You can set a value using reset!

(reset! state {:a 2})

;; Derefing the state will give you the current value
@state

;; Values changed in swap! are atomic if there is a conflict the swap!
;; is retried

;; Atoms are great way of doing sychrnous changes to shared state

;; Atoms are used as a cache as well sometimes but be aware!
;; Atoms do not provide a way to limit memory usage. It's up to the user


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Promise
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; There are some more constructs which help co-ordiate between threads

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

;; Compared to delay check when 'hello' is printed
(let [a (future (println "hello")
                1)]
  @a
  @a)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Rock paper scissors
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def BEATS {:rock :scissors, :paper :rock, :scissors :paper})

(defn judge
  [[name-1 move-1] [name-2 move-2]]
  (cond
   (= move-1 move-2) ::no-one
   (= move-2 (BEATS move-1)) name-1
   :else name-2))

(defn run-a-turn
  [game-room]
  (let [[player-1 player-2] (keys (:players game-room))
        winner (judge [player-1 (rand-nth (keys BEATS))]
                      [player-2 (rand-nth (keys BEATS))])]
    (if-not (= ::no-one winner)
      (update-in game-room [:players winner] inc)
      game-room)))


;;; Using agent

(defn create-a-room
  [name-1 name-2 room-name]
  (agent {:players {name-1 0
                    name-2 0}
          :name room-name}))

(defn run-game
  [room-a]
  (future (loop []
            (Thread/sleep 10)
            ;; room-a is an agent we want to run a turn after every 10
            ;; msecs
            (#_FIXME room-a #_FIXME)
            (recur))))


(defn run-multiple-games
  "Run multiple games of rock-paper-scissors
   On halt stop running the games and declare winners"
  [n]
  (let [game-rooms (map #(create-a-room "player-1"
                                        "player-2"
                                        (str "room-" %))
                        (range n))
        running-games (doall (map (fn [room]
                                    (run-game room))
                                  game-rooms))]


    (Thread/sleep 5000)

    (doseq [game running-games]
      (future-cancel game))

    (map (fn [a]
           [(:name @a)
            (-> (sort-by (comp - second)
                         (:players @a))
                first
                first)])
         game-rooms)))


;; Take home task modify run-multiple-games to count number of turns
;; that happened in each game


;; Coming back to populate-db


(defn populate-db
  []
  (jdbc/init-db)

  (with-open [rdr (clojure.java.io/reader "resources/data/albumlist.csv")]
    (let [lines (line-seq rdr)
          albums (idp/line-xs->album-xs idp/album-lines)]
      (doseq [album albums]
        ;; Running it in another thread will help
        (jdbc/insert! (update-in album
                                 [:subgenre]
                                 #(cs/join "," %)))))))


(comment (time (populate-db)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Take home exercise
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Make sure all the `jdbc/insert!` succeed


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Caveats
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Limited threadpool for agent and future

;; shutdown-agents needs to be called while exiting a service
