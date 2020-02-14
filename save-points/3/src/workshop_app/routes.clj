(ns workshop-app.routes
  (:require [workshop-app.handlers.users :as wahu]
            [compojure.core :refer [defroutes GET POST PUT DELETE ANY]]))


(defroutes app-routes
           (GET "/" _ wahu/get-handler)
           (ANY "*" _ {:status 404}))