(ns icw.web.handlers.albums
  (:require [icw.java-interop.jdbc :as jdbc]))

(defn list-albums
  []
  (let [d (jdbc/list-albums)]
    (if (seq d)
      {:albums (jdbc/list-albums)}
      {:message "Data is missing. Please populate the database."})))
