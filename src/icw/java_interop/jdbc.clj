(ns icw.java-interop.jdbc
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.java.jdbc :as jdbc])
  (:import [com.zaxxer.hikari HikariConfig HikariDataSource]
           [com.zaxxer.hikari.pool HikariPool]))

(def hsqldb {:dbtype "hsqldb"
             :dbname "albums"})
(def albums-drop-table-ddl
  (jdbc/drop-table-ddl :albums {:conditional? true}))

(def albums-table-create-ddl
  (jdbc/create-table-ddl
   :albums
   [[:number :int "PRIMARY KEY"]
    [:year :int]
    [:album "varchar(128)"]
    [:artist "varchar(128)"]
    [:genre "varchar(128)"]
    [:subgenre "varchar(128)"]]
   {:conditional? true}))

(defn db-drop-albums-table! [db-spec]
  (jdbc/db-do-commands
   db-spec
   [albums-drop-table-ddl]))

(defn db-create-albums-table! [db-spec]
  (jdbc/db-do-commands
   db-spec
   [albums-table-create-ddl
    "CREATE INDEX year_idx ON albums (year)"]))

(defn db-load! [db-spec table-name data]
  (jdbc/insert-multi! db-spec
                      table-name
                      data))

(defn init-db
  []
  (db-drop-albums-table! hsqldb)
  (db-create-albums-table! hsqldb))

(defn insert!
  [row]
  (Thread/sleep (rand-int 20))
  (jdbc/insert! hsqldb
                :albums
                row))

(defn list-albums
  []
  (jdbc/query hsqldb
              ["SELECT * from albums;"]))


(do (db-drop-albums-table! hsqldb)
    (db-create-albums-table! hsqldb))

(comment
  (jdbc/query hsqldb ["SELECT count(*) from albums limit 1"])

  (defonce hc (HikariConfig.))
  (doto hc
    (.setJdbcUrl "jdbc:hsqldb:albums"))
  (defonce hds (HikariDataSource. hc))
  (let [conn (.getConnection hds)
        stmt (.createStatement conn)
        res (.executeQuery stmt "SELECT * from albums limit 5")]
    (jdbc/result-set-seq res))

  (let [conn (.getConnection hds)
        prepped-stmt (jdbc/prepare-statement conn "SELECT count(*) from albums")]
    (jdbc/query conn [prepped-stmt])))
