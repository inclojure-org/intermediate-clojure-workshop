(ns workshop-app.core
  (:gen-class)
  (:require [ring.adapter.jetty :as raj]
            [clojure.string :as s]
            [compojure.core :refer [defroutes GET POST PUT DELETE ANY]]))

;;; Task: Change the handler to say hello to the person whose
;;; name & surname is coming in from the request.
;;; Return a response like "Hello, <name> <surname>!!!"
;;; Query using the API to capture the request and inspect it
;;; and then write your code.
(defn get-handler
  [request]
  (def r* request)
  {:status 200
   :body "Hello world!!!"})


(defroutes app-routes
           (GET "/" _ get-handler)
           (ANY "*" _ {:status 404}))


(defn -main
  [& _]
  (raj/run-jetty app-routes
                 {:port 65535
                  :join? false}))