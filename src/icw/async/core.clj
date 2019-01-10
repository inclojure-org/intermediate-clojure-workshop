;; [[file:~/github/intermediate-clojure-workshop/content/async/core.org::*Preamble%20Code][Preamble Code:1]]
(ns icw.async.core
  (:require [clojure.core.async :as a
             :refer [chan go go-loop <! >! take! put!]]
            [icw.async.rlsc :as rlsc]
            [icw.data.gen :as data-gen]))

(defonce counter (atom 0))
(def observing-mapper (map (fn [e]
                             (swap! counter inc)
                             e)))

(def in-ch (a/chan (a/dropping-buffer 32) observing-mapper))

(defonce enabled? (atom false))
(defonce quit? (atom false))

(defn enable-stream! []
  (reset! enabled? true))
(defn disable-stream! []
  (reset! enabled? false))

(defonce generator-loop
  (go-loop [stream (data-gen/get-albums-xs)]
    ; FIXME
    ; (a/<! (a/timeout 250))
    (if-not @quit?
      (do
        (if @enabled?
          (a/>! in-ch (first stream)))
        (recur (rest stream))))))
;; Preamble Code:1 ends here
