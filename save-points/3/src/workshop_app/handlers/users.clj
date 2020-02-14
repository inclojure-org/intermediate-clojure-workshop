(ns workshop-app.handlers.users)


(defn get-handler
  [{:keys [params] :as request}]
  (def r* request)
  (let [{:keys [name surname]} params]
    (if (and name surname)
      {:status 200
       :body   (str "Hello, "
                    name
                    " "
                    surname
                    "!!!")}
      {:status 400
       :body "Missing name and surname."})))