(def lucene-version "7.6.0")

(defproject intermediate-clojure-workshop "0.0.1-SNAPSHOT"

  :description "Clojure Workshop - Next Steps"
  :url "https://github.com/jaju/intermediate-clojure-workshop"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.8.1"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/core.async "0.4.490"]
                 [org.clojure/test.check "0.9.0"]

                 [integrant "0.7.0"]

                 [cheshire "5.8.1"]
                 [ring/ring-json "0.4.0"]
                 [compojure "1.6.1"]
                 [aleph "0.4.6"]

                 [org.clojure/data.csv "0.1.4"]
                 [org.clojure/java.jdbc "0.7.8"]
                 [org.hsqldb/hsqldb "2.4.1"]
                 [com.zaxxer/HikariCP "3.3.0"]

                 [org.apache.lucene/lucene-core ~lucene-version]
                 [org.apache.lucene/lucene-queryparser ~lucene-version]
                 [org.apache.lucene/lucene-analyzers-common ~lucene-version]]

  :profiles {:dev {:dependencies [[integrant/repl "0.3.1"]]}}
  :aot [icw.core])
