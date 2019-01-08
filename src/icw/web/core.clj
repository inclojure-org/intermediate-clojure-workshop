(ns icw.web.core
  (:require [compojure.core :as c]
            [aleph.http :as http]
            [ring.middleware
             [keyword-params :refer [wrap-keyword-params]]
             [params :refer [wrap-params]]
             [json :refer [wrap-json-response]]]

            [icw.web.handlers.albums :as albums]))

(defn- wrap-body [handler]
  (fn [request]
    (let [result (handler request)]
      {:status 200
       :body result})))


(c/defroutes app*
  (c/context "/albums" [:as request]
             (c/GET "/" []
                    )
             (c/GET "/:id" [id]
                    {:message (str "Here be your JSON of an album with id " id)}))
  (c/context "/search" [:as request]
             (c/GET "/:term" [term]
                    {:message (str "Looking for " term ", eh?")})))

(def app
  (c/routes
   (-> #'app*
       wrap-body
       wrap-json-response
       wrap-keyword-params
       wrap-params)))
