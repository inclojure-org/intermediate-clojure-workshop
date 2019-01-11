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

;; [[file:~/github/intermediate-clojure-workshop/content/async/rlsc.org::*Solution][Solution:1]]
(defrecord RLSC [in-ch out-ch process-fn time-gap-ms burst-count
                 -tokens -shutdown?]
  RLSCController
  (start! [_]

    ; An independent go process that tracks appropriate increments to the
    ; burst-count
    (go-loop []
      (<! (timeout time-gap-ms))
      (counter++ -tokens)
      (if-not @-shutdown?
        (recur)))

    (go-loop [v (<! in-ch)]
      (if (pos? (counter -tokens))
        (do
          "We have capacity. So, process the message on another thread so we do not block
          any thread in the core.async pool, and decrement available capacity by one."
          (thread (>!! out-ch (process-fn v)))
          (counter-- -tokens))
        (do
          "No spare capacity. Short-circuit the inward message to the next sink."
          (>! out-ch v)))
      (if-not @-shutdown?
        (recur (<! in-ch)))))

  (modify-burst! [_ new-burst-count]
    (reset! burst-count new-burst-count)
    (update-bound! -tokens new-burst-count))

  ; Special case of modify-burst!
  (zero! [this]
    (modify-burst! this 0))

  ; Reset our check variable and hope for the best.
  (shutdown! [_]
    (reset! -shutdown? true)))

(defn new-rate-limiter [in-ch out-ch process-fn time-gap-ms burst-count]
  (->RLSC in-ch out-ch process-fn time-gap-ms
          (atom burst-count)
          (new-counter burst-count)
          (atom false)))
;; Solution:1 ends here

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
