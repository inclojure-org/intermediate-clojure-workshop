(ns workshop-app.pure-fns
  (:require [clojure.test :refer :all]
            [workshop-app.handlers.users :as wahu]
            [workshop-app.middlewares.users :as wamu])
  (:import (java.time LocalDate)))


(deftest pure-get-person-test
  (is (= {:status 200
          :headers {"content-type" "application/json"}
          :body "{\"dob\":\"2000-01-01\",\"age\":20}"}
         #_(wahu/get-person "2000-01-01" (LocalDate/parse "2020-02-14")))
      "Is our pure get handler working as expected."))


(deftest pure-reject-uri-ending-with-slash-middleware-test
  (testing "Middleware should reject all uri's ending with /"
    (are [expected actual] (= expected actual)
                           {:uri "/"} _
                           {:status 400 :body "Bad request."} _
                           {:uri "/joel"} _)))

;; by using higher order functions you can also test behavior without
;; mutating any code.
(deftest pure-handle-any-exception
  (testing "Middleware should reject all uri's ending with /"
    (are [expected actual] (= expected actual)
                           {:status 500 :body "Internal server error."} ((wamu/handle-any-exception (fn [_] (throw (Exception.))))
                                                                         {})
                           {} ((wamu/handle-any-exception identity)
                               {}))))