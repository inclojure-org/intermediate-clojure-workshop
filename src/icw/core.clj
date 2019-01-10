(ns icw.core
  (:require [icw.system :as system]
            [icw.data.process]
            [icw.data.process-faster]
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

'icw.data.process-faster
