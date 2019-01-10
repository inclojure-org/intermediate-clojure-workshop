(ns icw.core
  (:require [icw.system :as system]
            [icw.data.process]
            [icw.data.process-faster]
            ;; [icw.java-interop.intro]
            ;; [icw.search.reader]
            [icw.async.intro]
            [icw.async.rlsc]
            [icw.web.core :as web])
  (:gen-class))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Introduction
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Start HTTP server

(comment
  (system/start!))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Chapter 1 - Lazy sequences
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; http://localhost:6789/albums

;; Where is it located ? Let's jump there
web/app

icw.data.process/populate-db

;; git checkout solutions src/

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Chapter 2 - Concurrency
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Can we make make populate-db faster?

'icw.data.process-faster


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Chapter 3 - Java interop
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; 'icw.java-interop.intro


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Chapter 4 - Java interop (search)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Go to
;; http://localhost:6789/search/beatles

;; Let's go back to the routes
web/app

;; We need fix search
;; icw.search.reader/search

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Chapter 5 - core.async introduction
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Let's write some core.async

'icw.async.intro

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Chapter 6 - core.async exercise
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

'icw.async.rlsc

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Summary
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; How to choose libraries and build projects ???

;; Start from smallest unit of data
;; Build transformations based on your case
;; Deal with constraints
;; Bring in databases and dependencies at very last


;; When choosing libraries
;; It should work with immutable data structure
;; It should produce immutable data structure
;; It should produce lazy sequences when it can
;; Prefer libraries over frameworks
;; It shouldn't use dynamic vars

;; Do concurrency when you actually have to
;; Adding concurrency primitives does add complexity to the system
;; When in doubt go for the simplest construct
