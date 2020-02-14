(ns workshop-app.routes
  (:require [workshop-app.handlers.users :as wahu]
            [compojure.core :refer [defroutes GET POST PUT DELETE ANY]]))


(defroutes app-routes
           (GET "/" _ wahu/get-handler)
           (POST "/:name" {:keys [params]} (wahu/add-person params))
           (GET "/:name" [name] (wahu/get-person name))
           ;; Add POST and DELETE route for updating and deleting
           ;; the record respectively.
           #_(_ _ _ _)
           #_(_ _ _ _)
           (ANY "*" _ {:status 404}))