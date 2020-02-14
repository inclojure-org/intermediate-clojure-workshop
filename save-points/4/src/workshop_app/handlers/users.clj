(ns workshop-app.handlers.users
  (:require [workshop-app.db.in-mem :as wadim]
            [cheshire.core :as json]))


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


(defn add-person
  [{:keys [name dob]}]
  (if (and name dob)
    (do (wadim/create! wadim/conn
                       name
                       dob)
        {:status 201
         :body   "Created user."})
    {:status 400
     :body   "User name or date of birth missing."}))


(defn get-person
  [name]
  (let [dob (wadim/read wadim/conn name)]
    {:status  200
     :headers {"content-type" "application/json"}
     :body    (when dob
                (json/generate-string {:dob dob}))}))
