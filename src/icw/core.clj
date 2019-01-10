(ns icw.core
  (:require [icw.system :as system]
            [icw.data.process]
            [icw.data.faster-process]
            [icw.web.core :as web]))


;; Start HTTP server

(comment
  (system/start!))


;; Chapter 1 - Lazy sequences

;; http://localhost:6789/albums

;; Where is it located ? Let's jump there
web/app

;; Chapter 2 - Concurrency

;; Can we make make populate-db faster?

icw.data.faster-process
