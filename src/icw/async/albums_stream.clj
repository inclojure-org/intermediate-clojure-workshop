;; [[file:~/github/intermediate-clojure-workshop/content/async/albums_stream.org::*Preamble][Preamble:1]]
(ns icw.async.albums-stream
  (:require [clojure.core.async :as a
             :refer [chan go go-loop <! >! take! put!]]
            [icw.async.rlsc :as rlsc]
            [icw.data.gen :as data-gen]))
;; Preamble:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/async/albums_stream.org::*Outline][Outline:2]]
(defonce counter (atom 0))
(def observing-mapper (map (fn [e]
                             (swap! counter inc)
                             e)))

(def in-ch (a/chan (a/dropping-buffer 32) observing-mapper))

(defonce enabled? (atom false))
(defonce quit? (atom false))

(defonce generator-loop
  (go-loop [stream (data-gen/get-albums-xs)]
    #_("Introduce an appropriate delay")
    (if-not @quit?
      (do
        (if @enabled?
          (a/>! in-ch #_(FIXME stream) :dummy))
        (recur #_(FIXME stream) :dummy)))))

(defn enable-stream! []
  (reset! enabled? true))
(defn disable-stream! []
  (reset! enabled? false))
;; Outline:2 ends here
