(ns icw.async.chapter1
  (:require [clojure.core.async :refer
             [go chan <! >! >!! <!! alts!! close! put! take! timeout]]
            [icw.common :refer [pprint]]))

;; Core async's most basic construct is channel
;; It has two operations on it put! and take!

(comment
  (let [ch (chan)]
    (put! ch "hello, orderly world!")
    (take! ch pprint)))


;; What can you do with it?

;; One process talking to other

;; Process 1 -----> chan ----> Process 2

;; In context of `go`, put is `>!` and take is `<!`
(comment
  (let [c (chan)]
    ;; process #1
    ;; push 1 to channel
    (go (>! c 1))


    ;; process #2
    ;; get something from channel
    (go (pprint "Hello from process #2 " (<! c)))))



;; It can interoperate between a thread and go block

(comment
  (let [c (chan)]
    ;; process #1
    (future (pprint "Fetching data from Mongodb")
            (Thread/sleep 400)
            (>!! c {:data "hello world"})
            (pprint "Fetching data from Mongodb completed"))

    ;; process #2
    (go (pprint "Data from mongodb #2 " (<! c)))))

;; Process 1 ---
;;              \
;;               ----- chan --- Process 3
;;              /
;; Process 2 ---

(comment
  (let [c1 (chan)
        c2 (chan)]

    ;; process #1
    (future (let [time (rand-int 1000)]
              (Thread/sleep time)
              (>!! c1 {:data (str "hello world from process 1 time " time)})))

    ;; process #2
    (future (let [time (rand-int 1000)]
              (Thread/sleep time)
              (>!! c2 {:data (str "hello world from process 2 time " time)})))

    ;; process #3 how do we get output of the fastest result
    (pprint (<!! c1))
    (pprint (<!! c2))))


;; Alts!!
(comment
  (let [c1 (chan)
        c2 (chan)]

    ;; process #1
    (future (let [time (rand-int 1000)]
              (Thread/sleep time)
              (>!! c1 {:data (str "hello world from process 1 time " time)})
              (pprint "released process 1!")))

    ;; process #2
    (future (let [time (rand-int 1000)]
              (Thread/sleep time)
              (>!! c2 {:data (str "hello world from process 2 time " time)})
              (pprint "released process 2!")))

    ;; process #3
    (let [[v c] (alts!! [c1 c2 t])]
      (pprint v))))




;; Exercise - 1
;; Modify alts! example. If both requests take more than 300msec print
;; "Wubalubadubdub" and close both request channels
;; Hint - timeout generates a channel which will close after given time
;; On closing the channel it gets nil as the final value
