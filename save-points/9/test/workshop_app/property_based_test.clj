(ns workshop-app.property-based-test
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :as tcct]
            [clojure.test.check.generators :as ctcg]
            [clojure.test.check.properties :as ctcp]
            [workshop-app.utils :as wau])
  (:import (java.time LocalDate)))


(def date-tuple-generator
  (ctcg/let [month (ctcg/choose 1 12)]
            (case month
              2 (ctcg/tuple (ctcg/choose 1900 2021) (ctcg/return month) (ctcg/choose 1 28))
              (1 3 5 7 8 10 12) (ctcg/tuple (ctcg/choose 1900 2021) (ctcg/return month) (ctcg/choose 1 31))
              (4 6 9 11) (ctcg/tuple (ctcg/choose 1900 2021) (ctcg/return month) (ctcg/choose 1 30)))))

(def date-object-generator
  (ctcg/fmap (fn [[y m d]]
               (LocalDate/of y m d))
             date-tuple-generator))

(def age-property-fn (some-fn pos? zero?))

(def age-property
  (ctcp/for-all [st-dt date-object-generator
                 end-dt date-object-generator]
                (age-property-fn (wau/years-between st-dt end-dt))))

#_(tc/quick-check 1000 age-property)


(tcct/defspec age-property-2 100
              (ctcp/for-all [st-dt date-object-generator
                             end-dt date-object-generator]
                            (age-property-fn (wau/years-between st-dt end-dt))))