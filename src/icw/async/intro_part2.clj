;; [[file:~/github/intermediate-clojure-workshop/content/async/intro.org::*Preamble][Preamble:1]]
(ns icw.async.intro-part2
  (:require [icw.common :refer :all]
            [clojure.core.async :as a
             :refer [go go-loop chan close!
                     <! >! <!! >!! take! put!
                     alts! alt! thread
                     buffer sliding-buffer dropping-buffer]]))
;; Preamble:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/async/intro.org::*chan][chan:1]]
(chan)
;; chan:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/async/intro.org::*Put,%20and%20then%20take.][Put, and then take.:1]]
; A simple put, followed by a take
(let [ch (chan)]
  (put! ch "hello, orderly world!")
  (take! ch pprint))
;; Put, and then take.:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/async/intro.org::*Take,%20and%20put?][Take, and put?:1]]
(let [ch (chan)]

  ;; take!, and put!, are non-blocking operations
  (take! ch pprint)

  ; The above returned immediately,
  ; event though there was nothing to consume
  (pprint "Now, we put!")
  (put! ch "hello, world!")

  (take! ch pprint)
  (put! ch "hello, again, world!"))
;; Take, and put?:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/async/intro.org::*Pushing%20the%20limits%20-%20put%20and%20take][Pushing the limits - put and take:1]]
; How many puts can a channel take at once?
(let [num-msgs 1025
      ch (chan)]
  (doseq [i (range num-msgs)]
    (put! ch i))

  (doseq [i (range num-msgs)]
    (take! ch identity)))
;; Pushing the limits - put and take:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/async/intro.org::*Add%20a%20cushion][Add a cushion:1]]
(let [chan-buf-sz 32
      put-limit   1025
      take-limit  64
      ;; Try using any of buffer, dropping-buffer and sliding-buffer
      ch          (chan (buffer chan-buf-sz))]

  (doseq [i (range put-limit)]
    (put! ch i))

  (doseq [i (range take-limit)]
    (take! ch pprint)))
;; Add a cushion:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/async/intro.org::*Let's%20go!][Let's go!:1]]
(let [c (chan)]

  (go (pprint "We have a new message: " (<! c)))

  (pprint "Spending time doing nothing.")
  (Thread/sleep 2500)
  (pprint "Done with the siesta. Now, let's put a message on the channel")
  (go (>! c "Did you have to wait too long?")))
;; Let's go!:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/async/intro.org::*Going,%20going,%20go-loop!][Going, going, go-loop!:1]]
; Let's create a looping go process.
(defonce looping-ch-stop? (atom false))
(def looping-ch
  (let [ch (chan)]
    (go-loop [msg (<! ch)]
      (pprint "We have a new message - " msg)
      (when-not @looping-ch-stop?
        (pprint "Onto the next one! Until then, I sleep...")
        (recur (<! ch))))
    ch))
; Evaluate the next one as many times as you wish.
(put! looping-ch "hello")
;; Going, going, go-loop!:1 ends here

;; [[file:~/github/intermediate-clojure-workshop/content/async/intro.org::*What's%20async%20without%20multiple%20actors?][What's async without multiple actors?:1]]
; Let's work with multiple channels.
; In a real scenario, there will be multiple "processes"
; We communicate with processes via channels

(let [ch1       (chan)
      ch2       (chan)
      out-ch    (chan 1)]
  ; alts! - selects amongst a list of channels which are ready for action
  (go (let [[val port] (alts! [ch1 ch2 [out-ch "Out!"]] :priority true)]
        ; Which one gets picked when > 1 are ready?
        ; Note that the out channel is very likely ready for action,
        ;  since we control when the action happens.
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
