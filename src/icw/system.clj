;; [[file:~/github/intermediate-clojure-workshop/content/system.org::*System][System:1]]
(ns icw.system
  (:require [integrant.core :as ig]
            [icw.data :as data]
            [icw.search.core :as search]
            [icw.web.core :as web]
            [aleph.http :as http]
            [icw.data]))

(def config
  {:data/db.albums {}
   :data/db {:album-data (ig/ref :data/db.albums)}
   :data/lucene {:album-data (ig/ref :data/db.albums)}
   :services/streams.twitter {}
   :services/streams.facebook {}
   :services/streams.linkedin {}
   :http/server {:port 6789, :handler (ig/ref :http/app)}
   :http/app {}})

(defmethod ig/init-key :data/db.albums [_ _]
  (data/load-album-csv-file data/album-csv-file))

(defmethod ig/init-key :data/db [_ _])

(defmethod ig/init-key :data/lucene [_ {:keys [album-data]}]
  (search/init! album-data #{:album :artist :genre :subgenre}))

(defmethod ig/init-key :services/streams.twitter [_ _])

(defmethod ig/init-key :services/streams.facebook [_ _])

(defmethod ig/init-key :services/streams.linkedin [_ _])

(defmethod ig/init-key :http/server [_ {:keys [handler] :as opts}]
  (http/start-server handler (dissoc opts :handler)))
(defmethod ig/halt-key! :http/server [_ server]
  (.close server))

(defmethod ig/init-key :http/app [_ _]
  #'web/app)

(defonce system (atom nil))

(defn start! []
  (if (nil? @system)
    (do
      (reset! system (ig/init config))
      nil)
    (println "System already booted.")))

(defn stop! []
  (when (some? @system)
    (ig/halt! @system)
    (reset! system nil)))

(comment
  (start!))
;; System:1 ends here
