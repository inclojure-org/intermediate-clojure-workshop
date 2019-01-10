;; [[file:~/github/intermediate-clojure-workshop/content/async/intro.org::*Preamble][Preamble:1]]
(ns icw.async.intro
  (:require [icw.common :refer :all]
            [clojure.core.async :as a
             :refer [go go-loop chan close!
                     <! >! <!! >!! take! put!
                     alts! alt! thread
                     buffer sliding-buffer dropping-buffer]]))
;; Preamble:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/async/intro.org::*Put,%20and%20then%20take.][Put, and then take.:1]]
; A simple put, followed by a take
(let [ch (chan)]
  (put! ch "hello, orderly world!")
  (take! ch pprint))
;; Put, and then take.:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/async/intro.org::*Take,%20and%20put?][Take, and put?:1]]
(let [ch (chan)]

  ; What happens when we take from an empty channel?
  (take! ch pprint)

  (pprint "Now, we put!")
  ; Now we put!, followed by a take!
  (put! ch "hello, world!")

  ; Retry this let-block by commenting the first take!
  (take! ch pprint))
;; Take, and put?:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/async/intro.org::*Pushing%20the%20limits%20-%20put%20and%20take][Pushing the limits - put and take:1]]
(let [num-msgs 1025
      ch (chan)]
  (doseq [i (range num-msgs)]
    (put! ch i))

  (doseq [i (range num-msgs)]
    (take! ch pprint)))
;; Pushing the limits - put and take:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/async/intro.org::*Add%20a%20cushion][Add a cushion:1]]
(let [chan-buf-sz 32
      put-limit   1024
      take-limit  64
      ;; Try using any of buffer, dropping-buffer and sliding-buffer
      ch          (chan (buffer chan-buf-sz))]

  (doseq [i (range put-limit)]
    (put! ch i))

  (doseq [i (range take-limit)]
    (take! ch pprint)))
;; Add a cushion:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/async/intro.org::*Let's%20go!][Let's go!:1]]
(let [ch        (chan 32)
      lck       (Object.)
      pprint    (fn [message] (pprint (str "[" (.getName (Thread/currentThread)) "] " message)))
      my-pprint (fn [msg] (locking lck (pprint msg)))]
  ;my-pprint pprint

  (go
    (my-pprint (<! ch))
    (my-pprint "Done handling the important stuff. Now, I rest."))

  (my-pprint "We need a quick nap. Sleeping...")
  (Thread/sleep 2500)
  (go (>! ch "[Message] Did this message make you wait?"))

  (my-pprint "Now, we are done. Bye!"))
;; Let's go!:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/async/intro.org::*Going,%20going,%20go-loop!][Going, going, go-loop!:1]]
; Let's pollute the namespace, since we need a handle to work with.
(def looping-ch
  (let [ctl-ch (chan)]                                      ; The channel we deal with outside of this block.
    (go-loop [msg (<! ctl-ch)]
      (condp = msg
        :quit (do
                (pprint "I quit!")
                (close! ctl-ch))
        (do (pprint "We have a new message.")
            (pprint msg)
            (pprint "Onto the next one! Until then, I sleep...")
            (recur (<! ctl-ch)))))
    ctl-ch))

; Evaluate the next one as many times as you wish.
(put! looping-ch "hello")

; Evaluate the following, and observe. Then put! more messages and observe.
(put! looping-ch :quit)

; If you dislike the pollution, evaluate the line below
#_(ns-unmap *ns* 'looping-ch)
;; Going, going, go-loop!:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/async/intro.org::*What's%20async%20without%20multiple%20actors?][What's async without multiple actors?:1]]
(let [ch1       (chan)
      ch2       (chan)
      out-ch    (chan 1)
      priority? true]                                       ;; Try variations
  (go (let [[val port] (alts! [ch1 ch2 [out-ch "Out!"]] :priority priority?)]
        (condp = port
          ch1 (pprint "We had an input '" val "' on ch1")
          ch2 (pprint "We had an input '" val "' on ch2")
          out-ch (pprint "Nothing came in. So, we sent out on out-ch"))))

  (if (zero? (rand-int 2))
    (put! ch1 "Hello, ch1")
    (put! ch2 "Hello, ch2")))
;; What's async without multiple actors?:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/async/intro.org::*And,%20what's%20clojure%20without%20convenience?][And, what's clojure without convenience?:1]]
(let [ch1       (chan)
      ch2       (chan)
      out-ch    (chan 1)
      priority? false]                                      ;; Try variations

  (go (alt!
        ch1 ([val] (pprint "We had an input \"" val "\" on ch1"))
        ch2 ([val] (pprint "We had an input \"" val "\" on ch2"))
        ;; Caution - It's a vector of vectors below.
        [[out-ch "Out!"]] ([_] (pprint "Nothing came in. So, we sent out on out-ch"))
        :priority priority?))
  (put! ch1 "Hello, ch1"))
;; And, what's clojure without convenience?:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/async/intro.org::*Where%20do%20the%20async%20'processes'%20run?][Where do the async 'processes' run?:1]]
(thread (pprint "Hello"))

(go (pprint "Hello"))

(let [c (chan)]
  (go (pprint "In value: " (<! c)))
  (pprint "Well, let's do something. Send a value to the channel.")
  (go (>! c "HereIsAChannelMessage")))

(let [c (chan 8)]
  (put! c "Hello, put!"
        (fn [& args]
          (pprint "On put! callback"))
        false)
  (take! c (fn [& [args]] (pprint "On take! callback" args)) false))

(let [put-take-counter (atom 0)
      c                (chan (sliding-buffer 128))
      count++          (fn [] (swap! put-take-counter inc))]

  (def on-my-thread true)
  (def on-core-async-thread false)

  (defn put-cb [v]
    (pprint "put! = " v))
  (defn take-cb [x]
    (pprint "take! = " x))
  (defn put-counter! [on-thread?]
    (put! c (count++) put-cb on-thread?))
  (defn take-counter! [on-thread?]
    (take! c take-cb on-thread?))

  (comment
    ;; Mix the put-s and take-s. Letting one run ahead of the other.
    ;; Observe where (the thread) the prints happen.
    (put-counter! on-core-async-thread)
    (take-counter! on-core-async-thread)
    (take-counter! on-my-thread)
    (doseq [_ (range 1024)]
      (put-counter! on-core-async-thread))
    (doseq [_ (range 128)]
      (take-counter! on-my-thread))))

;; Where do the async 'processes' run?:1 ends here
