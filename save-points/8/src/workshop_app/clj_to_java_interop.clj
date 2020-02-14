(ns workshop-app.clj-to-java-interop)

(gen-class :name org.inclojure.Demo
           :init init
           :prefix "-"
           :state "state"
           :methods [[getName [] String]
                     [setName [String] void]])


(defn -init []
  "State a hash map."
  [[] (atom {})])


(defn -getName
  [this]
  (:name @(.state this)))


(defn -setName
  [this name]
  (swap! (.state this) assoc :name name))