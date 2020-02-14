(ns workshop-app.db.redis)

(defonce r-db (atom {}))

(defn fetch
  [k]
  (Thread/sleep (rand-nth [20 20 20 20 20 20 20 300]))
  (clojure.core/get @r-db k))

(defn set
  [k v]
  (Thread/sleep (rand-nth [20 20 20 20 20 20 20 300]))
  (swap! r-db assoc k v))
