(ns workshop-app.intro-to-testing
  (:require [clojure.test :refer :all]))


;; Defining a test
(deftest test-name-1
  (is (= 1 1)
      "Optional message for assertion")

  (are [x y] (= x y)
             2 (+ 1 1)
             3 (+ 1 2)))


(deftest test-name-2
  (testing "Msg to add context to all the test run inside this"
    (is (= 1 2)
        "One should not equal 2."))

  (is (thrown? RuntimeException ((fn [] (throw (RuntimeException.)))))))