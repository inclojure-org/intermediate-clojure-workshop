(ns icw.system
  (:require [integrant.core :as ig]
            [icw.web.core :as web]
            [aleph.http :as http]))

(def config
  {:data/db {}
   :data/db.albums {}
   :data/lucene {}
   :services/streams.twitter {}
   :services/streams.facebook {}
   :services/streams.linkedin {}
   :http/server {:port 6789,
                 :handler (ig/ref :http/app)}
   :http/app {}})

(defmethod ig/init-key :data/db [_ _])
(defmethod ig/init-key :data/db.albums [_ _])
(defmethod ig/init-key :data/lucene [_ _])
(defmethod ig/init-key :services/streams.twitter [_ _])
(defmethod ig/init-key :services/streams.facebook [_ _])
(defmethod ig/init-key :services/streams.linkedin [_ _])
(defmethod ig/init-key :http/server [_ {:keys [handler] :as opts}]
  (http/start-server handler (dissoc opts :handler)))

(defmethod ig/init-key :http/app [_ _]
  #'web/app)

(defmethod ig/halt-key! :http/server [_ server]
  (.close server))

(defonce system (atom nil))

(defn start! []
  (if (nil? @system)
    (reset! system (ig/init config))
    (println "System already booted.")))

(defn stop! []
  (when (some? @system)
    (ig/halt! @system)
    (reset! system nil)))

(comment
  (start!))
