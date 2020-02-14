(ns workshop-app.vars)

;; this is a symbol
'a

;; to create a var we need to simply do
(def a)

#_(+ a 10)

#_(alter-var-root #'a (constantly 20))

#_(println a)

#_(println (var-get #'a))

(def ^:dynamic b)

#_(+ b 20)

(binding [b 10]
  (println (+ b 10)))