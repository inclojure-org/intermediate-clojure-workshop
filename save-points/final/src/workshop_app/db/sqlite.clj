(ns workshop-app.db.sqlite
  (:refer-clojure :rename {update cc-update
                           read cc-read})
  (:import (java.sql DriverManager Connection PreparedStatement)))

;; Connection conn = DriverManager.getConnection("jdbc:sqlite:prod_database_1.sqlite");
(defn init-conn!
  [conn-string]
  (DriverManager/getConnection conn-string))

(def conn (init-conn! "jdbc:sqlite:prod_database_1.sqlite"))

;; Statement statement = conn.createStatement();
;; statement.executeUpdate("create table if not exists person(name string primary key, dob string)");
;; statement.close()
(defn create-table
  [c]
  (with-open [statement (.createStatement c)]
    (.executeUpdate statement "create table if not exists person(name string primary key, dob string)")))


;; String k, v;
;; Statement statement = conn.prepareStatement("insert into person values(?,?)");
;; statement.setString(1, k);
;; statement.setString(2, v);
;; statement.executeUpdate();
;; statement.close();
(defn create!
  [conn k v]
  (let [statement (.prepareStatement ^Connection conn "insert into person(name, dob) values (?,?)")]
    (.setString ^PreparedStatement statement 1 k)
    (.setString ^PreparedStatement statement 2 v)
    (.executeUpdate statement)
    (.close statement)))

;; String k, v;
;; Statement statement = conn.prepareStatement("insert into person values(?,?)");
;; statement.setString(1, k);
;; statement.setString(2, v);
;; statement.executeUpdate();
;; statement.close();
(defn update!
  [conn k v]
  (with-open [statement (doto (.prepareStatement ^Connection conn "update person set dob=? where name=?")
                          (.setString 2 k)
                          (.setString 1 v))]
    (.executeUpdate statement)))


;; String k;
;; Statement statement = conn.prepareStatement("delete from person where name=?");
;; statement.setString(1, k);
;; statement.executeUpdate();
;; statement.close();
(defn delete!
  [conn k]
  (with-open [statement (.prepareStatement ^Connection conn "delete from person where name=?")]
    (.setString ^PreparedStatement statement 1 k)
    (.executeUpdate statement)))


;; Statement statement = conn.prepareStatement("select name, dob from person where name=?");
;; statement.setString(1, k);
;; ResultSet rs = statement.executeQuery();
;; Result result = rs.next();
;; ResultSetMetadata rsm =  result.getMetaData();
;; int columnCount = rsm.getColumnCount();
(defn read
  [conn k]
  (with-open [statement (doto (.prepareStatement ^Connection conn "select name, dob from person where name=?")
                          (.setString 1 k))]
    (with-open [rs (.executeQuery statement)]
      (when (.next rs)
        (let [rs-meta (.getMetaData rs)
              column-count (.getColumnCount rs-meta)]
          (into {}
                (map (fn [idx] [(.getColumnLabel rs-meta (int idx))
                                (.getObject rs (int idx))])
                     (range 1 (inc column-count)))))))))