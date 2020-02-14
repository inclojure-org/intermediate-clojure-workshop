(ns workshop-app.routes
  (:require [workshop-app.handlers.users :as wahu]
            [compojure.core :refer [defroutes GET POST PUT DELETE ANY]]))


(defroutes app-routes
           (GET "/" _ wahu/get-handler)
           (POST "/:name" {:keys [params]} (wahu/add-person params))
           (GET "/:name" [name] (wahu/get-person name))
           (PUT "/:name" {:keys [params]} (wahu/update-person params))
           (DELETE "/:name" [name] (wahu/delete-person name))
           (ANY "*" _ {:status 404}))