(ns workshop-app.db.mongo)


(defonce r-db (atom {:foo :bar}))

(defn fetch
  [k]
  (Thread/sleep (rand-nth [100 300]))
  (clojure.core/get @r-db k))

(defn set
  [k v]
  (Thread/sleep (rand-nth [ 100 300]))
  (swap! r-db assoc k v))
