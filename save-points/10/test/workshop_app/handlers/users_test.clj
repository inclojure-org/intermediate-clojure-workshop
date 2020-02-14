(ns workshop-app.handlers.users-test
  (:require [clojure.test :refer :all]
            [workshop-app.handlers.users :as wahu]
            [workshop-app.db.sqlite :as wads]))


;; Pattern 1
;; Using fixtures to actually bootstrap and teardown.
;; Scenario: I want to test against a actual instance
;; but I want to test against a different database.
(use-fixtures :each (fn [t]
                      (let [conn (wads/init-conn! "jdbc:sqlite::memory:")]
                        (with-redefs [wads/conn conn]
                          (wads/create-table conn)
                          (t)))))


(deftest add-person-test
  (is (= {:status 201
          :body   "Created user."}
         (wahu/add-person {:name "Joel Victor"
                           :dob  "2001-01-01"}))
      "Is the creation handler working as expected."))


(deftest update-person-test
  (is (= {:status 200
          :body "Updated user."}
         (wahu/update-person {:name "Joel Victor"
                              :dob "2000-01-01"}))
      "Is the update handler working as expected."))


(deftest delete-person-test
  (is (= {:status 200
          :body "Deleted user."}
         (wahu/delete-person "Joel Victor"))
      "Is the deletion handler working as expected."))