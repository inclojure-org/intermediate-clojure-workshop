(defproject inclojure/workshop-app "0.1.0"
            :dependencies [[org.clojure/clojure "1.10.1"]
                           [ring/ring "1.8.0"]
                           [compojure "1.6.1"]
                           [cheshire "5.9.0"]
                           [org.xerial/sqlite-jdbc "3.30.1"]]
            :main workshop-app.core
            :aot [workshop-app.clj-to-java-interop]
            :global-vars {*warn-on-reflection* true}
            :source-paths ["1/src" ;; simple hello world printer
                           "2/src" ;; hello world server and ring introduction
                           "3/src" ;; hello name server filled and middleware intro
                           "4/src" ;; middlewares solved with compojure intro
                           "5/src" ;; interop
                           "6/src" ;; interop+ with jdbc
                           "7/src" ;; introduction to testing
                           "8/src" ;; property based testing, vars & namespaces, clj to java interop
                           "9/src" ;; laziness solved with concurrency introduction
                           "10/src" ;; concurrency solution
                           "final/src"]
            :test-paths ["7/test" ;; introduction to testing
                         "8/test" ;; property based testing, vars & namespaces, clj to java interop
                         "9/test" ;; laziness solved with concurrency introduction
                         "10/test" ;; concurrency solution
                         "final/test"]
            :profiles {:test {:dependencies [[org.clojure/test.check "0.10.0"]]}}
            :local-repo ".local-m2")