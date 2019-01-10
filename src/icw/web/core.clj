(ns icw.web.core
  (:require [compojure.core :as c]
            [aleph.http :as http]
            [ring.middleware
             [keyword-params :refer [wrap-keyword-params]]
             [params :refer [wrap-params]]
             [json :refer [wrap-json-response]]]
            [icw.web.handlers.albums :as albums]
            [icw.search.core :as search]))

(defn wrap-body [response-body]
  {:status 200
   :body   response-body})

(def albums-context
  (c/context "/albums" [:as request]
    (c/GET "/" []
      (wrap-body
       (albums/list-albums)))
    (c/GET "/:id" [id]
      (wrap-body {:message (str "Here be your JSON of an album with id " id)}))))

(def search-context
  (c/context "/search" [:as request]
    (c/GET "/:term" [term]
      (let [{:keys [field]} (:params request)
            field (or field "album")]
        (or
          (search/search (keyword field) term)
          (wrap-body {:message (str "Looking for " term " in field " field ", eh?")}))))))

(c/defroutes app*
  albums-context
  search-context)

(def app
  (c/routes
    (-> #'app*
        wrap-json-response
        wrap-keyword-params
        wrap-params)))
;; Tying Together:1 ends here
