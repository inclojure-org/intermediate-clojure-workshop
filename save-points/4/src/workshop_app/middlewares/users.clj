(ns workshop-app.middlewares.users
  (:require [clojure.string :as s]))


(defn reject-uri-ending-with-slash
  [handler]
  (fn [{:keys [uri] :as request}]
    (if (and (not= uri "/")
             (s/ends-with? uri "/"))
      {:status 400
       :body   "Bad request."}
      (handler request))))


(defn handle-any-exception
  [handler]
  (fn [request]
    (try (handler request)
         (catch Exception e
           (.printStackTrace e)
           {:status 500
            :body "Internal server error."}))))