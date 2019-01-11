;; [[file:~/github/intermediate-clojure-workshop/content/async/rlsc.org::*The%20Template][The Template:1]]
(ns icw.async.rlsc
  (:require [clojure.core.async :as a
             :refer [go go-loop
                     chan close! timeout thread
                     <! >! take! put! >!! <!!
                     buffer dropping-buffer sliding-buffer]]
            [icw.common :refer :all]))

; A bounded counter that helps to track and cap consumption
(defprotocol IBoundedCounter
  (update-bound! [this new-ulimit])
  (counter++ [this])
  (counter-- [this])
  (counter [this]))

; The state-holding datastructure for supporting our behavior policy
(defrecord BoundedCounter [ulimit current]

  IBoundedCounter
  (update-bound! [_ new-ulimit]
    (reset! ulimit (max 0 new-ulimit)))
  (counter++ [_]
    (reset! current (min @ulimit (inc @current))))
  (counter-- [_]
    (reset! current (max 0
                         (min @ulimit (dec @current)))))
  (counter [_] @current))

(defn new-counter [ulimit & [initial]]
  (->BoundedCounter (atom ulimit) (atom (or initial 0))))

(comment
  ; Let's test our bounded counter.
  ; Increment-tests first. Try modifying the limits
  (let [c (new-counter 10 5)]
    (doseq [_ (range 7)]
      (println "Current count: " (counter c))
      (println "++ " (counter++ c))))

  ; Decrement tests
  (let [c (new-counter 10 13)]
    (doseq [_ (range 7)]
      (println "Current count: " (counter c))
      (println "-- " (counter-- c)))))
;; The Template:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/async/rlsc.org::*The%20API][The API:1]]
(defprotocol RLSCController
  (start! [this])
  (modify-burst! [this new-burst-count])
  (zero! [this])
  (shutdown! [this]))
;; The API:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/async/rlsc.org::*Outline][Outline:1]]
(defrecord RLSC
  [in-ch out-ch process-fn time-gap-ms burst-count]
  ;; Any internal state to track??

  RLSCController
  ; We need two go processes
  ; 1. One that tracks consumption and burst-rate limits
  ; 2. The other processes messages per the policy
  (start! [_]
    (go-loop []
      #_("A periodic process that increments tokens"))
    (go-loop [#_v #_("read a message")]
      "If we have capacity process, else simply pass on to the output channel"
      (recur #_("read next message if no shutdown signal"))))

  ; Policy change at run-time
  ; This needs to be conveyed to the go-blocks
  ;  which help us conform to policy
  (modify-burst! [this new-burst-count]
    #_("update the burst-count")
    #_("update tokens available"))

  ; Stop all transformation
  ; Signal the go-block logic to clamp down on transformations.
  (zero! [this]
    #_("special case of modify-burst! Is it?"))

  ; Stop the go blocks.
  ; How do we communicate with the go-blocks started in another place?
  (shutdown! [this]))

(defn new-rate-limiter [in-ch out-ch process-fn time-gap-ms burst-count]
  (->RLSC in-ch out-ch process-fn time-gap-ms
          (atom burst-count)))
;; Outline:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/async/rlsc.org::*Test%20runs][Test runs:1]]
(comment
  (let [count (atom 0)]
    (defn work-counter [] @count)
    (defn work-counter++ [] (swap! count inc)))

  (def in-ch (chan (dropping-buffer 8)))

  (def out-ch (chan (dropping-buffer 8)
                    (map pprint)
                    (constantly nil)))

  (defn process-value [v]
    (assoc v :heavy? true))
  (def r (new-rate-limiter in-ch out-ch
                           (fn [v]
                             (assoc v :heavy? true))
                           1000 3))
  (start! r)

  (zero! r)
  (doseq [_ (range 5)]
    (put! in-ch {:payload (work-counter++)})))
;; Test runs:1 ends here
